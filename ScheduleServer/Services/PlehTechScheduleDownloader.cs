using Microsoft.AspNetCore.WebUtilities;
using Microsoft.Extensions.Logging;
using ScheduleServer.Models;
using System.Globalization;
using System.Net.Http.Headers;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace ScheduleServer.Services;

public class PlehTechScheduleDownloader
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<PlehTechScheduleDownloader> _logger;

    private const string ApiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYW5vbiIsImlzcyI6InN1cGFiYXNlIiwiaWF0IjoxNzgxNDM2NjY5LCJleHAiOjIwOTY3OTY2Njl9.uVBH4rCUVsarTmNqhiKBw8kvLqKygVvs31fkWW0C6u0";
    private const string BaseUrlSchedule = "https://pleh.tech/rest/v1/rpc/group_week";
    private const string BaseUrlGroups = "https://pleh.tech/rest/v1/groups_current";

    public PlehTechScheduleDownloader(HttpClient httpClient, ILogger<PlehTechScheduleDownloader> logger)
    {
        _httpClient = httpClient;
        _logger = logger;

        _httpClient.DefaultRequestHeaders.Add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        _httpClient.DefaultRequestHeaders.Add("X-Requested-With", "XMLHttpRequest");
        _httpClient.DefaultRequestHeaders.Add("apikey", ApiKey);
        _httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", ApiKey);
        _httpClient.DefaultRequestHeaders.Add("Accept-Profile", "api");
    }

    public async Task<IEnumerable<Lesson>?> GetScheduleAsync(string groupGuid, DateTime dateFrom, DateTime dateTo)
    {
        string fromStr = dateFrom.ToString("yyyy-MM-dd");
        string toStr = dateTo.ToString("yyyy-MM-dd");

        var queryParams = new Dictionary<string, string?>
        {
            { "p_group_guid", groupGuid },
            { "p_from", fromStr },
            { "p_to", toStr },
            { "order", "day.asc,period.asc" },
            { "select", "day,period,discipline,workload_type,room,building,campus,teacher_name,teacher_names,instructor_names,instructor_departments,subgroup,approved" }
        };

        var requestUri = QueryHelpers.AddQueryString(BaseUrlSchedule, queryParams);

        try
        {
            HttpResponseMessage response = await _httpClient.GetAsync(requestUri);

            if (!response.IsSuccessStatusCode)
            {
                string errorBody = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"Ошибка API: {response.StatusCode}. Body: {errorBody}");
            }

            string json = await response.Content.ReadAsStringAsync();
            var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };

            return JsonSerializer.Deserialize<IEnumerable<ScheduleItem>>(json, options)?.Select(e => new Lesson
            {
                Discipline = e.Discipline,
                WorkloadType = e.WorkloadType,
                Location = $"{e.Building} {e.Room} {e.Campus}",
                Teacher = e.TeacherName,
                LessonStart = DateTime.ParseExact(e.Day, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.AssumeUniversal | DateTimeStyles.AdjustToUniversal).AddMinutes(StartEnd(e.Period).start),
                LessonEnd = DateTime.ParseExact(e.Day, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.AssumeUniversal | DateTimeStyles.AdjustToUniversal).AddMinutes(StartEnd(e.Period).end),
                GroupId = Guid.Parse(groupGuid)
            });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Произошла ошибка при получении расписания для группы {GroupGuid}", groupGuid);
            throw;
        }
    }

    public async Task<List<Group>> GetAllGroupsAsync()
    {
        var pageSize = 1000;
        var allGroups = new List<Group>();
        int offset = 0;
        bool hasMore = true;

        while (hasMore)
        {
            var queryParams = new Dictionary<string, string?>
            {
                { "select", "group_guid,group_name,faculty,program_type,course" },
                { "order", "group_name.asc" },
                { "offset", offset.ToString() },
                { "limit", pageSize.ToString() }
            };

            var requestUri = QueryHelpers.AddQueryString(BaseUrlGroups, queryParams);

            using var request = new HttpRequestMessage(HttpMethod.Get, requestUri);
            request.Headers.Add("Prefer", "count=exact");

            HttpResponseMessage response = await _httpClient.SendAsync(request);

            if (!response.IsSuccessStatusCode)
            {
                throw new HttpRequestException($"Ошибка при получении групп: {response.StatusCode}");
            }

            string json = await response.Content.ReadAsStringAsync();
            var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
            var pageGroups = JsonSerializer.Deserialize<List<Group>>(json, options);

            if (pageGroups != null && pageGroups.Any())
            {
                allGroups.AddRange(pageGroups);

                bool rangeHeaderParsed = false;
                if (response.Headers.TryGetValues("Content-Range", out var ranges))
                {
                    var rangeHeader = ranges.FirstOrDefault();
                    if (!string.IsNullOrEmpty(rangeHeader) && rangeHeader.Contains("/"))
                    {
                        string[] parts = rangeHeader.Split('/');
                        if (parts.Length == 2 && parts[1] != "*")
                        {
                            int totalItems = int.Parse(parts[1]);
                            hasMore = allGroups.Count < totalItems;
                            rangeHeaderParsed = true;
                        }
                    }
                }

                if (!rangeHeaderParsed)
                {
                    hasMore = pageGroups.Count == pageSize;
                }

                offset += pageSize;
            }
            else
            {
                hasMore = false;
            }
        }

        return allGroups;
    }

    private class ScheduleItem
    {
        public string Day { get; set; } = string.Empty;
        public int Period { get; set; }
        public string Discipline { get; set; } = string.Empty;
        [JsonPropertyName("workload_type")]
        public string WorkloadType { get; set; } = string.Empty;
        public string Room { get; set; } = string.Empty;
        public string Building { get; set; } = string.Empty;
        public string Campus { get; set; } = string.Empty;
        [JsonPropertyName("teacher_name")]
        public string TeacherName { get; set; } = string.Empty;
        public List<string>? TeacherNames { get; set; }
        public List<string>? InstructorNames { get; set; }
        public List<string>? InstructorDepartments { get; set; }
        public string? Subgroup { get; set; }
        public bool? Approved { get; set; }
    }

    private static (int start, int end) StartEnd(int period) => period switch
    {
        1 => (start: 08 * 60 + 30, end: 10 * 60 + 00),
        2 => (start: 10 * 60 + 10, end: 11 * 60 + 40),
        3 => (start: 11 * 60 + 50, end: 13 * 60 + 20),
        4 => (start: 14 * 60 + 00, end: 15 * 60 + 30),
        5 => (start: 15 * 60 + 40, end: 17 * 60 + 10),
        6 => (start: 17 * 60 + 20, end: 18 * 60 + 50),
        _ => throw new NotImplementedException(),
    };
}
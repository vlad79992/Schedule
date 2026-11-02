using System.Text.Json;

namespace ScheduleServer
{
    internal class RaspReaScheduleDownloader
    {
        private static readonly HttpClient HttpClient = new HttpClient();

        static RaspReaScheduleDownloader()
        {
            HttpClient.DefaultRequestHeaders.Add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
            HttpClient.DefaultRequestHeaders.Add("X-Requested-With", "XMLHttpRequest");
        }

        /// <summary>
        /// Получает идентификатор файла расписания с сервера.
        /// </summary>
        /// <param name="group">Название группы (например, "15.27д-ивт01/24б")</param>
        /// <param name="week">Номер недели</param>
        /// <param name="mode">Режим (например, "week")</param>
        /// <param name="mask">Маска (обычно 0)</param>
        /// <returns>Уникальный идентификатор файла (GUID)</returns>
        public static async Task<string?> GetFileIdAsync(string group, int week = 8, string mode = "week", int mask = 0)
        {
            if (string.IsNullOrWhiteSpace(group))
                throw new ArgumentException("Название группы не может быть пустым.", nameof(group));

            string encodedGroup = Uri.EscapeDataString(group);
            string url = $"https://rasp.rea.ru/Schedule/ExportCalendar?key={encodedGroup}&week={week}&mode={mode}&mask={mask}";
            string referer = $"https://rasp.rea.ru/?q={encodedGroup}";

            // Устанавливаем Referer для этого запроса
            using var request = new HttpRequestMessage(HttpMethod.Get, url);
            request.Headers.Referrer = new Uri(referer);

            HttpResponseMessage response = await HttpClient.SendAsync(request);
            response.EnsureSuccessStatusCode();

            string json = await response.Content.ReadAsStringAsync();
            using JsonDocument doc = JsonDocument.Parse(json);
            return doc.RootElement.GetProperty("fileId").GetString();
        }

        /// <summary>
        /// Скачивает .ics-файл по fileId и сохраняет его на диск.
        /// </summary>
        /// <param name="fileId">Идентификатор файла, полученный через GetFileIdAsync</param>
        /// <param name="outputPath">Путь для сохранения файла (например, "schedule.ics")</param>
        /// <param name="group">Название группы (для формирования Referer)</param>
        public static async Task<string> DownloadIcsFileAsync(string fileId, string group)
        {
            if (string.IsNullOrWhiteSpace(fileId))
                throw new ArgumentException("fileId не может быть пустым.", nameof(fileId));
            if (string.IsNullOrWhiteSpace(group))
                throw new ArgumentException("Название группы не может быть пустым.", nameof(group));

            string encodedGroup = Uri.EscapeDataString(group);
            string url = $"https://rasp.rea.ru/Schedule/GetFile?id={fileId}";
            string referer = $"https://rasp.rea.ru/?q={encodedGroup}";

            using var request = new HttpRequestMessage(HttpMethod.Get, url);
            request.Headers.Referrer = new Uri(referer);

            HttpResponseMessage response = await HttpClient.SendAsync(request);
            response.EnsureSuccessStatusCode();

            return await response.Content.ReadAsStringAsync();
        }
    }
}

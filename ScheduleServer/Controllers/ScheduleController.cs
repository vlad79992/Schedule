using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ScheduleServer.Models;
using ScheduleServer.Services;
using System.Globalization;

namespace ScheduleServer.Controllers;

[ApiController]
[Route("[controller]")]
public class ScheduleController(ScheduleContext context, PlehTechScheduleDownloader downloader) : ControllerBase
{
    private readonly ScheduleContext context = context;
    private readonly PlehTechScheduleDownloader downloader = downloader;

    [HttpGet("schedule/{groupName}")]
    public async Task<ActionResult<IEnumerable<Lesson>>> GetSchedule(string groupName, [FromQuery] DateOnly? date = null)
    {
        groupName = groupName.Replace("%2F", "/");
        var group = await context.Groups
            .FirstOrDefaultAsync(g => g.Name.ToLower() == groupName.ToLower());

        if (group == null)
        {
            return NotFound($"Группа '{groupName}' не найдена");
        }


        var requestedDate = date?.ToDateTime(new()) ?? DateTime.Now.Date;
        //DateTime requestedDate = string.IsNullOrEmpty(date)
        //    ? DateTime.UtcNow.Date
        //    : DateTime.SpecifyKind(DateTime.ParseExact(date, "yyyy-MM-dd", CultureInfo.InvariantCulture), DateTimeKind.Utc);

        int daysSinceMonday = ((int)requestedDate.DayOfWeek - (int)DayOfWeek.Monday + 7) % 7;
        DateTime startOfWeekDateTime = DateTime.SpecifyKind(requestedDate.AddDays(-daysSinceMonday), DateTimeKind.Utc);
        DateTime endOfWeekDateTime = DateTime.SpecifyKind(startOfWeekDateTime.AddDays(7).AddTicks(-1), DateTimeKind.Utc);

        DateOnly weekStart = DateOnly.FromDateTime(startOfWeekDateTime);

        var weekUpdate = await context.Updates
            .FirstOrDefaultAsync(u => u.GroupId == group.Guid && u.WeekStart == weekStart);

        bool isCacheValid = weekUpdate?.LastUpdate?.Date == DateTime.UtcNow.Date;

        if (!isCacheValid)
        {
            var remoteLessons = await downloader.GetScheduleAsync(
                group.Guid.ToString(),
                startOfWeekDateTime,
                endOfWeekDateTime);

            if (remoteLessons == null || !remoteLessons.Any())
            {
                return NotFound("Не удалось получить расписание с сервера");
            }

            var oldLessons = await context.Lessons
                .Where(l => l.GroupId == group.Guid
                         && l.LessonStart >= startOfWeekDateTime
                         && l.LessonStart <= endOfWeekDateTime)
                .ToListAsync();

            if (oldLessons.Any())
            {
                context.Lessons.RemoveRange(oldLessons);
            }

            context.Lessons.AddRange(remoteLessons);

            if (weekUpdate == null)
            {
                context.Updates.Add(new Update
                {
                    GroupId = group.Guid,
                    WeekStart = weekStart,
                    LastUpdate = DateTime.UtcNow
                });
            }
            else
            {
                weekUpdate.LastUpdate = DateTime.UtcNow;
            }

            await context.SaveChangesAsync();
        }

        var lessons = await context.Lessons
            .Where(l => l.GroupId == group.Guid
                     && l.LessonStart >= startOfWeekDateTime
                     && l.LessonStart <= endOfWeekDateTime)
            .OrderBy(l => l.LessonStart)
            .ToListAsync();

        return Ok(lessons);
    }

    [HttpGet("groups")]
    public async Task<ActionResult<IEnumerable<Group>>> GetGroups()
    {
        if (context.Groups.Count() == 0)
        {
            return NotFound("Не удалось получить группы с сервера");
        }
        
        return Ok(context.Groups);
    }
}

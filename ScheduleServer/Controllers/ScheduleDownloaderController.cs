using Ical.Net;
using Microsoft.AspNetCore.Mvc;
using ScheduleServer.Models;
using ScheduleServer.Services;
using System.Globalization;
namespace ScheduleServer.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ScheduleDownloaderController : ControllerBase
    {
        [HttpGet("rasp-rea/{group}", Name = "GetScheduleFromRaspRea")]
        async public Task<IEnumerable<Lesson>?> GetFromRaspRea(string group)
        {
            try
            {
                //string fileId = await ScheduleDownloader.GetFileIdAsync(group, week: 8);
                //var content = await ScheduleDownloader.DownloadIcsFileAsync(fileId, group);
                var calendar = Ical.Net.Calendar.Load("""
                                        BEGIN:VCALENDAR
                    PRODID:-//github.com/rianjs/ical.net//NONSGML ical.net 4.0//EN
                    VERSION:2.0
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Системное программирование\n8 корпус - 507
                     (Основная)\nОбязательные дисциплины\, Практическое занятие
                    DTEND:20261019T100000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261019T083000
                    LOCATION:8 корпус - 507 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Системное программирование
                    UID:c11c3d1d-03e6-4f41-9849-344fd627812f
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Основы работы с Большими данными\n3 корпус
                     - 451 (Основная)\nОбязательные дисциплины\, Практическое занятие
                    DTEND:20261019T114000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261019T101000
                    LOCATION:3 корпус - 451 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Основы работы с Большими данными
                    UID:0d0f7d7e-a1f1-47ef-9d06-73fe59740c19
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Иностранный язык профессионального общения\
                     n3 корпус - 411 (Основная)\nОбязательные дисциплины\, Практическое заняти
                     е
                    DTEND:20261020T132000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261020T115000
                    LOCATION:3 корпус - 411 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Иностранный язык профессионального общения
                    UID:65424f91-285c-4dcc-a4f1-5e08763e787b
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Элективные дисциплины по физической культур
                     е и спорту\n6 корпус - с/з №6 (Основная)\nОбязательные дисциплины\, Практ
                     ическое занятие
                    DTEND:20261020T153000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261020T140000
                    LOCATION:6 корпус - с/з №6 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Элективные дисциплины по физической культуре и
                     спорту
                    UID:47b3c7d8-6042-49fd-8241-83e766bc1264
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Проектный практикум "Разработка приложений
                     в среде системы управления базами данных"\n3 корпус - 409 (Основная)\nОбя
                     зательные дисциплины\, Практическое занятие
                    DTEND:20261020T171000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261020T154000
                    LOCATION:3 корпус - 409 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Проектный практикум "Разработка приложений в ср
                     еде системы управления базами данных"
                    UID:fcb64082-5c0a-42ec-ae27-36c5065a6867
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Компьютерная графика\n8 корпус - 512 (Основ
                     ная)\nОбязательные дисциплины\, Практическое занятие
                    DTEND:20261020T185000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261020T172000
                    LOCATION:8 корпус - 512 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Компьютерная графика
                    UID:59e6ef8d-a3ca-4dfd-ab03-5588dbb87b8a
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Экономика фирмы\n3 корпус - 525 (Основная)\
                     nБазовая (общепрофессиональная) часть\, Зачет (пересдача)
                    DTEND:20261022T114000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261022T101000
                    LOCATION:3 корпус - 525 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Экономика фирмы
                    UID:cbbdcd46-789c-417d-a87a-41f56feed38d
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Программирование и администрирование в сред
                     е системы управления базами данных\n3 корпус - 455 (Основная)\nОбязательн
                     ые дисциплины\, Практическое занятие
                    DTEND:20261022T171000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261022T154000
                    LOCATION:3 корпус - 455 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Программирование и администрирование в среде си
                     стемы управления базами данных
                    UID:6cb39847-0850-4c2c-b8c7-1604a4d44020
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Технологии разработки программного обеспече
                     ния\n3 корпус - 457 (Основная)\nОбязательные дисциплины\, Практическое за
                     нятие
                    DTEND:20261022T185000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261022T172000
                    LOCATION:3 корпус - 457 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Технологии разработки программного обеспечения
                    UID:3dfb78d3-a9ad-44a6-8029-a048d826cf17
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Технологии разработки программного обеспече
                     ния\n3 корпус - 404 (Основная)\nОбязательные дисциплины\, Лекция
                    DTEND:20261023T171000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261023T154000
                    LOCATION:3 корпус - 404 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Технологии разработки программного обеспечения
                    UID:8a67359b-2a87-42ab-8c76-62faa5246e2f
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Проектный практикум "Разработка приложений
                     в среде системы управления базами данных"\n3 корпус - 404 (Основная)\nОбя
                     зательные дисциплины\, Лекция
                    DTEND:20261023T185000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261023T172000
                    LOCATION:3 корпус - 404 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Проектный практикум "Разработка приложений в ср
                     еде системы управления базами данных"
                    UID:a5a2aa74-87d2-4286-924e-e1eebd1d86ae
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Компьютерная графика\n3 корпус - 404 (Основ
                     ная)\nОбязательные дисциплины\, Лекция
                    DTEND:20261024T153000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261024T140000
                    LOCATION:3 корпус - 404 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Компьютерная графика
                    UID:643c0547-c3a6-47dd-a1eb-56424530b0fa
                    END:VEVENT
                    BEGIN:VEVENT
                    DESCRIPTION:15.27Д-ИВТ01/24б - Программирование и администрирование в сред
                     е системы управления базами данных\n3 корпус - 404 (Основная)\nОбязательн
                     ые дисциплины\, Лекция
                    DTEND:20261024T171000
                    DTSTAMP:20261027T221454Z
                    DTSTART:20261024T154000
                    LOCATION:3 корпус - 404 (Основная)
                    SEQUENCE:0
                    SUMMARY:15.27Д-ИВТ01/24б - Программирование и администрирование в среде си
                     стемы управления базами данных
                    UID:e4a04cca-26da-427a-a069-a97bd676e80a
                    END:VEVENT
                    END:VCALENDAR
                    """.Trim());

                return calendar?.Events.Select(e => new Lesson
                {
                    Name = e.Summary!,
                    Location = e.Location,
                    Description = e.Description,
                    Teacher = null,
                    LessonStart = e.Start?.AsUtc,
                    LessonEnd = e.End?.AsUtc
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
                return null;
            }
        }

        [HttpGet("pleh-tech/{group}", Name = "GetScheduleFromPlehTech")]
        async public Task<IEnumerable<Lesson>?> GetFromPlehTech(string group)
        {
            return null;
            //try
            //{
            //    var groups = await PlehTechScheduleDownloader.GetAllGroupsAsync();
            //    var groupId = groups.FirstOrDefault(g => g.GroupName == group.Replace("%2F", "/"))?.GroupGuid;
            //    if (groupId is null) return null;
            //    return await PlehTechScheduleDownloader.GetScheduleAsync(
            //        groupId,
            //        DateTime.ParseExact("2026-10-26", "yyyy-MM-dd", CultureInfo.InvariantCulture),
            //        DateTime.ParseExact("2026-11-01", "yyyy-MM-dd", CultureInfo.InvariantCulture));
            //}
            //catch (Exception ex)
            //{
            //    Console.WriteLine(ex.ToString());
            //    return null;
            //}
        }

        [HttpGet("pleh-tech/all-groups", Name = "GetAllGroupsFromPlehTech")]
        async public Task<IEnumerable<Group>?> GetGroupsAsync()
        {
            try
            {
                return await PlehTechScheduleDownloader.GetAllGroupsAsync();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
                return null;
            }
        }
    }
}

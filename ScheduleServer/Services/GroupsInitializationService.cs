using Microsoft.EntityFrameworkCore;
using ScheduleServer.Models;

namespace ScheduleServer.Services;

internal class GroupsInitializationService : BackgroundService
{
    private readonly IServiceScopeFactory scopeFactory;

    public GroupsInitializationService(IServiceScopeFactory scopeFactory)
    {
        this.scopeFactory = scopeFactory;
    }
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        using var scope = scopeFactory.CreateScope();
        var downloader = scope.ServiceProvider.GetRequiredService<PlehTechScheduleDownloader>();
        var dbContext = scope.ServiceProvider.GetRequiredService<ScheduleContext>();

        var remoteGroups = await downloader.GetAllGroupsAsync();

        var remoteGuids = remoteGroups.Select(g => g.Guid).ToList();

        var existingGroups = await dbContext.Groups
            .Where(g => remoteGuids.Contains(g.Guid))
            .ToListAsync(stoppingToken);

        var existingDict = existingGroups.ToDictionary(g => g.Guid);

        var toAdd = new List<Group>();

        foreach (var remote in remoteGroups)
        {
            if (existingDict.TryGetValue(remote.Guid, out var existing))
            {
                existing.Name = remote.Name;
                existing.Faculty = remote.Faculty;
                existing.ProgramType = remote.ProgramType;
                existing.Course = remote.Course;
            }
            else
            {
                toAdd.Add(remote);
            }
        }

        if (toAdd.Any())
        {
            dbContext.Groups.AddRange(toAdd);
        }

        await dbContext.SaveChangesAsync(stoppingToken);
    }
}
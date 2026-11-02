using Microsoft.EntityFrameworkCore;
using ScheduleServer.Models;

namespace ScheduleServer;

public class ScheduleContext(DbContextOptions<ScheduleContext> options) : DbContext(options)
{
    public DbSet<Group>  Groups  { get; set; }
    public DbSet<Lesson> Lessons { get; set; }
    public DbSet<Update> Updates { get; set; }
}

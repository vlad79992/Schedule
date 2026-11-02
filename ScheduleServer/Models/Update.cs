namespace ScheduleServer.Models;

using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

[Table("updates")]
public class Update
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Column("group_id")]
    public Guid? GroupId { get; set; }

    [ForeignKey(nameof(GroupId))]
    [JsonIgnore]
    public virtual Group? Group { get; set; }

    [Column("last_update")]
    public DateTime? LastUpdate { get; set; }

    [Column("week_start")]
    public DateOnly? WeekStart { get; set; }
}

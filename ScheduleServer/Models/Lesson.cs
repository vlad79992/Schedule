namespace ScheduleServer.Models;

using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

[Table("lessons")]
public class Lesson
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Column("discipline")]
    public required string Discipline { get; set; }
    
    [Column("workload_type")]
    public string? WorkloadType { get; set; }
    
    [Column("location")]
    public string? Location { get; set; }

    [Column("teacher")]
    public string? Teacher { get; set; }

    [Column("lesson_start")]
    public DateTime? LessonStart { get; set; }

    [Column("lesson_end")]
    public DateTime? LessonEnd { get; set; }

    [JsonIgnore]
    [Column("group_id")]
    public Guid? GroupId { get; set; }

    [JsonIgnore]
    [ForeignKey(nameof(GroupId))]
    public virtual Group? Group { get; set; }
}

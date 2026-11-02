namespace ScheduleServer.Models;

using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

[Table("groups")]
public class Group
{
    [Key]
    [Column("guid")]
    [JsonPropertyName("group_guid")]
    public Guid Guid { get; set; }

    [Required]
    [MaxLength(100)]
    [Column("name")]
    [JsonPropertyName("group_name")]
    public string Name { get; set; } = null!;

    [MaxLength(255)]
    [Column("faculty")]
    [JsonPropertyName("faculty")]
    public string? Faculty { get; set; }

    [MaxLength(50)]
    [Column("program_type")]
    [JsonPropertyName("program_type")]
    public string? ProgramType { get; set; }

    [MaxLength(50)]
    [Column("course")]
    [JsonPropertyName("course")]
    public string? Course { get; set; }

    [JsonIgnore]
    public virtual ICollection<Lesson> Lessons { get; set; } = new List<Lesson>();
    [JsonIgnore]
    public virtual ICollection<Update> Updates { get; set; } = new List<Update>();
}

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using ScheduleServer;
using ScheduleServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace ScheduleServer.Pages_Lessons
{
    public class IndexModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public IndexModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public IList<Lesson> Lesson { get;set; } = default!;
        public SelectList GroupsSelectList { get; set; } = default!;

        public async Task OnGetAsync(Guid? groupId)
        {
            var groups = await _context.Groups.OrderBy(g => g.Name).ToListAsync();
            GroupsSelectList = new SelectList(groups, "Guid", "Name", groupId);

            var query = _context.Lessons.Include(l => l.Group).AsQueryable();

            if (groupId.HasValue)
            {
                query = query.Where(l => l.GroupId == groupId.Value);
            }

            Lesson = await query.ToListAsync();
        }
    }
}

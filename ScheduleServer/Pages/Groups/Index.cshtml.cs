using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using ScheduleServer;
using ScheduleServer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace ScheduleServer.Pages_Groups
{
    public class IndexModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public IndexModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public IList<Models.Group> Group { get;set; } = default!;

        [BindProperty(SupportsGet = true)]
        public string? SearchString { get; set; }

        public async Task OnGetAsync(string? searchString)
        {
            SearchString = searchString;

            var groupsQuery = _context.Groups.AsQueryable();

            if (!string.IsNullOrEmpty(searchString))
            {
                groupsQuery = groupsQuery.Where(g =>
                    g.Name.Contains(searchString) ||
                    (g.Faculty != null && g.Faculty.Contains(searchString))
                );
            }

            Group = await groupsQuery.ToListAsync();
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using ScheduleServer;
using ScheduleServer.Models;

namespace ScheduleServer.Pages_Updates
{
    public class IndexModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public IndexModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public IList<Update> Update { get;set; } = default!;

        public async Task OnGetAsync()
        {
            Update = await _context.Updates
                .Include(u => u.Group).ToListAsync();
        }
    }
}

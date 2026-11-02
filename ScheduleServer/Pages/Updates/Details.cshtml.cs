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
    public class DetailsModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public DetailsModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public Update Update { get; set; } = default!;

        public async Task<IActionResult> OnGetAsync(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var update = await _context.Updates.FirstOrDefaultAsync(m => m.Id == id);

            if (update is not null)
            {
                Update = update;

                return Page();
            }

            return NotFound();
        }
    }
}

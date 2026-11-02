using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using ScheduleServer;
using ScheduleServer.Models;

namespace ScheduleServer.Pages_Groups
{
    public class DetailsModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public DetailsModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public Group Group { get; set; } = default!;

        public async Task<IActionResult> OnGetAsync(Guid? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var group = await _context.Groups.FirstOrDefaultAsync(m => m.Guid == id);

            if (group is not null)
            {
                Group = group;

                return Page();
            }

            return NotFound();
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using ScheduleServer;
using ScheduleServer.Models;

namespace ScheduleServer.Pages_Lessons
{
    public class DetailsModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public DetailsModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public Lesson Lesson { get; set; } = default!;

        public async Task<IActionResult> OnGetAsync(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var lesson = await _context.Lessons.FirstOrDefaultAsync(m => m.Id == id);

            if (lesson is not null)
            {
                Lesson = lesson;

                return Page();
            }

            return NotFound();
        }
    }
}

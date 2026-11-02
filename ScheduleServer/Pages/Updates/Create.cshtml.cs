using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.AspNetCore.Mvc.Rendering;
using ScheduleServer;
using ScheduleServer.Models;

namespace ScheduleServer.Pages_Updates
{
    public class CreateModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public CreateModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        public IActionResult OnGet()
        {
        ViewData["GroupId"] = new SelectList(_context.Groups, "Guid", "Name");
            return Page();
        }

        [BindProperty]
        public Update Update { get; set; } = default!;

        // For more information, see https://aka.ms/RazorPagesCRUD.
        public async Task<IActionResult> OnPostAsync()
        {
            if (!ModelState.IsValid)
            {
                return Page();
            }

            _context.Updates.Add(Update);
            await _context.SaveChangesAsync();

            return RedirectToPage("./Index");
        }
    }
}

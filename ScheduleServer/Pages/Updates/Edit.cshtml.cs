using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using ScheduleServer;
using ScheduleServer.Models;

namespace ScheduleServer.Pages_Updates
{
    public class EditModel : PageModel
    {
        private readonly ScheduleServer.ScheduleContext _context;

        public EditModel(ScheduleServer.ScheduleContext context)
        {
            _context = context;
        }

        [BindProperty]
        public Update Update { get; set; } = default!;

        public async Task<IActionResult> OnGetAsync(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var update =  await _context.Updates.FirstOrDefaultAsync(m => m.Id == id);
            if (update == null)
            {
                return NotFound();
            }
            Update = update;
           ViewData["GroupId"] = new SelectList(_context.Groups, "Guid", "Name");
            return Page();
        }

        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more information, see https://aka.ms/RazorPagesCRUD.
        public async Task<IActionResult> OnPostAsync()
        {
            if (!ModelState.IsValid)
            {
                return Page();
            }

            _context.Attach(Update).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UpdateExists(Update.Id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return RedirectToPage("./Index");
        }

        private bool UpdateExists(int id)
        {
            return _context.Updates.Any(e => e.Id == id);
        }
    }
}

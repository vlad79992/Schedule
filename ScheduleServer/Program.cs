using Microsoft.EntityFrameworkCore;
using Scalar.AspNetCore;
using ScheduleServer;
using ScheduleServer.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddRazorPages();

builder.Services.AddDbContextPool<ScheduleContext>(opt =>
    opt.UseNpgsql(builder.Configuration.GetConnectionString("ScheduleContext")));

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

builder.Services.AddHttpClient<PlehTechScheduleDownloader>();
builder.Services.AddHostedService<GroupsInitializationService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
//if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.MapScalarApiReference();
}

app.UseStaticFiles();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.MapRazorPages();

app.Run();
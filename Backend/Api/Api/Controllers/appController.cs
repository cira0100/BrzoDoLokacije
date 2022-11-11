using Microsoft.AspNetCore.Authorization;
using System.Data;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Api.Interfaces;
using Api.Services;

namespace Api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class appController : ControllerBase
    {
        private readonly IConfiguration _configuration;
        public appController(IConfiguration configuration) { 
            _configuration = configuration;
        }

        [HttpPost("download")]
        public async Task<ActionResult> getApp([FromForm] string password)
        {
            string appPath = _configuration.GetSection("AppSettings:AppName").Value;
            string truePassword = _configuration.GetSection("AppSettings:AppPassword").Value;
            if (appPath == null || !System.IO.File.Exists(appPath))
                return BadRequest("Aplikacija ne postoji");
            
            if (password == null || !UserService.checkPassword(password,truePassword))
                return BadRequest("Pogresna sifra");
            return File(System.IO.File.ReadAllBytes(appPath), "application/octet-stream", Path.GetFileName(appPath));
        }
        [HttpGet("download")]
        public async Task<ActionResult<string>> getForm()
        {

                var html = await System.IO.File.ReadAllTextAsync(@"./Assets/appDownload.html");
                return base.Content(html, "text/html");
            
        }
    }

}


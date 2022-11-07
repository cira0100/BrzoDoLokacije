using Microsoft.AspNetCore.Authorization;
using System.Data;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

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

        [HttpGet("download")]
        public async Task<ActionResult> getApp()
        {
            string appPath = _configuration.GetSection("AppSettings:AppName").Value;
            if (appPath == null || !System.IO.File.Exists(appPath))
                return BadRequest("Aplikacija ne postoji");
            return File(System.IO.File.ReadAllBytes(appPath), "application/octet-stream", Path.GetFileName(appPath));
        }

    }
}

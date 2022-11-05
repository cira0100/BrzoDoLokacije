using Api.Interfaces;
using Api.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [Route("api/auth/")]
    public class AuthController : Controller
    {
        private readonly IUserService _userService;
        private readonly IJwtService _jwtService;
        public AuthController(IUserService userService,IJwtService jwtService)
        {
            _userService = userService;
            _jwtService = jwtService;
        }

        [HttpPost("registerdeprecated")]
        public async Task<ActionResult<string>> Register([FromBody] Register creds)
        {
            //this is beyond scuffed and will be cleaned up later, when users,login and controllers are made
            User novi = new User();
            novi.email = creds.email;
            novi.password = creds.password;
            novi.username = creds.username;
            novi.name = creds.name;
            novi.verified = true;
            novi.creationDate = DateTime.Now.ToUniversalTime();
            novi._id = "";

            int ret= await _userService.createUser(novi);
            if (ret == -1)
                return BadRequest("email already exists");
            if (ret == -2)
                return BadRequest("username already exists");

            return Ok();
        }
        [HttpPost("refreshJwt")]
        [Authorize(Roles ="User")]
        public async Task<ActionResult<string>> refreshJwt()
        {
            var jwt = await _userService.RenewToken();
            if (jwt != null)
            {
                return Ok(jwt);
            }
            return BadRequest("Pogresno uneti podaci");
        }
        [HttpPost("login")]
        public async Task<ActionResult<string>> Login([FromBody] Login creds)
        {
            var id = await _userService.UserIdFromJwt();
            if (id != null) return Forbid();

            var jwt= await _userService.Login(creds);
            if (jwt != null)
            {
                return Ok(jwt);
            }
            return BadRequest("Pogresno uneti podaci");
        }
        [HttpPost("register")]
        public async Task<ActionResult<string>> RegisterActual([FromBody] Register creds)
        {
            var msg = await _userService.Register(creds);
            switch (msg)
            {
                case "User Registered":
                    return Ok(msg);
                default:
                    return BadRequest(msg);
            }
        }
        [HttpPost("verify")]
        public async Task<ActionResult<string>> VerifyEmail([FromBody] VerifyUser creds)
        {
            var vrfchk = new Login();
            vrfchk.email = creds.email;
            vrfchk.password = creds.password;
            if (await _userService.CheckVerification(vrfchk))
                return Ok("User already verified");
            var uspeh = await _userService.VerifyUser(creds);
            if (!uspeh)
                return BadRequest("Kod netacan ili istekao");
            return Ok("Uspesno verifikovan");
        }
        [HttpPost("resetpass")]
        public async Task<ActionResult<string>> ResetPass([FromBody] ResetPass creds)
        {
            var uspeh = await _userService.ResetPassword(creds);
            if (!uspeh)
                return BadRequest("Kod netacan ili istekao");
            return Ok("Sifra uspesno resetovana");
        }
        [HttpPost("forgotpass")]
        public async Task<ActionResult<string>> ForgotPass([FromBody] JustMail justMail)
        {
            if (await _userService.ForgotPassword(justMail))
                return Ok("Email poslat");
            return BadRequest("Email nema registrovan nalog");
        }
        [HttpGet("verifytoken/{token}")]
        public async Task<ActionResult<string>> VerifyEmailToken(string token)
        {
            var username =_jwtService.EmailTokenToClaim(token,"username");
            string html;
            if (username == null)
            {
                html = await System.IO.File.ReadAllTextAsync(@"./Assets/VerifyFailed.html");
                return base.Content(html, "text/html");
            }
            else
            {
                html = await System.IO.File.ReadAllTextAsync(@"./Assets/VerifySuccess.html");
                html = html.Replace("{{name}}", username);

                await _userService.VerifyFromToken(token);
                return base.Content(html, "text/html");
            }
        }
    }
}

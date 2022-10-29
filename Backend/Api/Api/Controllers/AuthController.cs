using Api.Interfaces;
using Api.Models;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [Route("api/auth/")]
    public class AuthController : Controller
    {
        private readonly IUserService _userService;
        public AuthController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost("register")]
        public async Task<ActionResult<string>> Register([FromBody] Register creds)
        {
            //this is beyond scuffed and will be cleaned up later, when users,login and controllers are made
            User novi = new User();
            novi.email = creds.email;
            novi.password = creds.password;
            novi.username = creds.username;
            novi.name = creds.name;
            novi.creationDate = DateTime.Now.ToUniversalTime();
            novi._id = "";

            int ret= await _userService.createUser(novi);
            if (ret == -1)
                return BadRequest("email already exists");
            if (ret == -2)
                return BadRequest("username already exists");

            return Ok();
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
    }
}

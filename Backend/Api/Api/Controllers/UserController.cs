using Api.Interfaces;
using Api.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [Route("api/user/")]
    public class UserController : Controller
    {
        private readonly IUserService _userService;
        private readonly IJwtService _jwtService;
        private readonly IPostService _postService;
        public UserController(IUserService userService, IJwtService jwtService, IPostService postService)
        {
            _userService = userService;
            _jwtService = jwtService;
            _postService = postService;
        }
        [HttpPost("profile/pfp")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<User>> setPfp([FromForm]IFormFile image)
        {
            var id = await _userService.UserIdFromJwt();
            if(await _userService.AddOrChangePfp(id,image))
                return Ok();
            return BadRequest();
        }
        [HttpGet("profile")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<UserSend>> SelfProfile()
        {
            var id = await _userService.UserIdFromJwt();
            var rez = await _userService.GetSelfUserData(id);
            if (rez != null)
                return Ok(rez);
            return BadRequest();
        }
        [HttpGet("{username}/profile")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<UserSend>> GetProfile(string username)
        {
            var rez =  await _userService.GetUserData(username);
            if (rez != null)
                return Ok(rez);
            return BadRequest();
        }
        [HttpGet("posts")]
        [Authorize(Roles = "User")] 
        public async Task<ActionResult<List<PostSend>>> SelfPosts()
        {
            var id = await _userService.UserIdFromJwt();
            var rez = await _postService.GetUsersPosts(id);
            if (rez != null)
                return Ok(rez);
            return BadRequest();
        }
        [HttpGet("{id}/profile")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<UserSend>> GetUserById(string id)
        {
            var rez = await _userService.getUserById(id);
            if (rez != null)
                return Ok(rez);
            return BadRequest();
        }
        [HttpGet("history")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> ViewHistory()
        {
            var id = await _userService.UserIdFromJwt();
            var rez = await _postService.UserHistory(id);
            if (rez != null)
                return Ok(rez);
            return BadRequest();
        }
    }
}

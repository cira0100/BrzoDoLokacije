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
        [HttpGet("{id}/id/profile")]
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

        [HttpGet("{id}/followers")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<UserSend>>> GetFollowers(string id)
        {
            return Ok(await _userService.GetFollowers(id));
        }

        [HttpGet("{id}/following")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<UserSend>>> GetFollowing(string id)
        {
            return Ok(await _userService.GetFollowing(id));
        }

        [HttpGet("{id}/addFollower")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<Boolean>> AddFollower(string id)
        {
            return Ok(await _userService.AddFollower(id));
        }

        [HttpGet("{id}/myFollowings")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<UserSend>>> GetMyFollowings()
        {
            return Ok(await _userService.GetMyFollowings());
        }

        [HttpGet("{id}/checkIfAlreadyFollow")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<Boolean>> CheckIfAlreadyFollow(String id)
        {
            return Ok(await _userService.CheckIfAlreadyFollow(id));
        }

        [HttpGet("{id}/unfollow")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<Boolean>> Unfollow(string id)
        {
            return Ok(await _userService.Unfollow(id));
        }

        [HttpGet("{id}/myFollowers")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<UserSend>>> GetMyFollowers()
        {
            return Ok(await _userService.GetMyFollowers());
        }

        [HttpGet("profile/stats")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<UserStats>> SelfStats()
        {
            var id = await _userService.UserIdFromJwt();
            var tosend = await _postService.UserStats(id);
            if (tosend != null)
                return Ok(tosend);
            return BadRequest();
        }
        [HttpGet("{username}/profile/stats")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<UserStats>> GetStats(string username)
        {
            var rez = await _userService.GetUserData(username);
            if (rez == null)
                return BadRequest();
            var tosend = await _postService.UserStats(rez._id);
            if (tosend != null)
                return Ok(tosend);
            return BadRequest();
        }

        [HttpGet("{newUsername}/profile/changeMyUsername")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<int>> ChangeMyProfileUsername(string newUsername)
        {
            return await _userService.ChangeMyProfileUsername(newUsername);
        }


        [HttpGet("{id}/changeMyName")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<bool>> ChangeMyProfileName(string newName)
        {
            return Ok(await _userService.ChangeMyProfileName(newName));
        }



    }
}

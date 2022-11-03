using Api.Interfaces;
using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PostController : ControllerBase
    {
        private readonly IPostService _postService;
        public PostController(IPostService postService)
        {
            _postService = postService;
        }

        [HttpPost("add")]
        [Authorize(Roles ="User")]
        public async Task<ActionResult<PostSend>> addPost([FromBody] PostReceive post)
        {
            var res = await _postService.addPost(post);
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }
        [HttpGet]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> getAllPosts()
        {
            var res = await _postService.getAllPosts();
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }
        [HttpGet("posts /{id}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<PostSend>> getPostByid(string id)
        {
            var res = await _postService.getPostById(id);
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }

    }
}

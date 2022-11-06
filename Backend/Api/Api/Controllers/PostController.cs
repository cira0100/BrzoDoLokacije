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
        private readonly IFileService _fileService;
        public PostController(IPostService postService, IFileService fileService)
        {
            _postService = postService;
            _fileService = fileService;
        }

        [HttpPost("add")]
        [Authorize(Roles ="User")]
        public async Task<ActionResult<PostSend>> addPost([FromForm]PostReceive post)
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

        [HttpGet("image/{id}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult> getImage(string id)
        {
            Models.File f =await _fileService.getById(id);
            if (f == null || !System.IO.File.Exists(f.path))
                return BadRequest("Slika ne postoji");
            return File(System.IO.File.ReadAllBytes(f.path), "image/*", Path.GetFileName(f.path));


        }


    }
}

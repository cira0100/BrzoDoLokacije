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
        private readonly IUserService _userService;
        public PostController(IPostService postService, IFileService fileService,IUserService userService)
        {
            _postService = postService;
            _fileService = fileService;
            _userService = userService;
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
        [HttpGet("posts/{id}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<PostSend>> getPostByid(string id)
        {
            var userid = await _userService.UserIdFromJwt();
            var res = await _postService.getPostById(id,userid);
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }
        [HttpDelete("posts/delete/{id}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<string>> deletePost(string id)
        {
            var userid = await _userService.UserIdFromJwt();
            var res = await _postService.deletePost(id, userid);
            if (res)
            {
                return Ok(res);
            }
            return BadRequest("Post ne postoji ili vi niste vlasnik");
        }
        [HttpGet("image/{id}")]
        //[Authorize(Roles = "User")]
        public async Task<ActionResult> getImage(string id)
        {
            Models.File f =await _fileService.getById(id);
            if (f == null || !System.IO.File.Exists(f.path))
                return BadRequest("Slika ne postoji");
            return File(System.IO.File.ReadAllBytes(f.path), "image/*", Path.GetFileName(f.path));
        }
        [HttpGet("image/compress/{id}")]
        //[Authorize(Roles = "User")]
        public async Task<ActionResult> getCompressedImage(string id)
        {
            Byte[] f = await _fileService.getCompressedImage(id);
            if (f == null)
                return BadRequest("Slika ne postoji");
            return File(f, "image/*", "tempcompress");
        }

        [HttpPost("posts/{id}/addrating")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult> addRating([FromBody] RatingReceive rating,string id)
        {
            var userid = await _userService.UserIdFromJwt();
            var rez = await _postService.AddOrReplaceRating(rating, userid);
            if(rez != null)
                return Ok(rez);
            return BadRequest();
        }

        [HttpDelete("posts/{id}/removerating")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<int>> removeRating(string id)
        {
            var userid = await _userService.UserIdFromJwt();
            if (await _postService.RemoveRating(id,userid))
                return Ok();
            return BadRequest();
        }

        [HttpPost("posts/{id}/addcomment")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<CommentSend>> addComment([FromBody] CommentReceive cmnt,string id)
        {
            var userid = await _userService.UserIdFromJwt();
            var c = await _postService.AddComment(cmnt, userid, id);
            if (c != null)
                return Ok(c);
            return BadRequest();
        }

        [HttpGet("posts/{id}/listcomments")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<CommentSend>>> listComments(string id)
        {
            var ret = await _postService.ListComments(id);
            if(ret != null)
                return Ok(ret);
            return BadRequest();
        }

        [HttpDelete("posts/{id}/removecomment/{cmntid}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult> removeRating(string id,string cmntid)
        {
            var userid = await _userService.UserIdFromJwt();
            if (await _postService.DeleteComments(id,cmntid,userid))
                return Ok();
            return BadRequest();
        }
        [HttpGet("locations/{id}/posts")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> searchPosts(string id,int page=0,int sorttype=1,int  filterdate=1)
        {
            var res = await _postService.SearchPosts(id,page,sorttype,filterdate);
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }

        [HttpGet("posts/get10MostViewed")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> Get10MostViewed()
        {
            return Ok(await _postService.Get10MostViewed());
        } 
        
        [HttpGet("posts/get10Newest")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> Get10Newest()
        {
            return Ok(await _postService.Get10Newest());
        }
        
        [HttpGet("posts/get10Best")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> Get10Best()
        {
            return Ok(await _postService.Get10Best());
        }


        [HttpGet("posts/{id}/getUserPosts")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> GetUsersPosts(string id)
        {
            return Ok(await _postService.GetUsersPosts(id));
        }

        [HttpGet("posts/recommended")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> Recommended()
        {
            var userid = await _userService.UserIdFromJwt();
            return Ok(await _postService.Recommended(userid));
        }
        [HttpGet("favourite/{id}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<bool>> addRemoveFavourite(string id)
        {
            return Ok(await _postService.addRemoveFavourite(id));
        }

        [HttpGet("trending")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<Trending>>> Trending()
        {
            return Ok(await _postService.TrendingTags());
        }
        [HttpGet("userFavouritePosts")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<PostSend>>> getUserFavouritePosts()
        {
            return Ok(await _postService.userFavouritePosts());
        }

    }
}

using Api.Interfaces;
using Api.Models;
using Microsoft.AspNetCore.Authorization;
using System.Data;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LocationController : ControllerBase
    {
        private readonly ILocationService _locationService;
        private readonly IPostService _postService;
        public LocationController(ILocationService locationService, IPostService postService)
        {
            _locationService = locationService;
            _postService = postService;
        }

        [HttpPost("add")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<Location>> addPost([FromBody] Location loc)
        {
            var res = await _locationService.add(loc);
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }
        [HttpGet]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<Location>>> getAllPosts()
        {
            var res = await _locationService.getAllLocation();
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }
        [HttpGet("loc /{id}")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<Location>> getLocationByid(string id)
        {
            var res = await _locationService.getById(id);
            if (res != null)
            {
                return Ok(res);
            }
            return BadRequest();
        }

        [HttpGet("search")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<Location>>> searchLocation(int searchtype ,string? query,double? latitude,double? longitude)
        {
            Coords coords = new Coords();
            if (latitude!=null && longitude!=null) {
                coords.latitude = (double)latitude;
                coords.longitude = (double)longitude;
            }
            List<Location> ret = new List<Location>();
            switch (searchtype)
            {
                case 1:
                    ret = await _locationService.SearchLocation(query);
                    return Ok(ret);
                case 2:
                    ret = await _locationService.SearchLocation(coords);
                    return Ok(ret);
                default:
                    ret = await _locationService.SearchLocation(query);
                    return Ok(ret);
            }
        }

        [HttpGet("searchradius")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<Location>>> bestPostsForLocationInRadius(double latitude, double longitude, double radius)
        {
            Coords coords = new Coords();
            if (latitude != null && longitude != null)
            {
                coords.latitude = (double)latitude;
                coords.longitude = (double)longitude;
            }
            List<PostSend> ret = new List<PostSend>();
            ret = await _postService.BestPostForAllLocationsInRadius(coords, radius);
            if (ret != null && ret.Count > 0)
                return Ok(ret);
            return BadRequest();
            
        }
    }
}

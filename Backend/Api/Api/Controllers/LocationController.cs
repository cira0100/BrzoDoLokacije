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
        public LocationController(ILocationService locationService)
        {
            _locationService = locationService;
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

        [HttpPost("search")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<Location>>> searchLocation(int searchtype ,string? query,Coords? coords)
        {
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
    }
}

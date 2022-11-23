using System.Data;
using Api.Interfaces;
using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MessageController : ControllerBase
    {

        private readonly IJwtService _jwtService;
        private readonly IMessageService _messageService;
        public MessageController( IJwtService jwtService,IMessageService messageService)
        {

            _jwtService = jwtService;
            _messageService = messageService;

        }
        [HttpPost("add")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<Message>> addMessage([FromBody] MessageReceive msg)
        {
            var msgTemp = await _messageService.addMessage(msg);
            if(msgTemp == null)
                return BadRequest();
            return Ok(msgTemp);
        }
        [HttpGet("myMessages")]
        [Authorize(Roles = "User")]
        public async Task<ActionResult<List<MessageSend>>> getMyMessages()
        {
            var msgTemp = await _messageService.getReceiverMessage();
            if (msgTemp == null)
                return BadRequest();
            return Ok(msgTemp);
        }
    }
}

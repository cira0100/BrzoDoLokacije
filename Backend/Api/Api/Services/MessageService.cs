using System.Security.Claims;
using Api.Interfaces;
using Api.Models;
using Microsoft.AspNetCore.SignalR;
using MongoDB.Driver;

namespace Api.Services
{
    public class MessageService : IMessageService
    {
        private readonly IHttpContextAccessor _httpContext;
        private readonly IMongoCollection<Message> _messages;
        private readonly IUserService _userService;
        private readonly IJwtService _jwtService;
        private IConfiguration _configuration;
        private readonly IHubContext<ChatHub> _chatHub;
        public MessageService(IDatabaseConnection settings, IMongoClient mongoClient, IUserService userService, IJwtService jwtService, IHttpContextAccessor httpContextAccessor, IConfiguration configuration,IHubContext<ChatHub> chatHub)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _messages = database.GetCollection<Message>(settings.MessageCollectionname);
            _userService = userService;
            _jwtService = jwtService;
            _httpContext = httpContextAccessor;
            _configuration = configuration;
            _chatHub = chatHub;
        }
        public async Task<Message> addMessage(MessageReceive msg)
        {
            if (_httpContext.HttpContext.User.FindFirstValue("id") == null)
                return null;
            var senderId = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
            if (senderId == null)
                return null;
            var receiverCheck =await  _userService.GetSelfUserData(msg.receiverId);
            if (receiverCheck == null)
                return null;
            var tempMsg = new Message();
            tempMsg._id = "";
            tempMsg.receiverId = msg.receiverId;
            tempMsg.senderId = senderId;
            tempMsg.messagge = msg.messagge;
            tempMsg.timestamp = DateTime.Now.ToUniversalTime();
            if (ChatHub.CheckUser(msg.receiverId))
            {
                //user online
                var temp =messageToMessageSend(tempMsg); 
                foreach (var connection in ChatHub.getAllConnectionsOfUser(msg.receiverId))
                    await _chatHub.Clients.Client(connection).SendAsync("Message",temp);

            }
            else
            {
                //user offline
                await _messages.InsertOneAsync(tempMsg);

            }
            

            return tempMsg;
        }
        public MessageSend messageToMessageSend(Message msg)
        {
            var tempMsg = new MessageSend();
            tempMsg.senderId = msg.senderId;
            tempMsg.messagge = msg.messagge;
            tempMsg.timestamp = msg.timestamp;
            return tempMsg;
        }
        public async Task<List<MessageSend>> getReceiverMessage()
        {
            var receiverId = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
            if (receiverId == null)
                return null;
            var messages = await _messages.Find(msg => msg.receiverId == receiverId).ToListAsync();
            var tempMessages = new List<MessageSend>();
            foreach (var message in messages)
            {
                tempMessages.Add(messageToMessageSend(message));
                _messages.DeleteOne(msg => msg._id == message._id);
            }
            return tempMessages;
        }

    }
}

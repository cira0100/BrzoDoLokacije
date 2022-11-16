using System.Security.Claims;
using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;

namespace Api.Services
{
    public class MessageService
    {
        private readonly IHttpContextAccessor _httpContext;
        private readonly IMongoCollection<Message> _messages;
        private readonly IJwtService _jwtService;
        private IConfiguration _configuration;
        private readonly IFileService _fileService;
        public MessageService(IDatabaseConnection settings, IMongoClient mongoClient, IJwtService jwtService, IHttpContextAccessor httpContextAccessor, IConfiguration configuration)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _messages= database.GetCollection<Message>(settings.UserCollectionName);
            _jwtService = jwtService;
        }
        public async Task<Message> addMessage(MessageReceive msg)
        {
            var senderId = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
            if (senderId == null)
                return null;
            var tempMsg = new Message();
            tempMsg._id = "";
            tempMsg.receiverId = msg.receiverId;
            tempMsg.senderId = senderId;
            tempMsg.messagge = msg.messagge;
            tempMsg.timestamp= DateTime.Now.ToUniversalTime();

            await _messages.InsertOneAsync(tempMsg);

            return tempMsg;
        }
        public MessageSend messageToMessageSend(Message msg)
        {
            var tempMsg=new MessageSend();
            tempMsg.senderId = msg.senderId;
            tempMsg.messagge=msg.messagge;
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
                _messages.DeleteOne(msg=>msg._id==message._id);
            }
            return tempMessages;
        }

    }
}

using Api.Models;

namespace Api.Interfaces
{
    public interface IMessageService
    {
        Task<Message> addMessage(MessageReceive msg);
        Task<List<MessageSend>> getReceiverMessage();
        MessageSend messageToMessageSend(Message msg);
    }
}
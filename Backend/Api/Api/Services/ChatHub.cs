using Microsoft.AspNetCore.SignalR;

namespace Api.Services
{
    public class ChatHub:Hub
    {
        static public readonly Dictionary<string, string> Users = new Dictionary<string, string>();
        private readonly JwtService _jwtService;
        public ChatHub(JwtService jwtService)
        {
            _jwtService = jwtService;
        }
        public override async Task OnConnectedAsync()
        {
            string token = Context.GetHttpContext().Request.Query["access_token"];
            if (token == null)
                return;
            string id = _jwtService.TokenToId(token);
            if (id == null)
                return;
            Users.Add(Context.ConnectionId, id);
            //await SendDirect(id, "poruka");
            //await Send(Context.ConnectionId);
            await base.OnConnectedAsync();

        }
        public override async Task OnDisconnectedAsync(Exception? exception)
        {
            Users.Remove(Context.ConnectionId);
        }
        public static List<string> getAllConnectionsOfUser(string id)
        {
            List<string> keys = new List<string>();
            foreach (var user in Users)
            {
                if (user.Value == id)
                    keys.Add(user.Key);
            }
            return keys;
        }
    }
}
//private readonly IHubContext<ChatHub> _ichat;
//await _ichat.Clients.Client(connection).SendAsync("Message",);

//Message
//SenderId
//meesage
//date

//Message
//ReceiveId
//meesage


//Oba korisnika online  poruka se salje preko kontrolera, a stize preko socketa
//Online salje offline poruka se salje preko kontrolera,cuva se u bazi
//Korisnik koji ima poruke a bio je offline salje zahtev kontroleru za preuzimanje poruka
///chatHub
using Api.Interfaces;

namespace Api.Database
{
    public class DatabaseConnection : IDatabaseConnection
    {
        public string ConnectionString { get; set; } = String.Empty;
        public string DatabaseName { get; set; } = String.Empty;
        public string UserCollectionName { get; set; } = String.Empty;
        public string PostCollectionName { get; set; } = String.Empty;
        public string FileCollectionName { get; set; } = String.Empty;
        public string LocationCollectionName { get; set; } = String.Empty;
        public string MessageCollectionname { get; set; }= String.Empty;
    }
}

namespace Api.Interfaces
{
    public interface IDatabaseConnection
    {
        string ConnectionString { get; set; }
        string DatabaseName { get; set; }
        string UserCollectionName { get; set; }
        string PostCollectionName { get; set; }
        string FileCollectionName { get; set; }
        string LocationCollectionName { get; set; }
        string MessageCollectionname { get; set; }
    }
}

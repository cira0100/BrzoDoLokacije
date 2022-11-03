namespace Api.Services
{
    public interface IFileService
    {
        Task<Models.File> add(Models.File file);
        Task<Models.File> getById(string id);
    }
}
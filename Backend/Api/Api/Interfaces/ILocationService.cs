using Api.Models;

namespace Api.Interfaces
{
    public interface ILocationService
    {
        Task<Location> add(Location loc);
        Task<Location> getById(string id);
        Task<List<Location>> getAllLocation();
    }
}
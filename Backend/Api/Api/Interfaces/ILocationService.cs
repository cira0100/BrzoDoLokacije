using Api.Models;

namespace Api.Interfaces
{
    public interface ILocationService
    {
        Task<Location> add(Location loc);
        Task<Location> getById(string id);
        Task<List<Location>> getAllLocation();
        Task<List<Location>> SearchLocation(Coords coords);
        Task<List<Location>> SearchLocation(string query);
    }
}
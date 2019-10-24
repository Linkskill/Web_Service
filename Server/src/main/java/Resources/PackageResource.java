package Resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import DAO.PackageDAO;
import DAO.Implementation.PackageDAOJPA;

import Entities.PackageEntity;

import Utilities.DateHandler;
import Utilities.GsonFactory;

import com.google.gson.Gson;
import java.util.List;
import java.time.LocalDate;


/**
 * Classe responsável por determinar as rotas relativas ao acesso e alteração dos dados 
 * (por meio de JSONs) dos pacotes.
 */
@Path("packages")
public class PackageResource {
    private final PackageDAO packageDAO = new PackageDAOJPA();
    private final Gson gson;
    
    public PackageResource() {
        this.gson = GsonFactory.createGsonObject();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() {
        List<PackageEntity> allPackages = packageDAO.getAll();
        
        return gson.toJson(allPackages);
    }
    
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String findSimilar(
            @QueryParam("ticketType") String ticketType,
            @QueryParam("origin") String origin,
            @QueryParam("hotelOrCityName") String hotelOrCityName,
            @QueryParam("departureDate") String departureDateStr,
            @QueryParam("returnDate") String returnDateStr,
            @QueryParam("numRooms") int numRooms,
            @QueryParam("numPeople") int numPeople) {
        LocalDate departureDate = DateHandler.jsonStringToDate(departureDateStr);
        LocalDate returnDate = DateHandler.jsonStringToDate(returnDateStr);
        
        List<PackageEntity> similarPackages = packageDAO.searchSimilar(
                ticketType, origin, hotelOrCityName, numRooms, 
                departureDate, returnDate, numPeople
        );
        
        return gson.toJson(similarPackages);
    }
    
    @DELETE
    @Path("buy/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String buy(@PathParam("id") int id) {
        PackageEntity packageToBuy = packageDAO.find(id);
        
        if(packageToBuy == null) {
            return "O pacote não está mais disponível.";
        }
        
        boolean purchaseSuccessful = packageToBuy.buy();
        if(purchaseSuccessful)
            return "O pacote foi comprado com sucesso!";
        else
            return "Problema na compra";
    }
    
}

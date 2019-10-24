package Resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import DAO.TicketDAO;
import DAO.AccommodationDAO;
import DAO.PackageDAO;
import DAO.Implementation.TicketDAOJPA;
import DAO.Implementation.AccommodationDAOJPA;
import DAO.Implementation.PackageDAOJPA;

import Entities.TicketEntity;
import Entities.AccommodationEntity;
import Entities.PackageEntity;

import Utilities.DateHandler;
import Utilities.GsonFactory;

import com.google.gson.Gson;
import java.util.List;
import java.time.LocalDate;

/**
 * Classe responsável por determinar as rotas relativas ao acesso e alteração dos dados
 * (por meio de JSONs) das vagas de hospedagens.
 */
@Path("accommodations")
public class AccommodationResource {
    private final TicketDAO ticketDAO = new TicketDAOJPA();
    private final AccommodationDAO accommodationDAO = new AccommodationDAOJPA();
    private final PackageDAO packageDAO = new PackageDAOJPA();
    private final Gson gson;
    
    public AccommodationResource() {
        this.gson = GsonFactory.createGsonObject();
    }
    
    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String create(String jsonVacancy) {
        AccommodationEntity newAccommodation = gson.fromJson(jsonVacancy, AccommodationEntity.class);
        System.out.println("Criando nova hospedagem: \n" + newAccommodation.toString());
        
        if(newAccommodation.isValid()) {
            boolean confirmation = accommodationDAO.create(newAccommodation);
            if(!confirmation)
                return "Ocorreu um problema na criação da hospedagem.";
            
            List<TicketEntity> matchingTickets = ticketDAO.findMatches(newAccommodation);

            for(TicketEntity ticket : matchingTickets) {
                PackageEntity newPackage = new PackageEntity(ticket, newAccommodation);

                packageDAO.create(newPackage);
            }

            return "Hospedagem criada com sucesso!";
        } else {
            return "JSON no formato errado.";
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() {
        List<AccommodationEntity> allAccommodations = accommodationDAO.getAll();
        
        return gson.toJson(allAccommodations);
    }
    
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String findSimilar(
            @QueryParam("hotelOrCityName") String hotelOrCityName,
            @QueryParam("entranceDate") String entranceDateStr,
            @QueryParam("exitDate") String exitDateStr,
            @QueryParam("numRooms") int numRooms,
            @QueryParam("numPeople") int numPeople) {
        LocalDate entranceDate = DateHandler.jsonStringToDate(entranceDateStr);
        LocalDate exitDate = DateHandler.jsonStringToDate(exitDateStr);
        
        List<AccommodationEntity> similarAccommodations = accommodationDAO.searchSimilar(
            hotelOrCityName, entranceDate, exitDate,
            numRooms, numPeople
        );
        
        return gson.toJson(similarAccommodations);
    }
    
    @DELETE
    @Path("buy/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String buy(@PathParam("id") int id) {
        AccommodationEntity accommodationToBuy = accommodationDAO.find(id);
        
        if(accommodationToBuy == null) {
            return "Hospedagem não está mais disponível.";
        }
        
        boolean confirmation = accommodationToBuy.buy();
        
        if(confirmation) { return "Hospedagem comprada com sucesso!"; }
        else { return "Problema na compra"; }
    }
    
    @DELETE
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String remove(@PathParam("id") int id) {
        boolean confirmation = accommodationDAO.remove(id);
        
        if(confirmation) { return "Hospedagem removida com sucesso!"; }
        else { return "Erro na remoção da hospedagem"; }
    }
    
}

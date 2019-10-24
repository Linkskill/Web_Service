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
 * (por meio de JSONs) das passagens.
 */
@Path("tickets")
public class TicketResource {
    private final TicketDAO ticketDAO = new TicketDAOJPA();
    private final AccommodationDAO accommodationDAO = new AccommodationDAOJPA();
    private final PackageDAO packageDAO = new PackageDAOJPA();
    private final Gson gson;
    
    public TicketResource() {
        this.gson = GsonFactory.createGsonObject();
    }
    
    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String create(String jsonTicket) {
        TicketEntity newTicket = gson.fromJson(jsonTicket, TicketEntity.class);
        System.out.println("Criando nova passagem: \n" + newTicket.toString());
        
        if(newTicket.isValid()) {
            boolean confirmation = ticketDAO.create(newTicket);

            if(!confirmation) {
                return "Ocorreu um problema na criação da passagem.";
            }
            
            List<AccommodationEntity> matchingVacancies = accommodationDAO.findMatches(newTicket);

            for(AccommodationEntity vacancy : matchingVacancies) {
                PackageEntity newPackage = new PackageEntity(newTicket, vacancy);
                packageDAO.create(newPackage);
            }

            return "Passagem criada com sucesso!";
        } else {
            return "JSON no formato errado.";
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() {
        List<TicketEntity> allTickets = ticketDAO.getAll();
        
        return gson.toJson(allTickets);
    }
    
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String findSimilar(
            @QueryParam("ticketType") String ticketType,
            @QueryParam("origin") String origin,
            @QueryParam("destination") String destination,
            @QueryParam("departureDate") String departureDateStr,
            @QueryParam("returnDate") String returnDateStr, 
            @QueryParam("numPeople") int numPeople ) {
        LocalDate departureDate = DateHandler.jsonStringToDate(departureDateStr);
        LocalDate returnDate = DateHandler.jsonStringToDate(returnDateStr);
        
        List<TicketEntity> similarTickets = ticketDAO.searchSimilar(
            ticketType, origin, destination,
            departureDate, returnDate, numPeople
        );
        
        return gson.toJson(similarTickets);
    }

    
    @DELETE
    @Path("buy/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String buy(@PathParam("id") int id) {
        TicketEntity ticketToBuy = ticketDAO.find(id);
        
        if(ticketToBuy == null) {
            return "A passagem não está mais disponível.";
        }
        
        boolean confirmation = ticketToBuy.buy();
        
        if(confirmation) { return "Passagem comprada!"; }
        else { return "Não foi possível comprar a passagem"; }
    }
    
    @DELETE
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String remove(@PathParam("id") int id) {
        boolean confirmation = ticketDAO.remove(id);
        
        if(confirmation) { return "A passagem foi removida!"; }
        else { return "Erro na remoção da passagem"; }
    }

}


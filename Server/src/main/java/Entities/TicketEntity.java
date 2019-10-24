package Entities;

import DAO.TicketDAO;
import DAO.Implementation.TicketDAOJPA;
import Utilities.DateHandler;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Representação da tabela "ticket" do banco de dados.
 */
@Entity
@Table(name = "ticket")
public class TicketEntity implements Serializable {
    private static TicketDAO dao = new TicketDAOJPA();
    
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Expose
    private int id;
    @Column(name="tickettype")
    @Expose
    private String ticketType;
    
    @Expose
    private String origin;
    @Expose
    private String destination;
    
    @Column(name="departuredate")
    @Expose
    private LocalDate departureDate;
    @Column(name="returndate")
    @Expose
    private LocalDate returnDate;
    @Column(name="numpeople")
    @Expose
    private int numPeople;
   
    @Expose
    private float price;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "ticketEntity", fetch = FetchType.EAGER)
    private Set<PackageEntity> packagesThatIncludes;

    public TicketEntity() {
        //Necessário para o JPA
    }
    
    /**
     * Remove a passagem "comprada" pelo cliente do banco de dados por meio
     * do DAO. É sicronizado pelo fato de que pode ocorrer erros
     * caso dois clientes comprem a mesma passagem ao mesmo tempo.
     * @return true se foi possível comprar (remover a passagem do banco de dados), false caso contrário
     */
    public synchronized boolean buy() { 
        boolean confirmation = dao.remove(id);
        
        if(confirmation) { return true; } 
        else { return false; }
    }
    
    /**
     * Converte todos os dados relativos a essa entidade em uma string.
     * @return str do tipo String
     */
    @Override
    public String toString() {
        String str = "";

        str += "Passagem aérea #" + id + ": \n";
        str += "  Tipo da passagem: " + ticketType + "\n";
        str += "  Origem: " + origin + "\n";
        str += "  Destino: " + destination + "\n";
        str += "  Data de partida: " + DateHandler.dateToString(departureDate) + "\n";
        str += "  Data de retorno: " + DateHandler.dateToString(returnDate) + "\n";
        str += "  Para: " + numPeople + " pessoas\n";
        str += "  Preço: " + price + "\n";

        return str;
    }

    /**
     * Verifica se todos os campos foram preenchidos corretamente.
     * @return True se é uma instância válida, false caso contrário.
     */
    public boolean isValid() {
        if (ticketType == null || origin == null || destination == null)
            return false;
        
        if (departureDate == null)
            return false;
        
        if (ticketType.equals("ida e volta") && returnDate == null)
            return false;
        
        if (numPeople <= 0 || price < 0.0)
            return false;
        
        return true;
    }
    
    /**
     * Retorna o ID da entidade TicketEntity.
     * @return id do tipo int
     */
    public int getId() { return id; }

    /**
     * Retorna o modo das passagens.
     * @return ticketType do tipo String
     */
    public String getTicketType() { return ticketType; }

    /**
     * Retorna o local de origem.
     * @return origin do tipo String
     */
    public String getOrigin() { return origin; }

    /**
     * Retorna local de destino.
     * @return destination do tipo String
     */
    public String getDestination() { return destination; }

    /**
     * Retorna a data de partida.
     * @return departureDate do tipo LocalDate
     */
    public LocalDate getDepartureDate() { return departureDate; }

    /**
     * Retorna a data de retorno.
     * @return returnDate do tipo LocalDate
     */
    public LocalDate getReturnDate() { return returnDate; }

    /**
     * Retorna o número de pessoas.
     * @return numPeople do tipo int
     */
    public int getNumPeople() { return numPeople; }

    /**
     * Retorna o preço da passagem.
     * @return price do tipo float
     */
    public float getPrice() { return price; }
}
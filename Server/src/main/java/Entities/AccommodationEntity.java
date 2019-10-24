package Entities;

import DAO.AccommodationDAO;
import DAO.Implementation.AccommodationDAOJPA;
import Utilities.DateHandler;

import java.io.Serializable;
import javax.persistence.*;
import com.google.gson.annotations.Expose;
import java.time.LocalDate;
import java.util.Set;

/**
 * Representação da tabela "accommodation" do banco de dados.
 */
@Entity
@Table(name = "accommodation")
public class AccommodationEntity implements Serializable {
    private static AccommodationDAO dao = new AccommodationDAOJPA();
    
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Expose
    private int id;
    
    @Expose
    private String location;
    @Expose
    private String hotel;
    
    @Column(name="entrancedate")
    @Expose
    private LocalDate entranceDate;
    @Column(name="exitdate")
    @Expose
    private LocalDate exitDate;
    @Column(name="numrooms")
    @Expose
    private int numRooms;
    @Column(name="numpeople")
    @Expose
    private int numPeople;
    
    @Expose
    private float price;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "accommodationEntity", fetch = FetchType.EAGER)
    private Set<PackageEntity> packagesThatIncludes;

    public AccommodationEntity() {
        //Necessário para o JPA
    }

    /**
     * Remove a vaga de hospedagem "comprada" pelo cliente do banco de dados por meio
     * do DAO. É sicronizado pelo fato de que pode ocorrer erros
     * caso dois clientes comprem a mesma vaga ao mesmo tempo.
     * @return true se foi possível comprar (remover a hospedagem do banco de dados), false caso contrário
     */
    public synchronized boolean buy() { 
        boolean confirmation = dao.remove(id); 
        
        if(confirmation) { return true; }
        else {return false; }
    }
    
    /**
     * Verifica se todos os campos foram preenchidos corretamente.
     * @return True se é uma instância válida, false caso contrário.
     */
    public boolean isValid() {
        if(location == null || hotel == null)
            return false;
        
        if(entranceDate == null || exitDate == null)
            return false;
        
        if(numRooms <= 0 || numPeople <= 0 || price < 0.0)
            return false;
        
        return true;
    }
    
    
    /**
     * Converte todos os dados relativos a essa entidade em uma string.
     * @return str do tipo String
     */
    @Override
    public String toString() {
        String str = "";

        str += "Oferta de vaga de hotel #" + id + ": \n";
        str += "  Localização: " + location + "\n";
        str += "  Hotel: " + hotel + "\n";
        str += "  Data de entrada: " + DateHandler.dateToString(entranceDate) + "\n";
        str += "  Data de saida: " + DateHandler.dateToString(exitDate) + "\n";
        str += "  Número de quartos: " + numRooms + "\n";
        str += "  Para: " + numPeople + " pessoas\n";
        str += "  Preço: " + price + "\n";

        return str;
    }

    /**
     * Retorna o ID da entidade da hospedagem.
     * @return id do tipo int
     */
    public int getId() { return id; }

    /**
     * Retorna o nome do local.
     * @return location do tipo String
     */
    public String getLocation() { return location; }

    /**
     * Retorna o nome do hotel
     * @return hotel do tipo String
     */
    public String getHotel() { return hotel; }

    /**
     * Retorna a data de início do serviço de hospedagem.
     * @return entranceDate do tipo LocalDate
     */
    public LocalDate getEntranceDate() { return entranceDate; }

    /**
     * Retorna a data final do serviço de hospedagem.
     * @return exitDate do tipo LocalDate
     */
    public LocalDate getExitDate() { return exitDate; }

    /**
     * Retorna o número de quartos.
     * @return numRooms do tipo int
     */
    public int getNumRooms() { return numRooms; }

    /**
     * Retorna o número de pessoas a serem hospedadas.
     * @return numPeople do tipo int
     */
    public int getNumPeople() { return numPeople; }

    /**
     * Retorna o preço da hospedagem.
     * @return price do tipo float
     */
    public float getPrice() { return price; }
}

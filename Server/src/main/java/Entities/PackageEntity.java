package Entities;

import DAO.PackageDAO;
import DAO.Implementation.PackageDAOJPA;

import java.io.Serializable;
import javax.persistence.*;

import com.google.gson.annotations.Expose;

/**
 * Representação da tabela "package" do banco de dados.
 */
@Entity
@Table(name = "package")
public class PackageEntity implements Serializable {
    private static PackageDAO dao = new PackageDAOJPA();
    
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Expose
    private int id;

    @ManyToOne @JoinColumn(name = "ticket_id")
    @Expose
    private TicketEntity ticketEntity;

    @ManyToOne @JoinColumn(name = "accommodation_id")
    @Expose
    private AccommodationEntity accommodationEntity;

    @Expose
    private float price;

    public PackageEntity() {
        //Necessário para o JPA
    }

    /**
     * Construtor da classe PackageEntity, ajusta os atributos de acordo com as entidades
     * enviadas por parâmetro.
     * @param ticket do tipo TicketEntity
     * @param accommodation do tipo AccommodationEntity
     */
    public PackageEntity(TicketEntity ticket, AccommodationEntity accommodation) {
        this.ticketEntity = ticket;
        this.accommodationEntity = accommodation;
        this.price = (ticket.getPrice() + accommodationEntity.getPrice()) * (float)0.8;
    }

    /**
     * Remove o pacote "comprado" pelo cliente do banco de dados por meio
     * do DAO. É sicronizado pelo fato de que pode ocorrer erros caso dois clientes comprem 
     * o mesmo pacote ao mesmo tempo.
     * @return true se foi possível comprar (remover o pacote do banco de dados), false caso contrário
     */
    public synchronized boolean buy() { 
        boolean confirmation = dao.remove(id); 
        
        if(confirmation) { return true; } 
        else { return false; }
    }

    /**
     * Retorna o ID da entidade do pacote.
     * @return id do tipo int
     */
    public int getId() { return id; }

    /**
     * Retorna o preço do pacote.
     * @return price do tipo int
     */
    public float getPrice() { return price; }

    /**
     * Retorna a entidade das passagens.
     * @return ticketEntity do tipo TicketEntity
     */
    public TicketEntity getTicketEntity() { return ticketEntity; }

    /**
     * Retorna a entidade das vagas de hospedagem.
     * @return accommodationEntity do tipo AccommodationEntity
     */
    public AccommodationEntity getAccommodationEntity() { return accommodationEntity; }
}

package DAO;

import Entities.TicketEntity;
import Entities.AccommodationEntity;

import java.time.LocalDate;
import java.util.List;


public interface TicketDAO extends DAO<TicketEntity> {
    /**
     * Encontra passagens similares aos parâmetros informados.
     * 
     * @param ticketType Tipo de passagem (ida e volta, somente ida)
     * @param origin Local de origem
     * @param destination Local de destino
     * @param departureDate Data de partida
     * @param returnDate Data de retorno
     * @param numPeople Número de pessoas
     * @return Lista de passagens similares.
     */
    public List<TicketEntity> searchSimilar(String ticketType, String origin,
        String destination, LocalDate departureDate, LocalDate returnDate,
        int numPeople);
    
    /**
     * Encontra passagens que podem formar pacotes com uma determinada vaga de hospedagem.
     * 
     * @param accommodations Vaga para formar pacotes
     * @return Lista de passagens que podem formar um pacote com a vaga
     */
    public List<TicketEntity> findMatches(AccommodationEntity accommodations);
}

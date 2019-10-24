package DAO;

import Entities.AccommodationEntity;
import Entities.TicketEntity;

import java.time.LocalDate;
import java.util.List;


public interface AccommodationDAO extends DAO<AccommodationEntity> {
    /**
     * Encontra vagas similares aos parâmetros fornecidos.
     * 
     * @param hotelOrCityName Nome da cidade ou hotel
     * @param entranceDate Data de entrada
     * @param exitDate Data de saída
     * @param numRooms Número de quartos
     * @param numPeople Número de pessoas
     * @return Lista de vagas similares.
     */
    public List<AccommodationEntity> searchSimilar(String hotelOrCityName,
            LocalDate entranceDate, LocalDate exitDate, int numRooms, int numPeople);
    
    /**
     * Encontra vagas de hospedagem que podem formar pacotes com uma determinada passagem.
     * 
     * @param tickets Passagem para formar os pacotes
     * @return Lista de vagas que podem formar um pacote com a passagem
     */
    public List<AccommodationEntity> findMatches(TicketEntity tickets);
}

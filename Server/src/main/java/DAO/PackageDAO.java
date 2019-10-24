package DAO;

import Entities.PackageEntity;

import java.util.List;
import java.time.LocalDate;


public interface PackageDAO extends DAO<PackageEntity> {
    /**
     * Encontra pacotes similares aos parâmetros fornecidos.
     * 
     * @param ticketType Tipo da passagem (ida e volta ou somente ida)
     * @param origin Local de origem
     * @param hotelOrCityName Nome da cidade ou hotel
     * @param numRooms Número de quartos
     * @param departureDate Data de partida
     * @param returnDate Data de retorno
     * @param numPeople Número de pessoas (número de assentos da passagem e
     *                  total que é comportado somando todos os quartos)
     * @return Lista de pacotes similares.
     */
    public List<PackageEntity> searchSimilar(String ticketType,
            String origin, String hotelOrCityName, int numRooms,
            LocalDate departureDate, LocalDate returnDate, int numPeople);
}

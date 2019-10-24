package DAO.Implementation;

import DAO.AccommodationDAO;
import javax.persistence.*;

import Entities.AccommodationEntity;
import Entities.TicketEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe que implementa a interface AccommodationDAO.
 */
public class AccommodationDAOJPA implements AccommodationDAO {

    @Override
    public boolean create(AccommodationEntity entity) {
        boolean confirmation;
        EntityManager entityManager = JPASessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            confirmation = true;
        } catch (EntityExistsException e) {
            entityManager.getTransaction().rollback();
            System.out.println("Exception: " + e.getMessage());
            confirmation = false;
        } finally {
            entityManager.close();
        }
        
        return confirmation;
    }

    @Override
    public AccommodationEntity find(int id) {
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();
        AccommodationEntity accommodation = entityManager.find(AccommodationEntity.class, id);
        entityManager.getTransaction().commit();

        entityManager.close();

        return accommodation;
    }
    
    @Override
    public List<AccommodationEntity> getAll(){
        EntityManager entityManager = JPASessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Query getAllAccommodations = entityManager.createQuery(
            "SELECT ae FROM AccommodationEntity ae",
            AccommodationEntity.class
        );

        List<AccommodationEntity> accommodations = getAllAccommodations.getResultList();
        
        entityManager.getTransaction().commit();
        entityManager.close();
        
        return accommodations;
    }

    @Override
    public boolean remove(int id) {
        boolean confirmation;
        EntityManager entityManager = JPASessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            AccommodationEntity accommodation = entityManager.find(AccommodationEntity.class, id);
            entityManager.remove(accommodation);
            entityManager.getTransaction().commit();
            confirmation = true;
        } catch(EntityExistsException e) {
            entityManager.getTransaction().rollback();
            System.out.println("Exception: " + e.getMessage());
            confirmation = false;
        } finally {
            entityManager.close();
        }
        
        return confirmation;
    }
    
    @Override
    public List<AccommodationEntity> searchSimilar(String hotelOrCityName, LocalDate entranceDate, 
            LocalDate exitDate, int numRooms, int numPeople) {
        
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query getSimilarAccommodations = entityManager.createQuery(
            "SELECT ae FROM AccommodationEntity ae WHERE " +
                "(ae.location = :location OR ae.hotel = :location) AND " +
                "ae.entranceDate = :entranceDate AND " +
                "ae.exitDate = :exitDate AND " +
                "ae.numRooms = :numRooms AND " +
                "ae.numPeople = :numPeople",
            AccommodationEntity.class
        );
        getSimilarAccommodations.setParameter("location", hotelOrCityName);
        getSimilarAccommodations.setParameter("entranceDate", entranceDate);
        getSimilarAccommodations.setParameter("exitDate", exitDate);
        getSimilarAccommodations.setParameter("numRooms", numRooms);
        getSimilarAccommodations.setParameter("numPeople", numPeople);

        List<AccommodationEntity> similarAccommodations = getSimilarAccommodations.getResultList();

        entityManager.getTransaction().commit();

        entityManager.close();

        return similarAccommodations;
    }

    @Override
    public List<AccommodationEntity> findMatches(TicketEntity tickets) {
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query getMatchingAccommodations = entityManager.createQuery(
            "SELECT ae FROM AccommodationEntity ae WHERE " +
                "ae.location = :location AND " +
                "ae.entranceDate = :entranceDate AND " +
                "ae.exitDate = :exitDate AND " +
                "ae.numPeople = :numPeople",
            AccommodationEntity.class
        );
        
        getMatchingAccommodations.setParameter("location", tickets.getDestination());
        getMatchingAccommodations.setParameter("entranceDate", tickets.getDepartureDate());
        getMatchingAccommodations.setParameter("exitDate", tickets.getReturnDate());
        getMatchingAccommodations.setParameter("numPeople", tickets.getNumPeople());

        List<AccommodationEntity> matchingAccommodations = getMatchingAccommodations.getResultList();
        
        entityManager.getTransaction().commit();

        entityManager.close();
        
        return matchingAccommodations;
    }
    
}

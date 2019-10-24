package DAO.Implementation;

import DAO.TicketDAO;
import javax.persistence.*;

import Entities.AccommodationEntity;
import Entities.TicketEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe que implementa a interface TicketDAO.
 */
public class TicketDAOJPA implements TicketDAO {

    @Override
    public boolean create(TicketEntity entity) {
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
    public TicketEntity find(int id) {
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();
        TicketEntity ticket = entityManager.find(TicketEntity.class, id);
        entityManager.getTransaction().commit();

        entityManager.close();

        return ticket;
    }
    
    @Override
    public List<TicketEntity> getAll() {
        EntityManager entityManager = JPASessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Query getAllTickets = entityManager.createQuery(
            "SELECT te FROM TicketEntity te",
            TicketEntity.class
        );
        
        List<TicketEntity> tickets = getAllTickets.getResultList();
        
        entityManager.getTransaction().commit();
        entityManager.close();
        
        return tickets;
    }

    @Override
    public boolean remove(int id) {
        boolean confirmation;
        EntityManager entityManager = JPASessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {    
            TicketEntity ticket = entityManager.find(TicketEntity.class, id);
            entityManager.remove(ticket);
            entityManager.getTransaction().commit();
            confirmation = true;
        } catch(EntityExistsException e) {
            entityManager.getTransaction().rollback();
            System.out.println("Exception: " + e.getMessage());
            confirmation = false;
        } finally{
            entityManager.close();
        }
        
        return confirmation;
    }
    
    @Override
    public List<TicketEntity> searchSimilar(String ticketType, String origin, String destination,
            LocalDate departureDate, LocalDate returnDate, int numPeople) {
        
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query getSimilarTickets;
        if(returnDate == null) {
            getSimilarTickets = entityManager.createQuery(
                "SELECT te FROM TicketEntity te WHERE " +
                    "te.ticketType = :type AND " +
                    "te.origin = :origin AND " +
                    "te.destination = :destination AND " +
                    "te.departureDate = :departureDate AND " +
                    "te.returnDate IS NULL AND " +
                    "te.numPeople = :numPeople",
                TicketEntity.class
            );
        } else {
            getSimilarTickets = entityManager.createQuery(
                "SELECT te FROM TicketEntity te WHERE " +
                    "te.ticketType = :type AND " +
                    "te.origin = :origin AND " +
                    "te.destination = :destination AND " +
                    "te.departureDate = :departureDate AND " +
                    "te.returnDate = :returnDate AND " +
                    "te.numPeople = :numPeople",
                TicketEntity.class
            );
            getSimilarTickets.setParameter("returnDate", returnDate);
        }
        
        getSimilarTickets.setParameter("type", ticketType);
        getSimilarTickets.setParameter("origin", origin);
        getSimilarTickets.setParameter("destination", destination);
        getSimilarTickets.setParameter("departureDate", departureDate);
        getSimilarTickets.setParameter("numPeople", numPeople);

        List<TicketEntity> similarTickets = getSimilarTickets.getResultList();
        
        entityManager.getTransaction().commit();

        entityManager.close();

        return similarTickets;
    }

    @Override
    public List<TicketEntity> findMatches(AccommodationEntity accommodations) {
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query getMatchingTickets = entityManager.createQuery(
            "SELECT te FROM TicketEntity te WHERE " +
                "te.destination = :location AND " +
                "te.departureDate = :entranceDate AND " +
                "te.returnDate = :exitDate AND " +
                "te.numPeople = :numPeople",
            TicketEntity.class
        );
        getMatchingTickets.setParameter("location", accommodations.getLocation());
        getMatchingTickets.setParameter("entranceDate", accommodations.getEntranceDate());
        getMatchingTickets.setParameter("exitDate", accommodations.getExitDate());
        getMatchingTickets.setParameter("numPeople", accommodations.getNumPeople());

        List<TicketEntity> matchingTickets = getMatchingTickets.getResultList();
        
        entityManager.getTransaction().commit();

        entityManager.close();
        
        return matchingTickets;
    }
    
}

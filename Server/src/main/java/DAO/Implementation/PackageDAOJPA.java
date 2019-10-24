package DAO.Implementation;

import DAO.PackageDAO;
import javax.persistence.*;

import Entities.PackageEntity;
import DAO.TicketDAO;
import DAO.AccommodationDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe que implementa a interface PackageDAO.
 */
public class PackageDAOJPA implements PackageDAO {
    static private TicketDAO ticketDAO = new TicketDAOJPA();
    static private AccommodationDAO accommodationDAO = new AccommodationDAOJPA();
    
    @Override
    public boolean create(PackageEntity entity) {
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
    public PackageEntity find(int id) {
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();
        PackageEntity packageEntity = entityManager.find(PackageEntity.class, id);
        entityManager.getTransaction().commit();

        entityManager.close();

        return packageEntity;
    }
    
    @Override
    public List<PackageEntity> getAll() {
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query getAllPackages = entityManager.createQuery(
            "SELECT pe FROM PackageEntity pe",
            PackageEntity.class
        );

        List<PackageEntity> packages = getAllPackages.getResultList();
        
        entityManager.getTransaction().commit();

        entityManager.close();
        
        return packages;
    }

    @Override
    public boolean remove(int id) {
        boolean confirmation;
        EntityManager entityManager = JPASessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            PackageEntity packageToRemove = entityManager.find(PackageEntity.class, id);
            entityManager.remove(packageToRemove);
            ticketDAO.remove(packageToRemove.getTicketEntity().getId());
            accommodationDAO.remove(packageToRemove.getAccommodationEntity().getId());
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
    public List<PackageEntity> searchSimilar(String ticketType, String origin, 
            String hotelOrCityName, int numRooms, LocalDate departureDate, 
            LocalDate returnDate, int numPeople) {
        
        EntityManager entityManager = JPASessionFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query searchPackages = entityManager.createQuery(
            "SELECT pe FROM PackageEntity pe WHERE " +
                "pe.ticketEntity.ticketType = :type AND " +
                "pe.ticketEntity.origin = :origin AND " +
                "(pe.ticketEntity.destination = :location OR pe.accommodationEntity.hotel = :location) AND " +
                "pe.accommodationEntity.numRooms = :numRooms AND " +
                "pe.ticketEntity.departureDate = :departureDate AND " +
                "pe.ticketEntity.returnDate = :returnDate AND " +
                "pe.accommodationEntity.numPeople = :numPeople",
            PackageEntity.class
        );

        searchPackages.setParameter("type", ticketType);
        searchPackages.setParameter("origin", origin);
        searchPackages.setParameter("location", hotelOrCityName);
        searchPackages.setParameter("numRooms", numRooms);
        searchPackages.setParameter("departureDate", departureDate);
        searchPackages.setParameter("returnDate", returnDate);
        searchPackages.setParameter("numPeople", numPeople);

        List<PackageEntity> similarPackages = searchPackages.getResultList();

        entityManager.getTransaction().commit();

        entityManager.close();

        return similarPackages;
    }
    
}

package Resources;

import javax.ws.rs.core.Application;
import DAO.Implementation.JPASessionFactory;
import Utilities.GsonFactory;
import java.util.Set;

/**
 * Classe que define a rota principal, que instancia os demais recursos e pai das outras rotas.
 */
@javax.ws.rs.ApplicationPath("resources")
public class WebService extends Application {
    public WebService() {
        JPASessionFactory.initialize();
        GsonFactory.initialize();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(Resources.AccommodationResource.class);
        resources.add(Resources.PackageResource.class);
        resources.add(Resources.TestResource.class);
        resources.add(Resources.TicketResource.class);
    }
    
}

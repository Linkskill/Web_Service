package Resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Classe responsável por verificar se o servidor está respondendo.
 */
@Path("test")
public class TestResource {
    /**
     * Retorna uma mensagem falando que está tudo ok.
     * @return String de confirmação.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "O servidor esta respondendo.";
    }
}

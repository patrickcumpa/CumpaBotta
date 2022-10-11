package serveres20;

import serveres20.server.UDPServer;
import serveres20.salva_dati.SalvaDati;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import serveres20.configuration.Configurazione;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class ServerEs20 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            SalvaDati.readToFile();
            UDPServer server = new UDPServer(Configurazione.SERVER_PORT);
            Thread serThread = new Thread(server);
            serThread.start();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerEs20.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

package serveres21.TCPServer;

import java.io.IOException;

/**
 *
 * @author PatrickCumpa
 * @since 10/10/2022
 */
public class ServerEs21 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            TCPServer server = new TCPServer(13);
            Thread thrServer = new Thread(server);
            thrServer.start();
        } catch (IOException ex) {
            System.err.println("Errore all'avvio: " + ex);
        }
    }
    
}

package es21.TCPServer;

import es21.TCPServer.TCPServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            int c = System.in.read();
            thrServer.interrupt();
            thrServer.join();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerEs21.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

package clientes21;

import java.io.IOException;

/**
 *
 * @author TommasoBotta
 */
public class ClientEs21 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            TCPClient client = new TCPClient();
            Thread thrClient = new Thread(client);
            thrClient.start();
        } catch (IOException ex) {
            System.err.println("Errore all'avvio: " + ex);
        }
    }
    
}

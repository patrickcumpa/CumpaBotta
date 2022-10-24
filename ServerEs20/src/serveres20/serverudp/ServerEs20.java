package serveres20.serverudp;

import serveres20.salvadati.SalvaDati;
import java.io.IOException;
import java.net.SocketException;
import serveres20.Costanti;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class ServerEs20 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        try {
            SalvaDati.readToFile();
            CassonettoSmart server = new CassonettoSmart(Costanti.SERVER_PORT);
            Thread serThread = new Thread(server);
            serThread.start();
            serThread.join();
        } catch (SocketException ex) {
            System.err.println("Errore all'avvio: " + ex);
        } catch (InterruptedException ex) {
            System.err.println("Fine!");
        } 
    }
    
}

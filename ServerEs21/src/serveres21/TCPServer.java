package serveres21;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PatrickCumpa
 */
public class TCPServer implements Runnable {
    
    private ServerSocket server;
    private ArrayList<Giudice> giudici;
    
    public TCPServer(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.giudici = new ArrayList<>();
    }

    @Override
    public void run() {
        Socket connection = null;
        System.out.println("Server in attesa...\n");
        
        while (!Thread.interrupted()) {
            try {
                connection = server.accept();
                System.out.println(connection + " si è connesso!\n");
                
                ObjectInputStream objIn = new ObjectInputStream(connection.getInputStream());
                int scelta = objIn.readInt();
                
                switch (scelta) {
                    case 1:
                        giudici.add((Giudice) objIn.readObject());
                        FileWriter file = new FileWriter("dati.csv");
                        for (Iterator<Giudice> iterator = giudici.iterator(); iterator.hasNext();) {
                            file.write(iterator.next().toString());
                            file.write("\r\n");
                        }
                        file.close();
                        OutputStream out = new ObjectOutputStream(connection.getOutputStream());
                        out.write("Giudice salvato!".getBytes());
                        out.flush();
                        break;
                    default:
                        throw new AssertionError();
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.shutdownOutput();
                        connection.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                server.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
     
    
}
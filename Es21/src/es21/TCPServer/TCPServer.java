package es21.TCPServer;

import es21.Giudice;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PatrickCumpa
 */
public class TCPServer implements Runnable {

    private final ServerSocket server;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
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
                objIn = new ObjectInputStream(connection.getInputStream());
                objOut = new ObjectOutputStream(connection.getOutputStream());
                System.out.println(connection + " si è connesso!\n");

                int scelta = (int) objIn.readObject();

                switch (scelta) {
                    case 1:
                        giudici.add((Giudice) objIn.readObject());
                        salvaDati();
                        objOut.writeObject("\nVoti registrati con successo!\n");
                        leggiDati();
                        break;
                        
                    case 2:
                        break;
                        
                    case 3:
                        Thread.currentThread().interrupt();
                        break;
                        
                    default:
                        objOut.writeObject("\nPer favore inserire una scelta valida!\n");
                        objOut.flush();
                }
            } catch (IOException ex) {
                System.err.println("Errore nella comunicazione con il client: " + ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void salvaDati() throws IOException {
        FileWriter file = new FileWriter("dati.csv", true);
        for (Iterator<Giudice> iterator = giudici.iterator(); iterator.hasNext();) {
            file.write(iterator.next().toString());
            file.write("\r\n");
        }
        file.close();
    }

    private void leggiDati() throws FileNotFoundException, IOException {
        BufferedReader sc = new BufferedReader(new FileReader("dati.csv"));
        String line = "";
        while ((line = sc.readLine()) != null) {
            String[] tokens = line.split(",");
            System.out.println(Arrays.toString(tokens));
        }
        sc.close();
    }

}

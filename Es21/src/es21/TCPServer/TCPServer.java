package es21.TCPServer;

import es21.Giudice;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PatrickCumpa
 */
public class TCPServer implements Runnable {

    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private final ServerSocket server;

    public TCPServer(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    @Override
    public void run() {
        Socket connection = null;
        System.out.println("Server in attesa...\n");
        try {
            connection = server.accept();
            System.out.println(connection + " si è connesso!\n");
            dataIn = new DataInputStream(connection.getInputStream());
            dataOut = new DataOutputStream(connection.getOutputStream());

            while (!Thread.interrupted()) {

                switch (dataIn.readInt()) {
                    case 1:
                        if (!contaGiudici()) {
                            dataOut.writeUTF("\nSiamo spiacenti, ma l'archivio e' pieno!\n");
                            break;
                        }
                        dataOut.writeUTF("Inserisci il tuo nome: ");
                        Giudice giudice = new Giudice(dataIn.readUTF());
                        for (int i = 0; i < 4; i++) {
                            giudice.setVoti(dataIn.readInt());
                        }
                        salvaDati(giudice);

                        dataOut.writeUTF("\nVoti registrati con successo!\n");
                        break;

                    case 2:
                        dataOut.writeDouble(votoMedio((dataIn.readInt())));
                        break;

                    case 3:
                        Thread.currentThread().interrupt();
                        break;

                    default:
                        dataOut.writeUTF("\nPer favore inserire una scelta valida!\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il client: " + e);
        }

        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean contaGiudici() {
        String line;
        int count = -1;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("dati.csv"));
            while ((line = reader.readLine()) != null) 
                count++;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count < 10;
    }

    private void salvaDati(Giudice giudice) {
        FileWriter file = null;

        try {
            file = new FileWriter("dati.csv", true);
            file.write(giudice.toString());
            file.write("\r\n");
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                file.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private double votoMedio(int codice) {
        int count = 0;
        int numero = 0;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("dati.csv"));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] b = line.split(",");
                numero += Integer.parseInt(b[codice]);
                count++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numero / (double) count;
    }

}

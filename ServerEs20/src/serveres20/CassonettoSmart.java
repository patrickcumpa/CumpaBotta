package serveres20;

import serveres20.salva_dati.SalvaDati;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import serveres20.costanti.Costanti;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class CassonettoSmart implements Runnable {

    private int portClient;
    private InetAddress address;
    private DatagramSocket socket;
    private DatagramPacket request;
    private TesseraRFID utente = null;
    public static ArrayList<TesseraRFID> tessereRegistrate = new ArrayList<>();
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public CassonettoSmart(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.socket.setSoTimeout(50000);
    }
    
    @Override
    public void run() {
        int id;
        int scelta;

        System.out.println("Server in ascolto...\n");

        while (!Thread.interrupted()) {
            try {
                scelta = receiveInt();
                
                address = request.getAddress();
                portClient = request.getPort();

                System.out.println("------- RICHIESTA CLIENTE -------");
                System.out.println("IP cliente: " + request.getAddress());
                System.out.println("Porta cliente: " + request.getPort());

                switch (scelta) {
                    case 1:
                        System.out.println("Richiesta: apertura cassonetto");
                        System.out.println("---------------------------------\n");
                        id = receiveInt();
                        apriCassonetto(id);
                        break;

                    case 2:
                        System.out.println("Richiesta: crea tessera");
                        System.out.println("---------------------------------\n");
                        registraTessera();
                        break;

                    case 3:
                        System.out.println("Richiesta: elimina tessera");
                        System.out.println("---------------------------------\n");
                        id = receiveInt();
                        eliminaTessera(id);
                        break;

                    case 4:
                        System.out.println("Richiesta: chiudi");
                        System.out.println("---------------------------------\n");
                        socket.close();
                        Thread.currentThread().interrupt();
                        break;

                    default:
                        byte[] buffer = "Per favore, inserire una scelta valida!".getBytes();
                        DatagramPacket answer = new DatagramPacket(buffer, buffer.length, address, portClient);
                        socket.send(answer);
                }
            } catch (IOException ex) {
                System.err.println("Errore nella risposta: " + ex);
            }
        }
    }
    
    private int receiveInt() throws IOException {
        byte[] buffer = new byte[Integer.BYTES];
        
        request = new DatagramPacket(buffer, buffer.length);
        socket.receive(request);
        ByteBuffer input = ByteBuffer.wrap(request.getData());
        return input.getInt();
    }

    private boolean answerAndReceiveData(String risposta) throws IOException {
        byte[] buffer = risposta.getBytes();

        DatagramPacket answer = new DatagramPacket(buffer, buffer.length, address, portClient);
        socket.send(answer);

        buffer = new byte[Character.BYTES];
        request = new DatagramPacket(buffer, buffer.length);
        socket.receive(request);
        ByteBuffer input = ByteBuffer.wrap(request.getData());
        char scelta = input.getChar();
        return (scelta != 'n' || scelta == 'y');
    }

    private void answerData(String risposta) throws IOException {
        byte[] buffer = risposta.getBytes();

        DatagramPacket answer = new DatagramPacket(buffer, buffer.length, address, portClient);
        socket.send(answer);
    }
    
    private void sendId(TesseraRFID tessera) throws IOException {
        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES);
        output.putInt(tessera.getId());
        DatagramPacket answer = new DatagramPacket(output.array(), Integer.BYTES, address, portClient);
        socket.send(answer);
    }
    
    private void apriCassonetto(int id) throws IOException {

        utente = trovaUtente(id);

        if (utente == null) {
            if (answerAndReceiveData(Costanti.REGISTRARE)) {
                registraTessera();
            } else {
                answerData("Errore!\n");
            }
        } else if (!utente.isValida()) {
            String risposta = "\nSiamo spiacenti, ma non puoi utilizzare il cassonetto fino al "
                    + dtf.format(utente.getUltimaApertura().plusHours(72)) + "\n";
            answerData(risposta);
        } else {
            utente.setUltimaApertura(LocalDateTime.now());
            utente.setValida(false);
            SalvaDati.saveToFile();
            answerData(Costanti.APERTO);
        }
    }

    private void registraTessera() throws IOException {
        TesseraRFID tessera = new TesseraRFID();
        tessereRegistrate.add(tessera);
        SalvaDati.saveToFile();
        mostraTessereRegistrate();
        answerData(Costanti.REGISTRATO);
        sendId(tessera);

    }

    private void eliminaTessera(int id) throws IOException {

        utente = trovaUtente(id);

        if (utente == null) {
            answerData(Costanti.NON_REGISTRATO);
        } else if (answerAndReceiveData(Costanti.ELIMINARE)) {
            tessereRegistrate.remove(utente);
            SalvaDati.saveToFile();
            answerData(Costanti.ELIMINATO);
        } else {
            answerData("Errore!\n");
        }
    }

    private TesseraRFID trovaUtente(int id) {
        for (TesseraRFID tessera : tessereRegistrate) {
            if (tessera.getId() == id) {
                return tessera;
            }
        }
        return null;
    }

    private void mostraTessereRegistrate() {
        System.out.println("--------------------------- TESSERE REGISTRATE ---------------------------");
        for (TesseraRFID t : tessereRegistrate) {
            System.out.println(t);
        }
        System.out.println("--------------------------------------------------------------------------\n");
    }

}

package serveres20.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import serveres20.CassonettoSmart;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class UDPServer extends CassonettoSmart implements Runnable {

    private static DatagramSocket socket;
    private static DatagramPacket request;
    byte[] buffer = new byte[Integer.BYTES * 2];

    public UDPServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {

        int id;
        int scelta;

        System.out.println("Server in ascolto...\n");

        while (!Thread.interrupted()) {
            try {
                ByteBuffer data = ByteBuffer.wrap(buffer, 0, Integer.BYTES * 2);
                request = new DatagramPacket(buffer, buffer.length);
                request.setLength(buffer.length);
                socket.receive(request);

                scelta = data.getInt();
                id = data.getInt();

                System.out.println("------- RICHIESTA CLIENTE -------");
                System.out.println("IP cliente: " + request.getAddress());
                System.out.println("Porta cliente: " + request.getPort());

                switch (scelta) {
                    case 1:
                        System.out.println("Richiesta: apertura cassonetto");
                        System.out.println("---------------------------------\n");
                        apriCassonetto(id);
                        break;

                    case 2:
                        System.out.println("Richiesta: crea tessera");
                        System.out.println("---------------------------------\n");
                        registraTessera(id);
                        break;

                    case 3:
                        System.out.println("Richiesta: elimina tessera");
                        System.out.println("---------------------------------\n");
                        eliminaTessera(id);
                        break;
                    case 4:
                        System.out.println("Richiesta: chiudi");
                        System.out.println("---------------------------------\n");
                        socket.close();
                        Thread.currentThread().interrupt();
                }
            } catch (IOException ex) {
                Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static boolean answerAndReceiveData(String risposta) throws UnsupportedEncodingException, IOException {
        byte[] sendData = risposta.getBytes("UTF-8");
        byte[] receiveData = new byte[Character.BYTES];

        DatagramPacket answer = new DatagramPacket(sendData, sendData.length,
                request.getAddress(), request.getPort());
        socket.send(answer);

        ByteBuffer recBuffer = ByteBuffer.wrap(receiveData, 0, Character.BYTES);
        answer = new DatagramPacket(receiveData, receiveData.length);
        //request.setLength(receiveData.length);
        socket.receive(answer);
        char risultato = recBuffer.getChar();
        if (risultato == 'n' || risultato != 'y') {
            return false;
        }
        System.out.println("char risposta " + risultato);
        return true;
    }

    public static void answerData(String risposta) throws UnsupportedEncodingException {
        byte[] buffer = risposta.getBytes("UTF-8");

        try {
            DatagramPacket answer = new DatagramPacket(buffer, buffer.length,
                    request.getAddress(), request.getPort());
            socket.send(answer);
        } catch (IOException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

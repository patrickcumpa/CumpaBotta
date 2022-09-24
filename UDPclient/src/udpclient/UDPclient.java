package udpclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *
 * @author PatrickCumpa
 */
public class UDPclient {

    final private DatagramSocket socket;

    public UDPclient() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000); // 1000ms = 1s
    }

    public void closeSocket() {
        socket.close();
    }

    public String sendAndReceive(String request, String host, int port) throws UnknownHostException,
            IOException, SocketTimeoutException {
        byte[] buffer;
        DatagramPacket datagram;
        String answer;
        InetAddress address = InetAddress.getByName(host); // indirizzo IP del destinatario del datagram

        if (socket.isClosed()) {
            throw new IOException();
        } // verifica chiusura socket
        buffer = request.getBytes("UTF-8"); // trasformazione in array di byte della stringa
        datagram = new DatagramPacket(buffer, buffer.length, address, port); // costruzione datagram di richiesta
        socket.send(datagram); // trasmissione datagram di richiesta
        socket.receive(datagram); // attesa ricezione datagram di richiesta (tempo massimo di attesa: 1s)
        // verifica indirizzo/porta provenienza datagram di risposta
        if (datagram.getAddress().equals(address) && datagram.getPort() == port) {
            answer = new String(datagram.getData(), 0, datagram.getLength(), "ISO-8859-1");
        } else {
            throw new SocketTimeoutException();
        }
        return answer;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String IP_address;
        int UDP_port;
        String request, answer;
        UDPclient client;

        if (args.length != 3) {
            IP_address = "127.0.0.1";
            UDP_port = 7;
            request = "Hello world!";
        } else {
            IP_address = args[0];
            UDP_port = Integer.parseInt(args[1]);
            request = args[2];
        }

        try {
            client = new UDPclient();
            answer = client.sendAndReceive(request, IP_address, UDP_port);
            System.out.println("Risposta: " + answer);
            client.closeSocket();
        } catch (SocketException exception) {
            System.err.println("Errore creazione socket!");
        } catch (UnknownHostException exception) {
            System.err.println("Indirizzo IP errato!");
        } catch (SocketTimeoutException exception) {
            System.err.println("Nessuna risposta dal server!");
        } catch (IOException exception) {
            System.err.println("Errore generico di comunicazione!");
        }
    }

}

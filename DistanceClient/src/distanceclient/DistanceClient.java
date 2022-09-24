package distanceclient;

import java.io.*;
import java.net.*;
import java.nio.*;

public class DistanceClient {
    private DatagramSocket socket;
    private String IP_address;
    private int UDP_port;

    // costruttore (richiede l’indirizzo IP e il numero di porta UDP del server)
    public DistanceClient(String host, int port) throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000); // 1000ms = 1s
        IP_address = host;
        UDP_port = port;
    }

    public void close_socket() {
        socket.close();
    }

    public double computeDistance( float lat1, float lon1, float lat2, float lon2) throws UnknownHostException, IOException, SocketTimeoutException {
        DatagramPacket datagram;
        ByteBuffer input, output;
        InetAddress address;
        byte[] buffer;
        double distance;

        if ( socket.isClosed()) {
            throw new IOException();
        }
        // allocazione di un byte-buffer di 16 byte
        output = ByteBuffer.allocate(16);
        // copia valori delle coordinate geografiche nel byte-buffer
        output.putFloat(lat1);
        output.putFloat(lon1);
        output.putFloat(lat2);
        output.putFloat(lon2);
        // indirizzo IP del destinatario del datagram
        address = InetAddress.getByName(IP_address);
        // costruzione datagram di richiesta
        datagram = new DatagramPacket(output.array(), 16, address, UDP_port);
        socket.send(datagram); // trasmissione datagram di richiesta
        // allocazione di un buffer di 8 byte
        buffer = new byte[8];
        datagram = new DatagramPacket(buffer, buffer.length);
        // attesa ricezione datagram di richiesta tempo massimo di attesa: 1s
        socket.receive(datagram);
        // verifica indirizzo/porta provenienza datagram di risposta
        if ( datagram.getAddress().equals(address) && datagram.getPort() == UDP_port) {
            // incapsulazione del buffer della risposta in un byte-buffer
            input = ByteBuffer.wrap(datagram.getData());
            // estrazione di 1 valore double dal byte-buffer
            distance = input.getDouble();
        }
        else {
            throw new SocketTimeoutException();
        }
        return distance;
    }

    public static void main(String args[]) {
        DistanceClient client;
        float lat_LI = (float)(43.55000); // latitudine Livorno
        float lon_LI = (float)(10.31667); // longitudine Livorno
        float lat_PI = (float)(43.71667); // latitudine Pisa
        float lon_PI = (float)(10.40000); // longitudine Pisa
        double distance;

        try {
            client = new DistanceClient("127.0.0.1", 12345);
            distance = client.computeDistance(lat_LI, lon_LI, lat_PI, lon_PI);
            System.out.println("Distanza = " + distance);
            client.close_socket();
        }
        catch(SocketException exception) {
            System.err.println("Errore creazione socket!");
        }
        catch (UnknownHostException exception) {
            System.err.println("Indirizzo IP errato!");
        }
        catch (SocketTimeoutException exception) {
            System.err.println("Nessuna risposta dal server!");
        }
        catch (IOException exception) {
            System.err.println("Errore di comunicazione!");
        }
    }
}

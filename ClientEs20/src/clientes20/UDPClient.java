package clientes20;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 *
 * @author TommasoBotta
 */
public class UDPClient {
    
    private int serverPort;
    private DatagramSocket socket;
    private InetAddress IP_address;


    public UDPClient(String host, int port) throws SocketException, UnknownHostException {
        this.serverPort = port;
        this.IP_address = InetAddress.getByName(host);
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(50000);
    }
    
    public void sendInt(int numero) throws IOException {
        if (socket.isClosed()) {
            throw new IOException();
        }
        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES);
        output.putInt(numero);
        DatagramPacket datagram = new DatagramPacket(output.array(), Integer.BYTES, IP_address, serverPort);
        socket.send(datagram);
    }
    
    public String sendAndReceiveData(int id) throws IOException {
        byte[] buffer = new byte[1024];
        
        sendInt(id);

        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        datagram.setLength(buffer.length);
        socket.receive(datagram);

        if (!(datagram.getAddress().equals(IP_address)) && (datagram.getPort() != serverPort)) {
            throw new SocketTimeoutException();
        }
        return new String(datagram.getData(), datagram.getOffset(), datagram.getLength());
    }

    public String elaboraRisposta(char scelta) throws IOException {

        byte[] buffer = new byte[1024];
        
        ByteBuffer output = ByteBuffer.allocate(Character.BYTES);
        output.putChar(scelta);
        DatagramPacket datagram = new DatagramPacket(output.array(), Character.BYTES, IP_address, serverPort);
        socket.send(datagram);

        datagram = new DatagramPacket(buffer, buffer.length);
        datagram.setLength(buffer.length);
        socket.receive(datagram);
        if (!(datagram.getAddress().equals(IP_address)) && (datagram.getPort() != serverPort)) {
            throw new SocketTimeoutException();
        }
        return new String(datagram.getData(), datagram.getOffset(), datagram.getLength());
    }
    
    public void comandoNonValido() throws IOException {
        DatagramPacket datagram = new DatagramPacket(new byte[1024], 1024);
        socket.receive(datagram);
        if (!(datagram.getAddress().equals(IP_address)) && (datagram.getPort() != serverPort)) {
            throw new SocketTimeoutException();
        }
        System.out.println(new String(datagram.getData(), datagram.getOffset(), datagram.getLength()));
    }

    public void closeSocket() {
        socket.close();
    }
    
}


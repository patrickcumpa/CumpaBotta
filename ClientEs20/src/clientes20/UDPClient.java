package clientes20;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 *
 * @author TommasoBotta
 */
public class UDPClient {

    private DatagramSocket socket;
    private InetAddress IP_address;
    private int serverPort;

    public UDPClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        IP_address = InetAddress.getByName("localhost");
        serverPort = 12345;
    }
    
    public String sendAndReceiveData(int opzione, int id) throws IOException {

        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES * 2);
        output.putInt(opzione);
        output.putInt(id);

        DatagramPacket datagram = new DatagramPacket(output.array(), Integer.BYTES * 2, IP_address, serverPort);
        socket.send(datagram);

        byte[] buffer = new byte[1024];

        datagram = new DatagramPacket(buffer, buffer.length);
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
        return new String(datagram.getData(), datagram.getOffset(), datagram.getLength(), "ISO-8859-1");
    }
    
    public void chiudi(int opzione) throws IOException {
        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES * 2);
        output.putInt(opzione);
        output.putInt(0);

        DatagramPacket datagram = new DatagramPacket(output.array(), Integer.BYTES * 2, IP_address, serverPort);
        socket.send(datagram);
        socket.close();
    }

}

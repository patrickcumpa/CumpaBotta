package distanceserver;

import java.io.*;
import java.net.*;
import java.nio.*;
 

/**
 *
 * @author TommasoBotta
 */
public class DistanceServer extends Thread {
    // costanti per trasformazione valori latitudine/longitudine in coordinate cartesiane UTM nord/est (sistema WGS-84)
    private static final double PI = 3.141592653589793;
    private static final double WGS84_E2 = 0.006694379990197;
    private static final double WGS84_E4 = WGS84_E2*WGS84_E2;
    private static final double WGS84_E6 = WGS84_E4*WGS84_E2;
    private static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
    private static final double WGS84_SEMI_MINOR_AXIS = 6356752.314245;
    private static final double UTM_LONGITUDE_OF_ORIGIN = 3.0/180.0*PI;
    private static final double UTM_LATITUDE_OF_ORIGIN = 0.0;
    private static final double UTM_FALSE_EASTING = 500000.0;
    private static final double UTM_FALSE_NORTHING_N = 0.0;
    private static final double UTM_FALSE_NORTHING_S = 10000000.0;
    private static final double UTM_SCALE_FACTOR = 0.9996;
    
    private double m_calc(double latitude) {
        return (1.0 - WGS84_E2/4.0 - 3.0*WGS84_E4/64.0 - 5.0*WGS84_E6/256.0)*latitude - (3.0*WGS84_E2/8.0 + 3.0*WGS84_E4/32.0 + 45.0*WGS84_E6/1024.0)*Math.sin(2.0*latitude) + (15.0*WGS84_E4/256.0 + 45.0*WGS84_E6/1024.0)*Math.sin(4.0*latitude) - (35.0*WGS84_E6/3072.0)*Math.sin(6.0*latitude);
    }

    // metodo di calcolo della coordinata UTM nord
    private double north(double lat, double lon) {
        double north;
        int int_zone;
        double M, M_origin, A, A2, e2_prim, C, T, v;
        
        int_zone = (int)(lon/6.0);
        if ( lon < 0) {
            int_zone = int_zone - 1;
        }
        lon -= (double)(int_zone)*6.0;
        lon *= PI/180.0;
        lat *= PI/180.0;
        M = WGS84_SEMI_MAJOR_AXIS*m_calc(lat);
        M_origin = WGS84_SEMI_MAJOR_AXIS * m_calc(UTM_LATITUDE_OF_ORIGIN);
        A = (lon - UTM_LONGITUDE_OF_ORIGIN)*Math.cos(lat);
        A2 = A*A;
        e2_prim = WGS84_E2/(1.0 - WGS84_E2);
        C = e2_prim*Math.pow(Math.cos(lat), 2.0);
        T = Math.tan(lat);
        T *= T;
        v = WGS84_SEMI_MAJOR_AXIS/Math.sqrt(1.0 - WGS84_E2*Math.pow(Math.sin(lat), 2.0));
        north = UTM_SCALE_FACTOR*(M - M_origin + v*Math.tan(lat)*(A2/2.0 + (5.0 - T + 9.0*C + 4.0*C*C)*A2*A2/24.0 + (61.0 - 58.0*T + T*T + 600.0*C - 330.0*e2_prim)*A2*A2*A2/720.0));
        if (lat < 0) {
            north += UTM_FALSE_NORTHING_S;
        }
        return north;
    }

    // metodo di calcolo della coordinata UTM est
    private double east(double lat, double lon) {
        double east;
        int int_zone;
        double M, M_origin, A, A2, e2_prim, C, T, v;
        int_zone = (int)(lon/6.0);
        if ( lon < 0) {
            int_zone = int_zone - 1;
        }
        lon -= (double)(int_zone)*6.0;
        lon *= PI/180.0;
        lat *= PI/180.0;
        M = WGS84_SEMI_MAJOR_AXIS*m_calc(lat);
        M_origin = WGS84_SEMI_MAJOR_AXIS * m_calc(UTM_LATITUDE_OF_ORIGIN);
        A = (lon - UTM_LONGITUDE_OF_ORIGIN)*Math.cos(lat);
        A2 = A*A;
        e2_prim = WGS84_E2/(1.0 - WGS84_E2);
        C = e2_prim*Math.pow(Math.cos(lat),2.0);
        T = Math.tan(lat);
        T *= T;
        v = WGS84_SEMI_MAJOR_AXIS/Math.sqrt(1.0 - WGS84_E2 * Math.pow(Math.sin(lat),2.0));
        east = UTM_FALSE_EASTING + UTM_SCALE_FACTOR*v*(A + (1.0 - + C)*A2*A/6.0 + (5.0 - 18.0*T + T*T + 72.0*C - 58.0*e2_prim)*A2*A2*A/120.0);
        return east;
    }

    private DatagramSocket socket;
    
    // costruttore (richiede il numero di porta del servizio)
    public DistanceServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setSoTimeout(1000); // 1000ms = 1s
    }

    public void run() {
        byte[] buffer = new byte[1024];
        ByteBuffer data;
        DatagramPacket answer, request;
        float lat1, lon1, lat2, lon2;
        double north1, east1, north2, east2;
        double distance;

        while (!Thread.interrupted()) {
            try {
                request = new DatagramPacket(buffer, buffer.length);
                // attesa ricezione datagram di richiesta (tempo massimo di attesa: 1s)
                socket.receive(request);
                // incapsulazione del buffer della richiesta in un byte-buffer della dimensione di 4 valori float
                data = ByteBuffer.wrap(buffer, 0, 16);
                // estrazione dei 4 valori float dal byte-buffer
                lat1 = data.getFloat();
                lon1 = data.getFloat();
                lat2 = data.getFloat();
                lon2 = data.getFloat();
                // calcolo delle coordinate UTM nord/est dai valori di latitudine/longitudine
                north1 = north(lat1, lon1);
                east1 = east(lat1, lon1);
                north2 = north(lat2, lon2);
                east2 = east(lat2, lon2);
                // calcolo della distanza cartesiana tra i due punti
                distance = Math.sqrt(Math.pow(north1-north2, 2) +
                Math.pow(east1-east2,2));
                // incapsulazione del buffer della risposta in un byte-buffer della dimensione di 1 valore double
                data = ByteBuffer.wrap(buffer, 0, 8);
                // inserimento del valore double nel byte-buffer
                data.putDouble(distance);
                // costruzione del datagram da trasmettere a partire dal contenuto del byte-buffer
                answer = new DatagramPacket( data.array(), 8, request.getAddress(),
                request.getPort()); socket.send(answer);
                socket.send(answer);
            }
            catch (SocketTimeoutException exception) {
            }
            catch (IOException exception) {
            }
        }
        socket.close(); // chiusura del socket
    }

    public static void main(String[] args) {
        int c;

        try {
            DistanceServer server = new DistanceServer(12345);
            server.start();
            c = System.in.read();
            server.interrupt();
            server.join();
        }
        catch (IOException exception) {
            System.err.println("Errore!");
        }
        catch (InterruptedException exception) {
            System.err.println("Errore!");
        }
    }
}
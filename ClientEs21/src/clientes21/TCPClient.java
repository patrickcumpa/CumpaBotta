package clientes21;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TommasoBotta
 */
public class TCPClient implements Runnable {

    private final Socket socket;
    private final DataInputStream dataIn;
    private final DataOutputStream dataOut;

    public TCPClient() throws IOException {
        this.socket = new Socket("localhost", 13);
        this.dataIn = new DataInputStream((socket.getInputStream()));
        this.dataOut = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        String risposta;
        boolean loop = true;
        final String[] caffe = {"Arabica(1)", "Robusta(2)", "Liberica(3)", "Excelsa(4)"};
        final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (loop) {
            try {
                System.out.println("1. Vota caffe'");
                System.out.println("2. Richiedi voto medio");
                System.out.println("3. Esci");
                System.out.print("Effettuare una scelta: ");
                int scelta = Integer.parseInt(input.readLine());
                dataOut.writeInt(scelta);

                switch (scelta) {
                    case 1:
                        risposta = dataIn.readUTF();
                        if (risposta.contains("pieno")) {
                            System.out.println(risposta);
                            break;
                        }
                        System.out.print(risposta);
                        String nome = input.readLine();
                        dataOut.writeUTF(nome);
                        int i = 0;
                        while (i < caffe.length) {
                            System.out.print("Caffe' " + caffe[i] + ": ");
                            int voto = Integer.parseInt(input.readLine());
                            if (voto < 1 || voto > 10) {
                                System.out.println("\nPer favore, inserire un voto tra 1 e 10\n");
                                continue;
                            }
                            dataOut.writeInt(voto);
                            i++;
                        }
                        System.out.println(dataIn.readUTF());
                        break;

                    case 2:
                        for (String value : caffe) {
                            System.out.print(value + " | ");
                        }

                        System.out.print("\nPer favore, inserire il codice del caffe': ");
                        int codice = Integer.parseInt(input.readLine());
                        for (int j = 1; j < caffe.length + 1; j++) {
                            if (codice == j) {
                                dataOut.writeInt(j);
                                break;
                            }
                        }
                        System.out.printf("Voto medio: %.2f\n", dataIn.readDouble());
                        break;

                    case 3:
                        loop = false;
                        break;

                    default:
                        System.out.println(dataIn.readUTF());
                }
            } catch (IOException ex) {
                System.err.println("Errore nella comunicazione con il server: " + ex);
            }
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomSocket = new ServerSocket(6789);
        Socket connectionSocket = welcomSocket.accept();

        BufferedReader input = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        while (true) {
            String tekst = input.readLine();
            System.out.println(tekst);
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            outToClient.writeBytes("Ekko: " + tekst + '\n');
        }

        //Input i = new Input(connectionSocket);
        //i.start();
        //Output o = new Output(connectionSocket);
        //o.start();

    }
}

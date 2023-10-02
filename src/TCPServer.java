import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            (new ServerThread(connectionSocket)).start();
        }

        //Input i = new Input(connectionSocket);
        //i.start();
        //Output o = new Output(connectionSocket);
        //o.start();

    }
}

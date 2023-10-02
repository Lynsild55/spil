import java.net.ServerSocket;
import java.net.Socket;

public class SpilServer {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomSocket = new ServerSocket(6789);
        Socket connectionSocket = welcomSocket.accept();


        ServerInput i = new ServerInput(connectionSocket);
        i.start();
        ServerOutput o = new ServerOutput(connectionSocket);
        o.start();

    }
}

import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomSocket = new ServerSocket(6789);
        Socket connectionSocket = welcomSocket.accept();

        Input i = new Input();
        i.start();
        Output o = new Output();
        o.start();

    }
}

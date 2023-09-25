import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) {
        Socket clientSocket =new Socket();
        Output output = new Output(clientSocket);
        output.start();
        Input input = new Input(clientSocket);
        input.start();
    }
}

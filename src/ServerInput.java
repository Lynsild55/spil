import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerInput extends Thread {

    private Socket socket;
    private String clientSentence;

    public ServerInput(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientSentence = inFromClient.readLine();
                System.out.println(clientSentence);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

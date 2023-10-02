import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket connectionSocket;

    public ServerThread(Socket connSocket) {
        this.connectionSocket = connSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

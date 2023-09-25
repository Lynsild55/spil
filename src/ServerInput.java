import java.net.Socket;

public class ServerInput extends Thread {

    private Socket socket;

    public ServerInput(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {}
}

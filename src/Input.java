import java.net.Socket;

public class Input extends Thread {

    private Socket socket;

    public Input(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {}

}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Output extends Thread {

    private Socket socket;

    public Output(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {}
}


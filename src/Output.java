import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Output extends Thread {

    private Socket socket;

    public Output(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                BufferedReader serverSentence = new BufferedReader(new InputStreamReader(System.in));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                String sentence = serverSentence.readLine() + '\n';
                outToClient.writeBytes(sentence);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}


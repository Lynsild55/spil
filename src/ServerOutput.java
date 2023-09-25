import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ServerOutput extends Thread {

    private ArrayList<Socket> players = new ArrayList<>();

    public void addPlayer(Socket socket) {
        if (!players.contains(socket)) {
            players.add(socket);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                BufferedReader serverSentence = new BufferedReader(new InputStreamReader(System.in));
                for (Socket s: players) {
                    DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
                    String sentence = serverSentence.readLine() + '\n';
                    outToClient.writeBytes(sentence);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

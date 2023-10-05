import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ServerThread extends Thread {
    private Socket connectionSocket;
    private ArrayList<Socket> connections;
    private LinkedList<String> queue = new LinkedList<>();
    private TCPServer server;

    public ServerThread(Socket connectionSocket, ArrayList<Socket> connections, TCPServer server) {
        this.connectionSocket = connectionSocket;
        this.connections = connections;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            while (true) {
                String move = inFromClient.readLine();
                System.out.println(move);
                server.sendMessage(move);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

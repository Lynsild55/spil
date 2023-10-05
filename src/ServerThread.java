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

    public ServerThread(Socket connectionSocket, ArrayList<Socket> connections) {
        this.connectionSocket = connectionSocket;
        this.connections = connections;
    }

    @Override
    public synchronized void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            while (true) {
                String move = inFromClient.readLine();

                    for (Socket socket : connections) {
                        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                        System.out.println(move);
                        outToClient.writeBytes(move + "\n");
                    }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

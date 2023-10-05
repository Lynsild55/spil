import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

public class TCPServer {
        private ArrayList<Socket> connections = new ArrayList<>();

    public TCPServer() {
    }

    public static void main(String[] args) throws Exception {
        TCPServer tcpServer = new TCPServer();
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            tcpServer.connections.add(connectionSocket);
            (new ServerThread(connectionSocket, tcpServer.connections, tcpServer)).start();
        }

    }

    public synchronized void sendMessage(String msg) throws IOException {
        for (Socket socket : connections) {
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            outToClient.writeBytes(msg + "\n");
        }
    }
}

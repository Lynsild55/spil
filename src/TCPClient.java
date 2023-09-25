import javafx.application.Application;

import java.io.IOException;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) throws Exception {
        Socket clientSocket =new Socket("localhost", 6789);
        Output output = new Output(clientSocket);
        output.start();
        Input input = new Input(clientSocket);
        input.start();

        Application.launch(GUI.class);
    }
}

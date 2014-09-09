package echoserver;

import echoclient.EchoClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author jakobgaardandersen
 */
public class ClientHandler {

    private Scanner input;
    private PrintWriter output;
    private Socket socket;
    private EchoClient client;
    private String username;

    public Scanner connect(EchoClient client, String ip, int port, String user) throws IOException {
        socket = new Socket(ip, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        this.client = client;
        this.username = user;
        return input;
    }

    public void send(String msg, String receiver) {
        String message = "SEND#" + receiver + "#" + msg;
        output.println(message);
    }

    public void sendConnectMsg() {
        output.println("CONNECT#" + this.username);
    }

    public void disconnect() {
        output.println("CLOSE#");
    }

    public boolean handleMessage(String msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}

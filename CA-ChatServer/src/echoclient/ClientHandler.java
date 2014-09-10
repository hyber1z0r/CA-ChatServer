package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        this.socket = new Socket(ip, port);
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.client = client;
        this.username = user;
        return input;
    }

    public void send(String msg, String receiver) {
        String message = "SEND#" + receiver + "#" + msg;
        System.out.println("Sending message(clienthandler): " + message);
        output.println(message);
    }

    public void sendConnectMsg() {
        output.println("CONNECT#" + this.username);
    }

    public void disconnect() {
        output.println("CLOSE#");
    }

    public boolean handleMessage(String msg) {
        System.out.println("Client got message: " + msg);
        String[] protocols = msg.split("#");
        switch (protocols[0]) {
            case "MESSAGE":
                client.notifyListeners(new Message(this, protocols[1], protocols[2], MessageType.textmessage));
                return true;
                /// array out of bound. ONLINE er = ONLINE#Navn   altså ingen besked, kun 1 hashtag
            case "ONLINE":
                client.notifyListeners(new Message(this, protocols[1], MessageType.online));
                return true;
            case "CLOSE":
                try {
                    disconnect();
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            default:
                return false;
        }
    }
}

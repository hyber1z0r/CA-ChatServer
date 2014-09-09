package echoserver;

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
public class ClientHandler extends Thread {

    private final Scanner input;
    private final PrintWriter output;
    private final Socket socket;

    public ClientHandler(Socket socket) throws IOException {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String message = input.nextLine(); //IMPORTANT blocking call
            Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {
                String[] protocols = message.split("#");
                switch (protocols[1]) {
                    case "*":
                        EchoServer.send(protocols[2]);
                        break;
                    default:
                        EchoServer.send(protocols[2], protocols[1]);
                }
                EchoServer.send(message);
                Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
                message = input.nextLine(); //IMPORTANT blocking call
            }
            output.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            EchoServer.removeHandler(this);
            socket.close();
            Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, "Closed a Connection");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void send(String msg) {
        output.println(msg);
    }
}

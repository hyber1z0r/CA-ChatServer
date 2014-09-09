package echoclient;

import echoserver.ClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EchoClient extends Thread {

    private Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private final List<EchoListener> listeners = new ArrayList();
    private String username;
    private ClientHandler handler = new ClientHandler();

    public void connect(String address, int port, String name) throws UnknownHostException, IOException {
        this.input = handler.connect(this, address, port, username);
        start();
        handler.sendConnectMsg();
    }

    public void send(String msg, String receiver) {
        handler.send(msg, receiver);
    }

    public void disconnect() {
        handler.disconnect();
    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            String msg = input.nextLine();
            keepRunning = handler.handleMessage(msg);
        }
    }

    public void registerEchoListener(EchoListener l) {
        listeners.add(l);
    }

    public void unRegisterEchoListener(EchoListener l) {
        for (EchoListener el : listeners) {
            if (el.equals(l)) {
                listeners.remove(el);
                break;
            }
        }
    }

    public void notifyListeners(Message msg) {
        for (EchoListener el : listeners) {
            el.messageArrived(msg);
        }
    }

    public String getUsername() {
        return username;
    }

}

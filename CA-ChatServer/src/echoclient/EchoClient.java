package echoclient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EchoClient extends Thread {

    private Scanner input;
    private final List<EchoListener> listeners = new ArrayList();
    private String username;
    private final ClientHandler handler = new ClientHandler();

    public void connect(String address, int port, String name) throws UnknownHostException, IOException {
        this.username = name;
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
        System.out.println("Startet echoclient");
        while (keepRunning) {
            // den bliver hængende forevigt! 
            String msg = input.nextLine(); // important blocking call
            System.out.println("Message Received: " + msg);
            keepRunning = handler.handleMessage(msg);
        }
        System.out.println("EchoClient exited run");
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

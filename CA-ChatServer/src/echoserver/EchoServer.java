package echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import utils.Utils;

public class EchoServer extends Thread {

    private static int port;
    private static String address;
    private static String logFile;
    private ConcurrentHashMap<String, Handler> handlers = new ConcurrentHashMap();
    private static final Properties properties = Utils.initProperties("server.properties");

    public void run() {
        new EchoServer();
        main(null);
    }

    void addHandler(echoserver.Handler handler) {
        if (handler.getUsername() == null) {
            handler.sendStop();
            return;
        }
        handlers.put(handler.getUsername(), handler);
    }

    void sendOnline() {
        StringBuilder onlineUsers = new StringBuilder();
        boolean first = true;
        for (String user : handlers.keySet()) {
            if (!first) {
                onlineUsers.append(",");
            }
            onlineUsers.append(user);
            first = false;
        }
        for (Handler client : handlers.values()) {
            client.sendOnline(onlineUsers.toString());
        }
    }

    void closeClient(String user) {
        Handler client = (Handler) handlers.get(user);
        client.sendStop();
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        handlers.remove(user);
        sendOnline();
    }

    void sendMessage(String receivers_, String msg, String sender) {
        String messageString = "MESSAGE#" + sender + "#" + msg;
        String[] receivers = receivers_.split(",");
        if (receivers.length == 1) {
            String receiver = receivers[0];
            if (receiver.equals("*")) {
                for (Handler client : handlers.values()) {
                    client.sendMsg(messageString);
                }
            } else {
                sendMessageToClient(receiver, messageString, sender);
            }
        } else {
            for (String receiver : receivers) {
                receiver = receiver.replaceAll("\\s", "");
                sendMessageToClient(receiver, messageString, sender);
            }
        }
    }

    public void listen(ServerSocket serverSocket) {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connected to a client");
                new Handler(this, socket).start();
            }
        } catch (IOException ex) {
        }
    }

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(properties.getProperty("port"));
            String ip = properties.getProperty("serverIp");
            String logFile = properties.getProperty("logFile");
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Server started");
            Utils.setLogFile(logFile, EchoServer.class.getName());
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            EchoServer server = new EchoServer();
            server.listen(serverSocket);

            for (java.util.logging.Handler h : Logger.getLogger(EchoServer.class.getName()).getHandlers()) {
                h.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, "Connection error: ", ex);
        }
    }

    private void sendMessageToClient(String receiver, String messageString, String sender) {
        Handler handler = (Handler) handlers.get(receiver);
        handler.sendMsg(messageString);

    }

}

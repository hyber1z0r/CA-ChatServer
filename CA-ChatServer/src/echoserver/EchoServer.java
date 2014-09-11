package echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

public class EchoServer extends Thread {

    private final ConcurrentHashMap<String, Handler> handlers = new ConcurrentHashMap();
    private static final Properties properties = Utils.initProperties("server.properties");

    @Override
    public void run() {
        main(null);
    }

    public void addHandler(echoserver.Handler handler) {
        if (handler.getUsername() == null) {
            handler.sendStop();
            return;
        }
        handlers.put(handler.getUsername(), handler);
    }

    public void sendOnlineUsersMsg() {
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
            System.out.println(onlineUsers);
            client.sendOnline(onlineUsers.toString());
        }
    }

    public void closeClient(String user) {
        Handler client = (Handler) handlers.get(user);
        client.sendStop();
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        handlers.remove(user);
        sendOnlineUsersMsg();
    }

    public void sendMessage(String receivers, String msg, String sender) {
        String messageString = "MESSAGE#" + sender + "#" + msg;
        String[] receiversarray = receivers.split(",");
        if (receiversarray.length == 1) {
            String receiver = receiversarray[0];
            if (receiver.equals("*")) {
                for (Handler handler : handlers.values()) {
                    handler.sendMessage(messageString);
                }
            } else {
                Handler handler = (Handler) handlers.get(receiver);
                handler.sendMessage(messageString);
            }
        }
    }

    public void listen(ServerSocket serverSocket) {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
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
}

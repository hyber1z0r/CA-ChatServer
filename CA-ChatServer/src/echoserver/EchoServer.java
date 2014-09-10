package echoserver;

import echoclient.Message;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private static final List<Socket> sockets = new ArrayList();

    public static void stopServer() {
        keepRunning = false;
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");
        String logFile = properties.getProperty("logFile");
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Server started");
        Utils.setLogFile(logFile, EchoServer.class.getName());
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                sockets.add(socket);
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.closeLogger(EchoServer.class.getName());
        }
    }
    
    public static void removeSocket(Socket s){
        for (Socket socket : sockets) {
            if(socket.equals(s)){
                sockets.remove(socket);
                break;
            }
        }
    }
    
    public static void send(Message msg) throws IOException{
        for (Socket s : sockets) {
            PrintWriter output = new PrintWriter(s.getOutputStream(), true);
            output.println(msg);
        }
    }
}

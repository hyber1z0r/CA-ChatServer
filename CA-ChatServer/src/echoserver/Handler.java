/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Filipovic
 */
public class Handler extends Thread {

    private final Socket socket;
    private final PrintWriter output;
    private final Scanner input;
    private final EchoServer server;
    private String username;
    private boolean keepRunning = true;

    public Handler(EchoServer server, Socket s) throws IOException {
        this.server = server;
        this.socket = s;
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream(), true);

    }

    public String getUsername() {
        return username;
    }

    public void sendOnline(String onlineUsers) {
        String msg = "ONLINE#" + onlineUsers;
        output.println(msg);
    }

    public void close() throws IOException {
        socket.close();
    }

    public void sendStop() {
        output.println("CLOSE#");
    }

    public void sendMessage(String str) {
        output.println(str);
    }

    @Override
    public void run() {
        try {
            while (keepRunning) {
                String message = input.nextLine();
                String[] protocols = message.split("#");

                switch (protocols[0]) {
                    case "CONNECT":
                        username = protocols[1];
                        server.addHandler(this);
                        server.sendOnlineUsersMsg();
                        break;
                    case "SEND":
                        String receivers = protocols[1];
                        String msg = protocols[2];
                        server.sendMessage(receivers, msg, username);
                        break;
                    case "CLOSE":
                        server.closeClient(username);
                        keepRunning = false;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

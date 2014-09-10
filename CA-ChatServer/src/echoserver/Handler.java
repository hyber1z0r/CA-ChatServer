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

    private Socket socket;
    private PrintWriter output;
    private Scanner input;
    private EchoServer server;
    private String username;
    private boolean keepRunning = true;

    public Handler(EchoServer server, Socket s) throws IOException {
        this.server = server;
        this.socket = s;
        this.input = new Scanner(this.socket.getInputStream());
        this.output = new PrintWriter(this.socket.getOutputStream());

    }

    public String getUsername() {
        return username;
    }

    public void sendOnline(String onlineUsers) {

        String msg = "ONLINE#" + onlineUsers;
        this.output.println(msg);
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public void sendStop() {
        this.output.println("CLOSE#");
    }

    public void sendMsg(String str) {
        this.output.println(str);
    }

    public void run() {
        try {
            while (keepRunning) {
                String msg = input.nextLine();
                String[] protocols = msg.split("#");

                switch (protocols[0]) {
                    case "CONNECT":
                        this.username = protocols[1];
                        this.server.addHandler(this);
                        this.server.sendOnline();
                        break;
                    case "SEND":

                        String recepients = protocols[1];
                        String message = protocols[2];
                        this.server.sendMessage(recepients, message, this.username);
                        break;
                    case "CLOSE":

                        this.server.closeClient(this.username);
                        this.keepRunning = false;

                        break;
                }
            }
        } catch (Exception e) {

        }
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Http;

import com.sun.net.httpserver.*;
import echoserver.EchoServer;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Neno
 */
public class HttpServer extends Thread
{

    static int port = 8080;
    static String ip = "100.85.76.5";
    static String contentFolder = "public/";

    public static void main(String[] args) throws Exception
    {
        if (args.length == 3)
        {
            port = Integer.parseInt(args[1]);
            ip = args[0];
            contentFolder = args[2];
        }
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(ip, port), 0);
        server.createContext("/Online", new RequestHandler());
        server.createContext("/", new RequestHandler2());
        server.createContext("/bootstrap.min.css", new CSSRequestHandler());
        server.createContext("/Chatlog", new ChatlogRequestHandler());
        server.createContext("/CA-ChatClient", new DownloadRequestHandler());
        
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    @Override
    public void run()
    {
        try
        {
            main(null);
        } catch (Exception ex)
        {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static class RequestHandler implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            String path = contentFolder + "onlineusers.html";
            File file = new File(path);
            Path path1 = Paths.get(path);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path1), charset);
            content = content.replaceAll("<h1>Online users:</h1>", "<h1>Online users: " + EchoServer.getOnlineUsers() + "</h1>");
            Files.write(path1, content.getBytes(charset));

            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
            String contentType = "text/html";
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
                
            }
        }

        
    }
    
     static class RequestHandler2 implements HttpHandler
     {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            String path = contentFolder + "index.html";
            File file = new File(path);
            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
            String contentType = "text/html";
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
                
            }
        }        
         
     }
     static class CSSRequestHandler implements HttpHandler
     {
         @Override
         public void handle(HttpExchange he) throws IOException
        {
            String path = contentFolder + "bootstrap.min.css";
            File file = new File(path);
            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
            String contentType = "text/css";
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
                
            }
        }  
     }
     
     
     static class DownloadRequestHandler implements HttpHandler
     {
         @Override
         public void handle(HttpExchange he) throws IOException
        {
            String path = "dist/CA-ChatServer.jar";
            File file = new File(path);
            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
            String contentType = "application/java-archive";
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
                
            }
        }  
     }
     
     static class ChatlogRequestHandler implements HttpHandler
     {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            String path = contentFolder + "Chatlog.html";
            File file = new File(path);
            String ChatPath = "chatLog.txt"; 
            Scanner scan = new Scanner(new File(ChatPath));
            StringBuilder sb = new StringBuilder();
            while (scan.hasNext())
            {
              sb.append("<tr><td>");
              sb.append(scan.nextLine());
              sb.append("</td><td>");
              sb.append(scan.nextLine());
              sb.append("</td><tr>");
            }
            sb.append("</tr>");
            Path path1 = Paths.get(path);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path1), charset);
            content = content.replaceAll("###DATA###", sb.toString());
            Files.write(path1, content.getBytes(charset));
            scan.close();
            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
            String contentType = "text/html";
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
                
            }
        }        
         
     }
     
        static class DocumentationRequestHandler implements HttpHandler
     {
         @Override
         public void handle(HttpExchange he) throws IOException
        {
            String path = "dist/Documentation.pdf";
            File file = new File(path);
            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
            String contentType = "application/java-archive";
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
                
            }
        }  
     }
}

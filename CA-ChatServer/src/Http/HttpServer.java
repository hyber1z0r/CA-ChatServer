/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 *
 * @author Neno
 */
public class HttpServer
{

    static int port = 8080;
    static String ip = "127.0.0.1";
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
        server.createContext("/pages", new RequestHandler());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    static class RequestHandler implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            String filepath = he.getRequestURI().toString();
            String path = contentFolder + filepath.replaceAll("/pages", "");
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
            String contentType = getMime(filepath);
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", contentType);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
            }
        }

        private String getMime(String s)
        {
            String contentType;
            int dot = s.indexOf(".");
            String filetype = s.substring(dot + 1, s.length());
            switch (filetype)
            {
                case "jpg":
                case "gif":
                case "png":
                case "jpeg":
                case "bmp":
                    contentType = "image";
                    break;
                case "pdf":
                case "zip":
                    contentType = "application";
                    break;
                case "css":
                case "html":
                case "javascript":
                case "rtf":
                case "xml":
                    contentType = "text";
                    break;
                default:
                    contentType = "text";
            }
            return contentType + "/" + filetype;
        }
    }
}

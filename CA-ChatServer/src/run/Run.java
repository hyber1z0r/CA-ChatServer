/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import Http.HttpServer;
import echoserver.EchoServer;

/**
 *
 * @author Filipovic
 */
public class Run {
    
    public static void main(String[] args) throws Exception {
        new EchoServer().start();
        new HttpServer().run();
    }
    
    
}

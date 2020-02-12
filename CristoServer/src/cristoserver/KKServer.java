/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author alejandronieto
 */
public class KKServer extends Thread{
    
    int portNumber;
    boolean listening;
    
    ArrayList<KKMultiServerThread> conexiones;
    
    KKServer(int port){
        super();
        portNumber = port;
        listening = true;
        conexiones = new ArrayList();
        
    }
    
    @Override
    public void run(){
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
	            conexiones.add(new KKMultiServerThread(serverSocket.accept())); 
                    conexiones.get(conexiones.size() - 1).start(); 
                    CristoServer.debug("Conexion aceptada");
                    System.out.println("Conexion aceptada");
	        }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    
}

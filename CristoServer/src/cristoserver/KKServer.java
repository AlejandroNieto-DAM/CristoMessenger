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
public class KKServer {
    
    int portNumber;
    boolean listening;
    
    ArrayList<KKMultiServerThread> conexiones;
    
    KKServer(int port){
            portNumber = port;
            listening = true;
            conexiones = new ArrayList();
    }
    
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
	            conexiones.add(new KKMultiServerThread(serverSocket.accept()));
                    CristoServer.debug("Conexion aceptada");
                    System.out.println("Conexion aceptada");
                    conexiones.get(conexiones.size() - 1).start();
	        }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    
}

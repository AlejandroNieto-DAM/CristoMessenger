/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Controllers.KKMultiServerThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author alejandronieto
 */
public class KKServer extends Thread{
    
    int portNumber;
    boolean listening;
    
    ServerSocket serverSocket;

    ArrayList<KKMultiServerThread> conexiones;
    CristoServer myCS;
    

    KKServer(int port, CristoServer myCS){
        super();
        portNumber = port;
        listening = true;
        conexiones = new ArrayList();
        this.myCS = myCS;
        
    }
    
    @Override
    public void run(){
        try { 
            serverSocket = new ServerSocket(portNumber);
            while (listening) {
	            conexiones.add(new KKMultiServerThread(serverSocket.accept(), this, myCS.getLeidos(), myCS.getEncrypt())); 
                    conexiones.get(conexiones.size() - 1).start(); 
                    CristoServer.debug("Conexion aceptada");
                    System.out.println("Conexion aceptada");
	        }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    
    public void parar() throws IOException, SQLException{
        for(int i = 0; i < conexiones.size(); i++){
            conexiones.get(i).getProtocol().setDisconnected();
            conexiones.get(i).stop();
        }
        
        serverSocket.close();
        this.stop();
        
    }
    
    
    public int getHebrasSize(){
        return conexiones.size();
    }
       
    public KKMultiServerThread getConexionAt(int i){
        return conexiones.get(i);
    }
    
}

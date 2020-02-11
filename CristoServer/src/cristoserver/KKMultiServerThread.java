/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Controllers.User_Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandronieto
 */
public class KKMultiServerThread extends Thread{
     private Socket socket = null;
     KnockKnockProtocol kkp;
          

    public KKMultiServerThread(Socket socket, KnockKnockProtocol mainProtocol) {
        super("KKMultiServerThread");
        this.socket = socket;
        this.kkp = mainProtocol;
    }
    
    public void run() {

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine = ""; 
            String outputLine = "";
            
            kkp.setPrintWriter(out);
            kkp.setBufferedReader(in);
            
            try{
                while ((inputLine = in.readLine()) != null) {
                
                    CristoServer.debug("FROMCLIENT " + inputLine);
                    //System.out.println(inputLine);

                    if(inputLine.contains("OK_SEND!")){
                        
                        for(int i = 0; i < kkp.contadorMsg; i++){
                            outputLine = kkp.sendMsg(i);
                            out.println(outputLine);
                        }
                        
                        inputLine = in.readLine();
                        CristoServer.debug("FROMCLIENT " + outputLine);

                        
                    } else {
                        outputLine = kkp.processInput(inputLine);
                        out.println(outputLine);
                    }
                    

                    CristoServer.debug("FROMSERVER " + outputLine);
                    //System.out.println(outputLine);

                    


                    if (outputLine.contains("BAD_LOGIN") || outputLine == null)
                        break;
                
                }
            } catch(SocketException e){
                
                
                
            }
            
//            System.out.println("DESCONECTO AL USUARIO");
            kkp.setDisconnected();
            socket.close();
            
    
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
             Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
}

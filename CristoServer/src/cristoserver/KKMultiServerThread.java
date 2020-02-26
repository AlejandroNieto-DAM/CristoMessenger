/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandronieto
 */
public class KKMultiServerThread extends Thread{
    
     private Socket socket = null;
     KnockKnockProtocol kkp;
     KKServer myKKS;
     
     String login = "";
     PrintWriter out;
     BufferedReader in;
     String inputLine = ""; 
            String outputLine = "";
     
     private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     Timestamp timestamp = new Timestamp(System.currentTimeMillis());

     
    public KKMultiServerThread(Socket socket, KKServer myKKS) {
        super("KKMultiServerThread");
        this.socket = socket;
        
        this.kkp = new KnockKnockProtocol();
        
        this.myKKS = myKKS;
    }
    
    public void run() {
        
        try {
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                     new InputStreamReader(
                             socket.getInputStream()));
            
            try{
                while ((inputLine = in.readLine()) != null) {
                    
                    CristoServer.debug("FROMCLIENT " + inputLine);
                    System.out.println("FROMCLIENT " + inputLine);

                    this.filtrado(inputLine);
                    
                    
                    
                    if (outputLine.contains("BAD_LOGIN"))
                        break;
                }
            } catch(SocketException e){
                                  
            } catch (FileNotFoundException | InterruptedException ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            kkp.setDisconnected();
            socket.close();
            
    
        } catch (IOException e) {
        } catch (SQLException ex) {
             Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void filtrado(String inputLine) throws FileNotFoundException, IOException, SQLException, InterruptedException{
        
        if(inputLine.contains("OK_SEND!") && kkp.contadorPaquetes(inputLine) == 5){

            for(int i = 0; i < kkp.contadorMsg; i++){
                outputLine = kkp.sendMsg(i);
                out.println(outputLine);
            }

        }else if (inputLine.contains("ALL_RECEIVED")){
           
            out.println("");
            
        } else if (inputLine.contains("PHOTO_RECEIVED")){
           
            out.println("");
            
        } else if(inputLine.contains("CHAT")){
            
            if(inputLine.contains("RECEIVED_MESSAGE")){
                this.sendReceivedMessage(inputLine);
            } else {
                this.sendMessage(inputLine);
            }     

        } else if(inputLine.contains("GET_PHOTO")){

            out.println("PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#STARTING_MULTIMEDIA_TRANSMISSION_TO#" + this.getLogin());
            kkp.loadFile(inputLine);

            while(kkp.getSeparador() > 0 ){
                out.println(kkp.getPhotoUser());
            }
            outputLine = kkp.getPhotoUser();

            out.println("PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#ENDING_MULTIMEDIA_TRANSMISSION#" + this.getLogin());


        } else {
     
            outputLine = kkp.processInput(inputLine);
            out.println(outputLine);

        }

        CristoServer.debug("FROMSERVER " + outputLine);
        //System.out.println(outputLine);                
    }
    
    public String getLogin(){
        return this.kkp.getLogin();
    }
    
    public void sendReceivedMessage(String inputLine){
        
        String[] datos = inputLine.split("#");
        
        outputLine = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#CHAT#" + datos[5] + "#" + kkp.getLogin() + "#MESSAGE_SUCCESFULLY_PROCESSED#" + sdf.format(timestamp);
        out.println(outputLine);
        
    }
    
    public void sendMessage(String inputLine) throws SQLException{
        
        Boolean encontrado = false;
        String loginFriend = this.kkp.getFriend(inputLine);
        
        
        String[] datos = inputLine.split("#");  
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#CHAT#" + datos[4] + "#" + datos[5] + "#" + datos[6] + "#";
        
        for(int i = 0; i < myKKS.getHebrasSize() && !encontrado; i++){ 
            if(this.myKKS.getConexionAt(i).getLogin().equals(loginFriend)){
                PrintWriter outB = this.myKKS.getConexionAt(i).getOutputStream();
                outB.println(cadena + sdf.format(timestamp));
                encontrado = true; 
            }
        }
        
        String tryInsert = "";
        tryInsert = kkp.receiveMessage(inputLine);
        
        if(!tryInsert.contains("Bien")){
            out.println(tryInsert);
        }
  
    }
    
    public PrintWriter getOutputStream(){
        return this.out;
    }
    
    public BufferedReader getInputStream(){
        return this.in;
    }
}

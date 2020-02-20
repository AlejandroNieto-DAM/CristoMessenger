/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Controllers.Friend_Controller;
import Controllers.Message_Controller;
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
                
                    
                    this.filtrado(inputLine);
                    
                    if (outputLine.contains("BAD_LOGIN") || outputLine == null)
                        break;
                   
                
                }
            } catch(SocketException e){
                                  
            }
            
            kkp.setDisconnected();
            socket.close();
            
    
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
             Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    public synchronized void filtrado(String inputLine) throws FileNotFoundException, IOException, SQLException{
        CristoServer.debug("FROMCLIENT " + inputLine);
        //System.out.println(inputLine);

        if(inputLine.contains("OK_SEND!") && kkp.contadorPaquetes(inputLine) == 5){

            for(int i = 0; i < kkp.contadorMsg; i++){
                outputLine = kkp.sendMsg(i);
                out.println(outputLine);
            }

            inputLine = in.readLine();
            CristoServer.debug("FROMCLIENTDEMONIO " + outputLine);


        } else if(inputLine.contains("CHAT")){

            this.sendMessage(inputLine);
            //outputLine = kkp.processInput(inputLine);
            //out.println(outputLine);

        } else if(inputLine.contains("GET_PHOTO")){

            out.println("PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#STARTING_MULTIMEDIA_TRANSMISSION_TO#" + this.getLogin());
            kkp.loadFile(inputLine);

            while(kkp.getSeparador() > 0 ){
                out.println(kkp.getPhotoUser());
            }
            outputLine = kkp.getPhotoUser();

            out.println("PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#ENDING_MULTIMEDIA_TRANSMISSION#" + this.getLogin());

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
    
    public void sendMessage(String inputLine){
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        System.out.println("Tamo activos aqui con nuestro papito");

        for(int i = 0; i < myKKS.getHebrasSize(); i++){
            String loginFriend = this.kkp.getFriend(inputLine);
            System.out.println("Login friend pa mandar --> " + loginFriend);
            if(this.myKKS.getConexionAt(i).getLogin().equals(loginFriend)){
                PrintWriter out = this.myKKS.getConexionAt(i).getOutputStream();
                out.println(inputLine + "#" + sdf.format(timestamp));
                System.out.println("Hacio ejjeje");
            }
        }
    }
    
    public PrintWriter getOutputStream(){
        return this.out;
    }
}

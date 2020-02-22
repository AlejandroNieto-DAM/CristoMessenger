package Controladores;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */

import Classes.RefrescarListaAmigos;
import Vista.CristoMessenger;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import java.util.Base64;
import java.util.ArrayList;

 
public class KnockKnockClient extends Thread{

    int portNumber;
    String hostName;
    String login;
    String pass;
    JFrame loginFrame;
    ClientProtocol protocol;
    CristoMessenger a;
    PrintWriter out;
    Socket kkSocket;
    BufferedReader in;
    
    
  
    LocalDateTime dateTime = LocalDateTime.now();
    
    RefrescarListaAmigos friendRefresh;
    
    int numeroMsgs = 0;
    int totalNumeroMensajes = 0;
    
    public KnockKnockClient(int port, String host, String login, String pass, JFrame frame) throws IOException{
        this.portNumber = port;
        this.hostName = host;
        this.login = login;
        this.pass = pass;
        this.loginFrame = frame;
        a = new CristoMessenger(this);
        protocol = new ClientProtocol(login, pass, a);
        kkSocket = new Socket(hostName, portNumber);
        out = new PrintWriter(kkSocket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        
        friendRefresh = new RefrescarListaAmigos(this);

    }
    
    
    @Override
    public void run(){
 
        String fromServer = "";
        String fromUser = "";

        fromUser = protocol.processInput(null);
        out.println(fromUser);
        
        try {
            while((fromServer = in.readLine()) != null){   
                this.filtrado(fromServer);
            }
        } catch (IOException ex) {
            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    public synchronized void filtrado(String fromServer) throws IOException{
        
        if(fromServer.contains("LOGIN_CORRECT")){

                    protocol.processInput(fromServer);

                    try {
                        this.getPhoto();
                    } catch (IOException ex) {
                        Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    this.a.loadPhoto();
                    this.loginFrame.setVisible(false);
                    this.a.setActualUser(login);
                    this.a.setVisible(true); 
                    //this.friendRefresh.start();  

                }

                if(fromServer.contains("MSGS")){
                    this.getMessagesFrom(fromServer);
                }
                
                if(fromServer.contains("STATUS")){      
                     protocol.processInput(fromServer);
                }
                
                if(fromServer.contains("ALLDATA_USER")){      
                    protocol.processInput(fromServer);
                }
                
                if(fromServer.contains("CHAT")){
                    if(!fromServer.contains("MESSAGE_SUCCESFULLY_PROCESSED")){                      
                        this.addNewMsg(fromServer);
                    }
                    
                }          
    }
    
    
    public void addNewMsg(String fromServer) throws IOException{
        String datos[] = fromServer.split("#");
        out.println("PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#CHAT#RECEIVED_MESSAGE#" + datos[4] + "#TIMESTAMP");

        if(this.a.getFocusFriend().equals(datos[4])){
            protocol.addNewMsg(fromServer);
        }
    }
    
    public synchronized void getPhoto() throws IOException{
        String output = protocol.getPhoto();
        out.println(output);
        ArrayList<String> cadenas = new ArrayList();
        ArrayList<String> decodedBytes = new ArrayList();
        
        String fromServer = in.readLine();
        
        while(!(fromServer = in.readLine()).contains("ENDING_MULTIMEDIA_TRANSMISSION")){
            String datos[] = fromServer.split("#");
            cadenas.add(new String(datos[7]));
        }
        
        System.out.println("Img --> " + fromServer);
        for(String s : cadenas){
            decodedBytes.add(new String(Base64.getDecoder().decode(s.getBytes())));
        }
       
        
        File file = new File("userPhoto.jpg");
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        for (String s : decodedBytes) {
            for (char c : s.toCharArray()) {
                fos.write(c);
            }
        }
        fos.flush();
        fos.close();
    
    }
     
    public synchronized void processMsg(String theInput) throws IOException{
        protocol.processInput(theInput);  
    }
    
    public synchronized void refreshFriends() throws IOException{
        ArrayList<User> friendList = this.a.getFriends();
        
        for(int i = 0; i < friendList.size(); i++){
            String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#STATUS#" + login + "#" + friendList.get(i).getLogin();
            out.println(cadena);
            String estado = this.protocol.friendStatus(in.readLine());
            if(estado.equals("CONNECTED")){
                friendList.get(i).setEstadoUsuario(1);
            } else {
                friendList.get(i).setEstadoUsuario(0);
            }
        }
     
        this.a.setFriendsOf(friendList);
    }
    
    
    public synchronized void sendMessage(String text) throws IOException{
        String output = protocol.sendMessage(text);
        out.println(output);
    }
      
    public synchronized void getFriendStatus() throws IOException{
        String output = protocol.getFriendStatus();
        out.println(output);  
    }
    
    public synchronized void getFriendData() throws IOException{
        String output = protocol.getFriendData();
        out.println(output);
    }
    
    public synchronized void getMessagesIniciarAccion() throws IOException{
        
        this.numeroMsgs = 0;
        this.totalNumeroMensajes = 0;

        String output =  protocol.getMessages();
        out.println(output);
             
    }
    
    public synchronized void getMessagesFrom(String fromServer) throws IOException{

        String output = protocol.processInput(fromServer);
        this.numeroMsgs = protocol.getNumeroDeMensajes();
        this.totalNumeroMensajes = protocol.getTotalNumeroDeMensajes();
        System.out.println("numero de mgsg " + this.numeroMsgs);
        
        if(totalNumeroMensajes != 0){ 
            
            do{
                output =  protocol.getMessages();
                out.println(output);
                fromServer = in.readLine();
                output = protocol.processInput(fromServer);
                this.numeroMsgs = protocol.getNumeroDeMensajes();
                System.out.println("numero de mgsg " + this.numeroMsgs);
        
            }while(numeroMsgs == 0);

            out.println(output);
            System.out.println("output dentro del if + " + output);

            for(int i = 0; i < this.numeroMsgs; i++){
                 fromServer = in.readLine();
                 System.out.println("FROMSERVER DENTRO WHILE " + fromServer);
                 protocol.leerMsgs(fromServer);
            }
            
            
            String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#ALL_RECEIVED";
            out.println(theOutput);
                    
        }
        
        this.numeroMsgs = 0;
        this.totalNumeroMensajes = 0;
        protocol.restar = 1;
             
    }
}

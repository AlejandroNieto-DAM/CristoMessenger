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

import Classes.User;
import Classes.RefrescarListaAmigos;
import Vista.CristoMessenger;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import java.util.Base64;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

 
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
    
    
    Boolean recibiendoMsg = false;
    int contadorMsgs = 0;
    
    Lock lock;
    Condition usando;
    Boolean procesando = false;
    String condition = "";
    
    RefrescarListaAmigos friendRefresh;
    
    int numeroMsgs = 0;
    int totalNumeroMensajes = 0;
    int contadorIcons = 0;
    
    ArrayList<String> cadenas = new ArrayList();
    ArrayList<String> decodedBytes = new ArrayList();
    Boolean friendPhoto = false;
    
    
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
        lock = new ReentrantLock();
        usando = lock.newCondition();

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
        } catch (InterruptedException ex) {
            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    public void filtrado(String fromServer) throws IOException, InterruptedException{
        
        //System.out.println("FROM SERVER ESTO ES LO QUE RECIBO --> " + fromServer);
        
        if(fromServer.contains("ENDING_MULTIMEDIA_TRANSMISSION")){
           condition = "";
           
        }
        
        if(condition != ""){
            while(!fromServer.contains(condition)){
                usando.await();
            } 
        }
        
        

        if(fromServer.contains("LOGIN_CORRECT")){

            protocol.processInput(fromServer);
            
            

            try {
                this.getPhoto();
                
            } catch (IOException ex) {
                Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            

        }

        if(fromServer.contains("MSGS")){
            if(this.recibiendoMsg){
                this.processMsgs(fromServer);
            } else {
                this.getMessagesFrom(fromServer);
            }
            
        }
        
        if(fromServer.contains("STARTING_MULTIMEDIA_TRANSMISSION_TO")){
            lock.lock();
            condition = "RESPONSE_MULTIMEDIA";  
        }

        if(fromServer.contains("RESPONSE_MULTIMEDIA")){
            System.out.println("Entrando a seguir con la foto");
            String datos[] = fromServer.split("#");
            cadenas.add(new String(datos[7]));   
        }

        if(fromServer.contains("ENDING_MULTIMEDIA_TRANSMISSION")){
            if(friendPhoto){
                this.processFriendPhoto();
                System.out.println("Hemos procesao una foto");
            } else {
                this.processPhoto();
            }

            condition = "";
            usando.signalAll();
            lock.unlock();
        }

        if(fromServer.contains("STATUS")){    
            this.changeFriendsState(fromServer);
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
        out.println(protocol.addNewMsg(fromServer));   
    }
    
    public void getPhoto() throws IOException{
        cadenas.clear();
        this.decodedBytes.clear();
        
        String output = protocol.getPhoto();
        out.println(output);   
    }
    
    public void processPhoto() throws IOException{
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
        
        this.getFriendPhoto();
        
        
        
        
        friendPhoto = true;
        
    }
    
    public void getFriendPhoto() throws IOException{
        cadenas.clear();
        this.decodedBytes.clear();
        
        ArrayList<User> friendList = this.a.getFriends();
        
        String output = protocol.getFriendPhoto(friendList.get(contadorIcons).getLogin());
        out.println(output);
              
    }
    
    public void processFriendPhoto() throws IOException{
        for(String s : cadenas){
            decodedBytes.add(new String(Base64.getDecoder().decode(s.getBytes())));
        }
  
        File file = new File("friendIcons/" + contadorIcons + ".jpg");
        contadorIcons++;
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        for (String s : decodedBytes) {
            for (char c : s.toCharArray()) {
                fos.write(c);
            }
        }
        fos.flush();
        fos.close();
        
        
        if(contadorIcons < 2){
            
            cadenas.clear();
            this.decodedBytes.clear();
        
            ArrayList<User> friendList = this.a.getFriends();
            String output = protocol.getFriendPhoto(friendList.get(contadorIcons).getLogin());
            out.println(output); 
        
        } else {
            this.a.loadPhoto();
            this.a.setActualUser(login);
            this.loginFrame.setVisible(false);   
            this.a.setVisible(true); 
            this.friendRefresh.start(); 
        }
        
        //this.a.loadFriendPhoto();
        procesando = false;
    }
     
    public void processMsg(String theInput) throws IOException{
        protocol.processInput(theInput);  
    }
    
    public void changeFriendsState(String fromServer){
        ArrayList<User> friendList = this.a.getFriends();
        
        String[] datos = fromServer.split("#");

        for(int i = 0; i < friendList.size(); i++){
            String estado = this.protocol.friendStatus(fromServer);
            if(datos[4].equals(friendList.get(i).getLogin())){
                if(estado.equals("CONNECTED")){
                    friendList.get(i).setEstadoUsuario(1);
                } else {
                    friendList.get(i).setEstadoUsuario(0);
                }
            }
        }
        
        this.a.setFriendsOf(friendList);
    }
    
    public void refreshFriends() throws IOException{

        ArrayList<User> friendList = this.a.getFriends();
        
        for(int i = 0; i < friendList.size(); i++){
            String cadena = protocol.getFriendStatus() + friendList.get(i).getLogin();
            out.println(cadena);
        }  
    }
    
    public void sendMessage(String text) throws IOException{
        String output = protocol.sendMessage(text);
        out.println(output);
    }
      
    public void getFriendStatus() throws IOException{
        String output = protocol.getFriendStatus();
        out.println(output);  
    }
    
    public void getFriendData() throws IOException{
        String output = protocol.getFriendData();
        out.println(output);
    }
    
    public void getMessagesIniciarAccion() throws IOException{        
        this.numeroMsgs = 0;
        this.totalNumeroMensajes = 0;
        this.contadorMsgs = 0;
        protocol.restar = 1;
        this.recibiendoMsg = false;
        
        String output =  protocol.getMessages();
        out.println(output);
             
    }
    
    public void processMsgs(String fromServer) throws IOException{
        System.out.println("Mira el numero de mensajes --> " + this.numeroMsgs);
       
        if(this.contadorMsgs < this.numeroMsgs){
            protocol.leerMsgs(fromServer);
            contadorMsgs++;
            System.out.println("Mira el contador de mensajes --> " + this.contadorMsgs);

            if(this.contadorMsgs == this.numeroMsgs){
                String theOutput = protocol.msgAllReceived();
                out.println(theOutput);
                System.out.println("Output de que ya los he recibio tos --> " + theOutput);
                in.readLine();
                condition = "";
                usando.signalAll();
                lock.unlock();
            }
        }
            
            
        
        
    }
    
    public void getMessagesFrom(String fromServer) throws IOException{
                
        String output = protocol.processInput(fromServer);
        this.numeroMsgs = protocol.getNumeroDeMensajes();
        this.totalNumeroMensajes = protocol.getTotalNumeroDeMensajes();
        
        if(totalNumeroMensajes != 0){ 
            if(numeroMsgs == 0){
                output =  protocol.getMessages();
                out.println(output);
            } else {
                out.println(output);
                this.recibiendoMsg = true;
                lock.lock();
                condition = "MSGS";
            }
        }               
    }
}

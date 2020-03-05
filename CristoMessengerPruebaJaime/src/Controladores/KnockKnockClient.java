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

import Classes.BarraNotificaciones;
import Classes.User;
import Classes.RefrescarListaAmigos;
import Vista.CristoMessenger;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    CristoMessenger myCristoMessengerScreen;
    PrintWriter out;
    Socket kkSocket;
    BufferedReader in;
    
    
    Boolean recibiendoMsg = false;
    int contadorMsgs = 0;
    int diasParaAtras = 0;
    
    Lock lock;
    Condition usando;
    Boolean procesando = false;
    String condition = "";
    String extension = "";
             
    RefrescarListaAmigos friendRefresh;
    
    int numeroMsgs = 0;
    int totalNumeroMensajes = 0;
    int contadorIcons = 0;
    
    ArrayList<String> cadenas = new ArrayList();
    ArrayList<String> decodedBytes = new ArrayList();
    Boolean friendPhoto = false;
    
    
    ArrayList<String> notificaciones = new ArrayList();
    BarraNotificaciones notis;
    
    int contadorFiles = 0;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    
    public KnockKnockClient(int port, String host, String login, String pass, JFrame frame) throws IOException{
        this.portNumber = port;
        this.hostName = host;
        this.login = login;
        this.pass = pass;
        this.loginFrame = frame;
        myCristoMessengerScreen = new CristoMessenger(this);
        protocol = new ClientProtocol(login, pass, myCristoMessengerScreen);
        kkSocket = new Socket(hostName, portNumber);
        out = new PrintWriter(kkSocket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        
        friendRefresh = new RefrescarListaAmigos(this);
        lock = new ReentrantLock();
        usando = lock.newCondition();
        
        notis = new BarraNotificaciones(this);

    }
    
    
    @Override
    public void run(){
 
        String fromServer = "";
        String fromUser = "";

        fromUser = protocol.processInput(null);
        out.println(fromUser);
        
        try {
            try{
                while((fromServer = in.readLine()) != null){   
                    if(fromServer.startsWith("PROTOCOLCRISTOMESSENGER1.0")){
                        this.filtrado(fromServer);
                    }
                }
            } catch (SocketException ex){
                Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                kkSocket.close();
            } 
        } catch (IOException ex) {
            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void filtrado(String fromServer) throws IOException, InterruptedException{
        
        //System.out.println("FROM SERVER ESTO ES LO QUE RECIBO --> " + fromServer);
        
        if(fromServer.contains("ENDING_MULTIMEDIA_TRANSMISSION") || fromServer.contains("ENDING_MULTIMEDIA_CHAT")){
           condition = "";   
        }
        
        if(condition != ""){
            while(!fromServer.contains(condition)){
                usando.await();
            } 
        }

        if(fromServer.contains("LOGIN_CORRECT")){
            protocol.processInput(fromServer);
            
            out.println(this.protocol.getUserData());
            
            try {
                this.getPhoto();
                
            } catch (IOException ex) {
                Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
            }  
            
        } else if(fromServer.contains("MSGS")){
            if(this.recibiendoMsg){
                this.processMsgs(fromServer);
            } else {
                this.getMessagesFrom(fromServer);
            }
            
        } else if(fromServer.contains("STARTING_MULTIMEDIA_CHAT")){
            System.out.println("Starting");
            lock.lock();
            condition = "MULTIMEDIA_CHAT";
            cadenas.clear();
            String datos[] = fromServer.split("#");
            extension = datos[6];
        
        } else if(fromServer.contains("STARTING_MULTIMEDIA_TRANSMISSION_TO")){
            
            lock.lock();
            condition = "RESPONSE_MULTIMEDIA"; 
            
        } else if(fromServer.contains("ENDING_MULTIMEDIA_CHAT")){
            
            this.processMultimediaReceived();
            
            condition = "";
            usando.signalAll();
            lock.unlock();
        
        } else if(fromServer.contains("MULTIMEDIA_CHAT_TRANSMISION")){
            String datos[] = fromServer.split("#");
            System.out.println("CADENA RECEIVIDA --> " + fromServer);
            cadenas.add(new String(datos[7])); 
        
        } else if(fromServer.contains("RESPONSE_MULTIMEDIA")){
            String datos[] = fromServer.split("#");
            cadenas.add(new String(datos[7]));   
        
        } else if(fromServer.contains("ENDING_MULTIMEDIA_TRANSMISSION")){
            if(friendPhoto){
                this.processFriendPhoto();
            } else {
                String output = this.processPhoto();
                out.println(output);
            }

            condition = "";
            usando.signalAll();
            lock.unlock();
        
        } else if(fromServer.contains("STATUS")){    
            this.changeFriendsState(fromServer);
        
        } else if(fromServer.contains("ALLDATA_USER")){
            protocol.processInput(fromServer);
        
        } else if(fromServer.contains("CHAT")){
            if(!fromServer.contains("MESSAGE_SUCCESFULLY_PROCESSED")){
                String[] datos = fromServer.split("#");
                
                if(this.myCristoMessengerScreen.getFocusFriend().equals(datos[4])){
                    this.addNewMsg(fromServer);
                } else {
                    String cadena = "Tienes un nuevo mensaje de " + datos[4];
                    notificaciones.add(cadena);
                }
                
                
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
    
    public String processPhoto() throws IOException{
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
  
        String cadena = this.protocol.photoReceived(this.login);
        
        friendPhoto = true;
        
        return cadena;
        
    }
    
    public void getFriendPhoto() throws IOException{
        cadenas.clear();
        this.decodedBytes.clear();
        
        ArrayList<User> friendList = this.myCristoMessengerScreen.getFriends();
        
        String output = protocol.getFriendPhoto(friendList.get(contadorIcons).getLogin());
        out.println(output);
              
    }
    
    public void processFriendPhoto() throws IOException{
        for(String s : cadenas){
            decodedBytes.add(new String(Base64.getDecoder().decode(s.getBytes())));
        }
  
        File file = new File("friendIcons/" + contadorIcons + ".jpg");
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        for (String s : decodedBytes) {
            for (char c : s.toCharArray()) {
                fos.write(c);
            }
        }
        fos.flush();
        fos.close();
        
        ArrayList<User> friendList = this.myCristoMessengerScreen.getFriends();
 
        out.println(this.protocol.photoReceived(protocol.getFriendPhoto(friendList.get(contadorIcons).getLogin())));
        
        contadorIcons++;
        
        int numeroAmigos = friendList.size();
        if(contadorIcons < numeroAmigos){
            
            cadenas.clear();
            this.decodedBytes.clear();

            String output = protocol.getFriendPhoto(friendList.get(contadorIcons).getLogin());
            out.println(output); 
        
        } else {
            this.myCristoMessengerScreen.loadPhoto();
            this.myCristoMessengerScreen.setActualUser(login);
            this.loginFrame.setVisible(false);   
            this.myCristoMessengerScreen.setVisible(true); 
            this.friendRefresh.start(); 
            this.notis.start();
        }
        
        procesando = false;
    }
    
    public void changeFriendsState(String fromServer){
        ArrayList<User> friendList = this.myCristoMessengerScreen.getFriends();
        
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
        
        this.myCristoMessengerScreen.setFriendsOf(friendList);
    }
    
    public void sendNotificationChar(String c){
        this.myCristoMessengerScreen.addNotificationChar(c);
    }
    
    public ArrayList getNotificaciones(){
        return this.notificaciones;
    }
    
    public void refreshFriends() throws IOException{

        ArrayList<User> friendList = this.myCristoMessengerScreen.getFriends();
        
        for(int i = 0; i < friendList.size(); i++){
            String cadena = protocol.getFriendStatus() + friendList.get(i).getLogin();
            out.println(cadena);
        }  
    }
    
    public void sendMessage(String text) throws IOException{
        String output = protocol.sendMessage(text);
        System.out.println("Mira el mensaje que mando --> " + output);
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
    
    public void getMessagesIniciarAccion(int restar) throws IOException{        
        this.numeroMsgs = 0;
        this.totalNumeroMensajes = 0;
        this.contadorMsgs = 0;
        
        if(restar == 0){
            protocol.restar = 1;
        } else {
            protocol.restar = restar;
        }
        
        this.recibiendoMsg = false;
        
        String output =  protocol.getMessages();
        out.println(output);
         
    }
    
    public void processMsgs(String fromServer) throws IOException{
       
        if(this.contadorMsgs < this.numeroMsgs){
            protocol.leerMsgs(fromServer);
            contadorMsgs++;

            if(this.contadorMsgs == this.numeroMsgs){
                String theOutput = protocol.msgAllReceived();
                out.println(theOutput);
                this.diasParaAtras = protocol.restar;
                this.myCristoMessengerScreen.canWheeled = true;
                
                condition = "";
                usando.signalAll();
                lock.unlock();
            }
        }    
    }
    
    
    public void deleteNotification(String id_user_notification){
        for(int i = 0; i < this.notificaciones.size(); i++){
            if(notificaciones.get(i).contains(id_user_notification)){
                notificaciones.remove(i);
            }
        }
    }
    
    public int getDiasParaAtras(){
        return this.diasParaAtras;
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
    
    
    public void actualizarNotificaciones(){
        for(String s  : notificaciones){
            if(s.contains(this.myCristoMessengerScreen.getFocusFriend())){
                notificaciones.remove(s);
            }
        }
    }
    
    
    public void sendMultimedia(String ruta, String extension) throws IOException{
        
        
        out.println("PROTOCOLCRISTOMESSENGER1.0#HORA/FECHA#CLIENT#STARTING_MULTIMEDIA_CHAT#" + this.login + "#" + this.myCristoMessengerScreen.getFocusFriend() + "#" + extension);
                
        this.protocol.loadFile(ruta);
        
        while(protocol.getSeparador() > 0 ){
            String cadena = protocol.sendPhoto();
            out.println(cadena);
        }

        out.println("PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#ENDING_MULTIMEDIA_CHAT#" + this.login + "#" + this.myCristoMessengerScreen.getFocusFriend());
        
    }
    
    public void processMultimediaReceived(){
        
        ArrayList<String> decodeLines = new ArrayList<String>();

        for (String s : this.cadenas) {
            decodeLines.add(new String(Base64.getDecoder().decode(s)));
        }
        
        try{
            File file = new File("multimedias/CristoMessenger" + sdf.format(timestamp) + "." + this.extension);
            file.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (String s : decodeLines) {
                    for (char c : s.toCharArray()) {
                        fos.write(c);
                    }
                }
                fos.flush();
                fos.close();
            }
        } catch (IOException ex) {
            
        }
        
        contadorFiles++;
        
    }
}

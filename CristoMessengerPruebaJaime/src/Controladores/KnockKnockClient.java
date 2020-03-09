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
import java.security.Key;
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
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

 
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
    
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
    
    
    boolean encrypt;
    
    
    public KnockKnockClient(int port, String host, String login, String pass, JFrame frame, boolean encrypt) throws IOException{
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

        this.encrypt = encrypt;
    }
    
    
    @Override
    public void run(){
 
        String fromServer = "";
        String fromUser = "";

        fromUser = protocol.processInput(null);
        this.salida(fromUser);
        
        try {
            try{
                while((fromServer = in.readLine()) != null){   
                    
                    String cadenaADecodear = fromServer;
                    
                    if(encrypt){ 
                        
                        try {
                            cadenaADecodear = KnockKnockClient.decrypt(fromServer);
                        } catch (Exception ex) {
                            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    
                    CristoMessenger.returnException("ENTRADA --> " + fromServer);
                    System.out.println("ENTRADA --> " + fromServer);
                    
                    if(cadenaADecodear.startsWith("PROTOCOLCRISTOMESSENGER1.0")){
                        this.filtrado(cadenaADecodear);
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
            
            this.salida(this.protocol.getUserData());
   
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
            cadenas.add(new String(datos[7])); 
        
        } else if(fromServer.contains("RESPONSE_MULTIMEDIA")){
            String datos[] = fromServer.split("#");
            cadenas.add(new String(datos[7]));   
        
        } else if(fromServer.contains("ENDING_MULTIMEDIA_TRANSMISSION")){
            if(friendPhoto){
                this.processFriendPhoto();
            } else {
                String output = this.processPhoto();
                this.salida(output);
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
        this.salida(protocol.addNewMsg(fromServer));
    }
    
    public void getPhoto() throws IOException{
        cadenas.clear();
        this.decodedBytes.clear();
        
        String output = protocol.getPhoto();
        this.salida(output);   
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

        this.salida(output);
              
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
 
        this.salida(this.protocol.photoReceived(friendList.get(contadorIcons).getLogin()));
        //System.out.println("RECIBIDA " + friendList.get(contadorIcons).getLogin());
        
        contadorIcons++;
        
        int numeroAmigos = friendList.size();
        if(contadorIcons < numeroAmigos){
            
            cadenas.clear();
            this.decodedBytes.clear();

            String output = protocol.getFriendPhoto(friendList.get(contadorIcons).getLogin());
            //System.out.println("Mira esto --> " + friendList.get(contadorIcons).getLogin());
            this.salida(output); 
        
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
            this.salida(cadena);
        }  
    }
    
    public void sendMessage(String text) throws IOException{
        String output = protocol.sendMessage(text);
        this.salida(output);
    }
      
    public void getFriendStatus() throws IOException{
        String output = protocol.getFriendStatus();
        this.salida(output);
    }
    
    public void getFriendData() throws IOException{
        String output = protocol.getFriendData();
        this.salida(output);
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
        
        
        String output =  protocol.getMessages();
        this.salida(output);
         
    }
    
    public void processMsgs(String fromServer) throws IOException{
       
        if(this.contadorMsgs < this.numeroMsgs){
            protocol.leerMsgs(fromServer);
            contadorMsgs++;

            if(this.contadorMsgs == this.numeroMsgs){
                String theOutput = protocol.msgAllReceived();
                this.salida(theOutput);
                this.diasParaAtras = protocol.restar;
                this.myCristoMessengerScreen.canWheeled = true;
                
                condition = "";
                usando.signalAll();
                lock.unlock();
                this.recibiendoMsg = false;

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
                this.salida(output);
            } else {
                this.salida(output);
                this.recibiendoMsg = true;
                lock.lock();
                System.out.println("Mira el recibiendo msgs --> " + recibiendoMsg);
                condition = "MSGS";
            }
        }               
    }
    
    
    public void actualizarNotificaciones(){

        for(int i = 0; i < this.notificaciones.size(); i++){
            if(this.notificaciones.get(i).contains(this.myCristoMessengerScreen.getFocusFriend())){
                notificaciones.remove(i);
            }
        }
        
    }
    
    
    public void sendMultimedia(String ruta, String extension) throws IOException{
        
        
        String startingMC = this.protocol.startingMultimediaChat(extension);
        this.salida(startingMC);
        
        this.protocol.loadFile(ruta);
        
        while(protocol.getSeparador() > 0 ){
            String cadena = protocol.sendMultimediaFriend();
            this.salida(cadena);
        }

        String endMC = this.protocol.endingMultimediaChat();
        this.salida(endMC);
        
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
    
    
    public synchronized void salida(String salida){
        
        System.out.println("SALIDA --> " + salida);
        CristoMessenger.returnException("SALIDA --> " + salida);
        
        int contadorEspacios = 76;
        
        if(encrypt){
            
            String cadena = "";
            try {
                cadena = KnockKnockClient.encrypt(salida);
            } catch (Exception ex) {
                Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String send = "";
        
            for(int i = 0; i < cadena.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   send += cadena.charAt(i);
                }

            }
            
            
            out.println(send);
            
        } else {
            out.println(salida);
        }
    }
    
    
    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return new BASE64Encoder().encode(encVal);
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }

    /**
     * Generate a new encryption key.
     */
    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }
}

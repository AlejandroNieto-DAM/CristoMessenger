/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import cristoserver.KKServer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


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
            
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};       
    
    
    boolean encrypt = false;

     
    public KKMultiServerThread(Socket socket, KKServer myKKS, boolean leidos, boolean encrypt) {
        super("KKMultiServerThread");
        this.socket = socket;
        
        this.kkp = new KnockKnockProtocol(leidos);
        
        this.myKKS = myKKS;
        
        this.encrypt = encrypt;
    }
    
    public void run() {
        
        try {
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                     new InputStreamReader(
                             socket.getInputStream()));
            
            try{
                
                while ((inputLine = in.readLine()) != null) {
                    
                    System.out.println("ENTRADA --> " + inputLine);
                    
                    String cadenaAFiltrar = "";
                    if(encrypt){
                        cadenaAFiltrar = KKMultiServerThread.decrypt(inputLine);
                        
                    } else {
                        cadenaAFiltrar = inputLine;
                    }

                    this.filtrado(cadenaAFiltrar);

                    if (outputLine.contains("BAD_LOGIN"))
                        break;
                }
                
            } catch(SocketException e){
                                  
            } catch (FileNotFoundException | InterruptedException ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                kkp.setDisconnected();
                socket.close();
            }
            
            
            
    
        } catch (IOException e) {
        } catch (SQLException ex) {
             Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void filtrado(String inputLine) throws FileNotFoundException, IOException, SQLException, InterruptedException{
        
        if(inputLine.contains("OK_SEND!") && kkp.contadorPaquetes(inputLine) == 5){

            for(int i = 0; i < kkp.contadorMsg; i++){
                outputLine = kkp.sendMsg(i);
               
                this.salida(outputLine);
                
                
            }

        }else if (inputLine.contains("ALL_RECEIVED")){
           
            //out.println("");
            
        } else if (inputLine.contains("PHOTO_RECEIVED")){
           
            //out.println("");
            
        } else if(inputLine.contains("STARTING_MULTIMEDIA_CHAT")){
            
            this.startingMultimediaChat(inputLine);
        
        } else if(inputLine.contains("ENDING_MULTIMEDIA_CHAT")){ 
            
            this.endingMultimediaChat(inputLine);
            
        }else if(inputLine.contains("MULTIMEDIA_CHAT_TRANSMISION")){
            
            this.multimediaChatMsg(inputLine);
        
        }else if(inputLine.contains("CHAT")){
            
            if(inputLine.contains("RECEIVED_MESSAGE")){
                this.sendReceivedMessage(inputLine); 
            } else {
                try {
                    this.sendMessage(inputLine);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }     

        } else if(inputLine.contains("GET_PHOTO")){

           
            this.salida(kkp.startingMultimedia());
            
            kkp.loadFile(inputLine);

            while(kkp.getSeparador() > 0 ){
                
                this.salida(kkp.getPhotoUser());
                
            }
            
            this.salida(kkp.endingMultimedia());

        } else {
     
            outputLine = kkp.processInput(inputLine);
            
            this.salida(outputLine);
            

        }  
    }
    
    public void startingMultimediaChat(String theInput){
        String[] datos = theInput.split("#");
        PrintWriter outU = getOutputUser(datos[5]);
        
        String outputLine = "";
        
        int contadorEspacios = 76;

        if(encrypt){
            try {  
                outputLine = KKMultiServerThread.encrypt(theInput);
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String salida = "";
        
            for(int i = 0; i < outputLine.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   salida += outputLine.charAt(i);
                }

            }
            
            outputLine = salida;
        } else {
            outputLine = theInput;
        }
        outU.println(outputLine);
    }
    
    public void multimediaChatMsg(String theInput){
        String[] datos = theInput.split("#");
        PrintWriter outU = getOutputUser(datos[5]);
        
        String outputLine = "";
        
        int contadorEspacios = 76;
        
        if(encrypt){
            try {  
                outputLine = KKMultiServerThread.encrypt(theInput);
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String salida = "";
        
            for(int i = 0; i < outputLine.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   salida += outputLine.charAt(i);
                }

            }
            
            outputLine = salida;
        } else {
            outputLine = theInput;
        }
        outU.println(outputLine);
    }
    
    public void endingMultimediaChat(String theInput){
        String[] datos = theInput.split("#");
        PrintWriter outU = getOutputUser(datos[5]);
        String outputLine = "";
        int contadorEspacios = 76;

        if(encrypt){
            try {  
                outputLine = KKMultiServerThread.encrypt(theInput);
                
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String salida = "";
        
            for(int i = 0; i < outputLine.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   salida += outputLine.charAt(i);
                }

            }
            
            outputLine = salida;
        } else {
            outputLine = theInput;
        }
        outU.println(outputLine);
    }
    
    public PrintWriter getOutputUser(String user){
        Boolean encontrado = false;
        PrintWriter outB = null;
        for(int i = 0; i < myKKS.getHebrasSize() && !encontrado; i++){ 
            if(this.myKKS.getConexionAt(i).getLogin().equals(user)){
                outB = this.myKKS.getConexionAt(i).getOutputStream();
                encontrado = true; 
            }
        }
        
        return outB;
        
    }
    
    public String getLogin(){
        return this.kkp.getLogin();
    }
    
    public void sendReceivedMessage(String inputLine){
        
        String[] datos = inputLine.split("#");
        PrintWriter outU = getOutputUser(datos[5]);
        String outputLine = "";
        
        outputLine = kkp.sendReceivedMessage(inputLine);
        
        int contadorEspacios = 76;

        if(encrypt){
            try {  
                outputLine = KKMultiServerThread.encrypt(outputLine);
                
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String salida = "";
        
            for(int i = 0; i < outputLine.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   salida += outputLine.charAt(i);
                }

            }
            
            outputLine = salida;
        }
        
        outU.println(outputLine);
         
        
    }
    
    public String sendMsjEncrypt(String cadena){
        int contadorEspacios = 76;
        String outputLine = "";
        
        if(encrypt){
            try {  
                outputLine = KKMultiServerThread.encrypt(cadena);
                
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String salida = "";
        
            for(int i = 0; i < outputLine.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   salida += outputLine.charAt(i);
                }

            }
            
            outputLine = salida;
 
        }
        
        return outputLine;
    }
    
    public void sendMessage(String inputLine) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
        
        Boolean encontrado = false;
        String loginFriend = this.kkp.getFriend(inputLine);
        
        String cadena = kkp.sendMessage(inputLine);
        
        for(int i = 0; i < myKKS.getHebrasSize() && !encontrado; i++){ 
            if(this.myKKS.getConexionAt(i).getLogin().equals(loginFriend)){
                PrintWriter outB = this.myKKS.getConexionAt(i).getOutputStream();
                outB.println(this.sendMsjEncrypt(cadena + sdf.format(timestamp)));
                encontrado = true; 
            }
        }
        
        String tryInsert = "";
        tryInsert = kkp.receiveMessage(inputLine);
        
        if(!tryInsert.contains("Bien")){
            
            
            this.salida(tryInsert);
            
            
        }
  
    }
    
    public KnockKnockProtocol getProtocol(){
        return kkp;
    }
    
    public PrintWriter getOutputStream(){
        return this.out;
    }
    
    public BufferedReader getInputStream(){
        return this.in;
    }
    
    
    public synchronized void salida(String send){
        
        int contadorEspacios = 76;
        
        System.out.println("SALIDA --> " + send);

        String encrypted = "";
        if(encrypt){
            
            try {  
                encrypted = KKMultiServerThread.encrypt(send);
            } catch (Exception ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String salida = "";
        
            for(int i = 0; i < encrypted.length(); i++){
                if(i == contadorEspacios){
                    contadorEspacios += 77;
                } else {
                   salida += encrypted.charAt(i);
                }
                                

            }
            
            out.println(salida);
                        
        } else {
            out.println(send);
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
    public static String decrypt(String encryptedData) throws IOException, InvalidKeyException, NoSuchAlgorithmException, Exception {

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

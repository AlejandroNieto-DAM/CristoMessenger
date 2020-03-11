package Controladores;

import Classes.User;
import Classes.Message;
import Vista.CristoMessenger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;

public class ClientProtocol {
    
    private final String cadenaPrincipal;
    private final int LOGGING = 0;
    private final int LOGGED = 1;
    
    public int state = LOGGING;
    
    private String login;
    private String passwd;
        
    CristoMessenger myCristoMessenger;
    ArrayList<Message> msjs = new ArrayList();
    ArrayList<User> friendList = new ArrayList();
    
    int separador = 0;

        
    int numeroMensajes;
    int totalNumeroMensajes;
    
    public int restar = 1;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    
    File file;
    FileInputStream fin = null;

     
    ClientProtocol(String login, String pass, CristoMessenger a){
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
        this.login = login;
        this.passwd = pass;
        myCristoMessenger = a;
        msjs = new ArrayList();
        numeroMensajes = 0;
        totalNumeroMensajes = 0;
    }
    
    public String processInput(String theInput){
        String theOutput = null;
            
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            
            if(theInput.startsWith(cadenaPrincipal)){
                if(theInput.contains("LOGIN_CORRECT")){
                    leerAmigos(theInput);                    
                }
                
                if(theInput.contains("BAD_LOGIN")){
                    theOutput = null;
                }
                
                if(theInput.contains("#MSGS#")){
                    this.msjs.clear();
                    this.leerNumeroMensajes(theInput);
                    theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#MSGS#OK_SEND!";
                }

                if(theInput.contains("STATUS")){
                    String status = this.friendStatus(theInput);
                    this.myCristoMessenger.setFriendStatus(status);
                    
                }
                
                if(theInput.contains("ALLDATA_USER")){
                    String[] datos = theInput.split("#");
                    
                    if(datos[4].equals(this.login)){
                        this.processUserData(theInput);
                    } else {
                        String nombre = this.userData(theInput);
                        this.myCristoMessenger.setFriendData(nombre);
                    }
                }
            }
        }
        
        return theOutput;
    }
    
    public String msgAllReceived(){
        return cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#MSGS#ALL_RECEIVED";
    }
    
    public String getPhoto(){
        String cadena = "";
        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#GET_PHOTO#" + this.login;
        return cadena;
    }
    
    public String getFriendPhoto(String friend){
        String cadena = "";
        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#GET_PHOTO#" + friend;
        return cadena;
    }
     
    public String userData(String theInput){
        String cadena = ""; 
        String[] datos = theInput.split("#");
        cadena = datos[5] + " " + datos[6] + " " + datos[7];
        
        ArrayList<User> friends = this.myCristoMessenger.getFriends();
        for(int i = 0; i < friends.size(); i++){
            if(datos[4].equals(friends.get(i).getLogin())){
                if(friends.get(i).getEstadoUsuario() == false){
                    cadena += " " + "NOT_CONNECTED";
                } else {
                    cadena += " " + "CONNECTED";
                }
            }
        }
        
        return cadena;
    }
      
    public void processUserData(String theInput){
        String cadena = ""; 
        String[] datos = theInput.split("#");
        cadena = datos[5] + " " + datos[6] + " " + datos[7];
        this.myCristoMessenger.setActualUserInfo(cadena);
    }
     
    public String getUserData(){
        String cadena = "";
        cadena += cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#ALLDATA_USER#" + this.login;
        return cadena;
    } 
    
    public String getFriendData(){
        String cadena = "";
        cadena += cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#ALLDATA_USER#" + this.myCristoMessenger.getFocusFriend();
        return cadena;
    } 
    
    public String sendMessage(String text){
        String cadena = "";
        cadena += cadenaPrincipal + "#" + this.sdf.format(timestamp) + "#" + "CLIENT#CHAT#" + login + "#" + this.myCristoMessenger.getFocusFriend() + "#" + text;
        return cadena;
    }
    
    public String friendStatus(String theInput){
        String cadena = "";
        
        String[] cadenas = theInput.split("#");
        int contadorAtt = 0;
        
        for(String att : cadenas){
            if(contadorAtt == 5){
                cadena = att;
            }
            
            contadorAtt++;
        }
        
        return cadena;
    }
    
    public String getFriendStatus(){
        String cadena  = ""; 
        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#STATUS#" + login + "#";
        return cadena;
    }
    
    public void leerNumeroMensajes(String fromServer){         
        String[] msgs = fromServer.split("#");
        int contadorStt = 0;
        for(String stt : msgs){
            
            if(contadorStt == 6){
                this.totalNumeroMensajes = Integer.parseInt(stt);
            }
            
            if(contadorStt == 7){
                this.numeroMensajes = Integer.parseInt(stt);
            }
            contadorStt++;
        }  
    }
    
    public int getNumeroDeMensajes(){  
        return this.numeroMensajes;
    }
    
    public int getTotalNumeroDeMensajes(){  
        return this.totalNumeroMensajes;
    }
    
    public String getMessages(){
        this.msjs.clear();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 24 * restar * 60 * 60 * 1000L);
        String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT" + "#MSGS#" + login + "#" + myCristoMessenger.getFocusFriend() + "#" + sdf.format(timestamp);
        restar++;
        return theOutput;
    }
    
    public void leerAmigos(String fromServer){       
        String[] datos = fromServer.split("#");
        
        int contador = 0;
        boolean nombre = true;
        
        String loginF = "";
        
        for(String s : datos){
            if(contador > 6){
                
                if(nombre){
                    
                    loginF = s;
                    nombre = false;
                    
                } else {
                    
                    User aux = new User();
                    aux.setLogin(loginF);
                    if(s.equals("CONNECTED")){
                       aux.setEstadoUsuario(1);
                    } else {
                       aux.setEstadoUsuario(0);
                    }
                    
                    friendList.add(aux);
                    
                    nombre = true;
                }
                
            }
            contador++;
        }
            
        myCristoMessenger.setFriendsOf(friendList);
            
    }
    
    public void leerMsgs(String fromServer) throws IOException{
        
        if(fromServer.startsWith(cadenaPrincipal)){
            
            int contadorStt = 1;
            String logOr = "";
            String logDest = "";
            String dateHour = "";
            String text = "";
            String read = "";
            String msgsFiltrado = "";
            String[] msgs = null;

            msgsFiltrado = fromServer.substring(fromServer.indexOf("MSGS") + 5, fromServer.length());
            msgs = msgsFiltrado.split("#");
            
            int contadorFiltro = 0;
            for(String s : msgs){
                contadorFiltro++;
            }
            
            if(contadorFiltro == 4){
                
                for(String att : msgs){
                    if(contadorStt == 1){
                        logOr = att;
                    }

                    if(contadorStt == 2){
                        logDest = att;
                    }

                    if(contadorStt == 3){
                        dateHour = att;
                    }

                    if(contadorStt == 4){
                        text = att;
                    }

                    contadorStt++;
                }

                Message m = new Message();
                m.setId_user_orig(logOr);
                m.setId_user_dest(logDest);
                m.setDate(dateHour);
                m.setText(text);
                m.setRead(1);  
                m.setSent(1);

                this.msjs.add(m);
                this.myCristoMessenger.setMessages(msjs);
                
            } else {
                
               for(String att : msgs){
                    if(contadorStt == 1){
                        logOr = att;
                    }

                    if(contadorStt == 2){
                        logDest = att;
                    }

                    if(contadorStt == 3){
                        dateHour = att;
                    }

                    if(contadorStt == 4){
                        text = att;
                    }

                    if(contadorStt == 5){
                        read = att;
                    }


                    contadorStt++;
                }

                Message m = new Message();
                m.setId_user_orig(logOr);
                m.setId_user_dest(logDest);
                m.setDate(dateHour);
                m.setText(text);

                if(read.equals("LEIDO")){
                   m.setRead(1);  
                } else {
                   m.setRead(0);   
                }

                m.setSent(1);

                this.msjs.add(m);
                this.myCristoMessenger.setMessages(msjs); 
            }
            
        }
    }
    
    public String photoReceived(String login){
        String cadena = "";
        cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#PHOTO_RECEIVED#" + login;
        return cadena;
    }
    
    public String addNewMsg(String fromServer) throws IOException{
        String cadena = "";
        String[] datos = fromServer.split("#");
        
        if(this.myCristoMessenger.getFocusFriend().equals(datos[4])){
           Message m = new Message();
        
            m.setId_user_orig(datos[4]);
            m.setId_user_dest(datos[5]);
            m.setDate(datos[7]);
            m.setText(datos[6]);
            m.setRead(1);
            m.setSent(1);

            this.msjs.add(m);
            this.myCristoMessenger.setMessages(msjs); 
        }
        
        cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#CHAT#RECEIVED_MESSAGE#" + datos[4] + "#" + sdf.format(timestamp);
        
        return cadena;
        
    }  
    
    
    public int getSeparador(){
        return separador;
    }
    
    public void loadFile(String ruta) throws FileNotFoundException{
 
        try{
            file = new File(ruta);
            fin = new FileInputStream(file);
        } catch(FileNotFoundException e){
            
        }
        
        separador = (int)file.length();
    }
    
    public String sendMultimediaFriend() throws FileNotFoundException, IOException{
        String cadena = "";

        int  i = 0;
        int contador = 0;
        int[] fileContent;
        int bytesPorLeer = 511;
        String toEncode = "";
        
        cadena = "PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#MULTIMEDIA_CHAT_TRANSMISION#" + this.login + "#" + this.myCristoMessenger.getFocusFriend() + "#";

        if(separador > bytesPorLeer){
            fileContent = new int[bytesPorLeer];
            cadena += bytesPorLeer + "#";
        } else {
            fileContent = new int[separador];
            bytesPorLeer = separador;
            cadena += separador + "#";
        }

        while(contador < bytesPorLeer){
            i = fin.read();
            fileContent[contador] = i;
            toEncode += (char)i;
            
            contador++;
        }

        String encodedString = Base64.getEncoder().encodeToString(toEncode.getBytes());
        
        cadena += encodedString;
        
        //System.out.println("Pero mira el encode --> " + encodedString);

        contador = 0;

        separador -= 512; 
        
        if(separador < 0){
            separador = 0;
            fin.close();
        }
  
        return cadena;
    }
    
    
    public String startingMultimediaChat(String extension){
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#STARTING_MULTIMEDIA_CHAT#" + this.login + "#" + this.myCristoMessenger.getFocusFriend() + "#" + extension;
        return cadena;
    }
    
    public String endingMultimediaChat(){
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#ENDING_MULTIMEDIA_CHAT#" + this.login + "#" + this.myCristoMessenger.getFocusFriend();
        return cadena;
    }
    
}

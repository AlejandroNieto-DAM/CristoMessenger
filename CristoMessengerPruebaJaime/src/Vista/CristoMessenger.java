package Vista;


import Classes.CellRenderer;
import Classes.Message;
import Controladores.KnockKnockClient;
import Classes.User;
import java.awt.Image;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.util.*;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class CristoMessenger extends javax.swing.JFrame{

    String actualUser;
    ImageIcon imageIcon;
    ImageIcon imageIconUser;
    String valor;
    ArrayList<Message> mensjs;
    
    String focusFriend;

    KnockKnockClient myKK;
    
    String[] friends;
    ArrayList<User> friendList;
    
    int numeroAmigos = 0;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
    /**
     * Creates new form CristoMessenger
     * @param myKKClient
     */
    public CristoMessenger(KnockKnockClient myKKClient) {
        
        initComponents();
        
        actualUser = "";
        jListFriends.setModel(new DefaultListModel());
        imageIcon = new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(jLabelIconAboveSearch.getWidth(), jLabelIconAboveSearch.getHeight(), Image.SCALE_DEFAULT));
        jLabelIconAboveSearch.setIcon(imageIcon); 
        

        imageIcon = new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(jLabelIconRegisterWindow.getWidth(), jLabelIconRegisterWindow.getHeight(), Image.SCALE_DEFAULT));
        jLabelIconRegisterWindow.setIcon(imageIcon);
        
        jLabelMessageOfExistingUserRegisterWindow.setText("");
        jLabelErrorPasswordIncorrect.setText("");
        
        this.myKK = myKKClient;
             
    }
    
    public void loadPhoto(){
       imageIconUser = new ImageIcon(new ImageIcon("userPhoto.jpg").getImage().getScaledInstance(jLabelIconUserConnected.getWidth(), jLabelIconUserConnected.getHeight(), Image.SCALE_DEFAULT));
       jLabelIconUserConnected.setIcon(imageIconUser); 
    }
    
    public void loadFriendPhoto(){
        int position = 0;
        for(int i = 0; i < this.friendList.size(); i++){
            if(friendList.get(i).getLogin().equals(this.focusFriend)){
                position = i;
            }
        }
       imageIconUser = new ImageIcon(new ImageIcon("friendIcons/" + position + ".jpg" ).getImage().getScaledInstance(jLabelIconUserConnected.getWidth(), jLabelIconUserConnected.getHeight(), Image.SCALE_DEFAULT));
       this.jLabelIconFriend.setIcon(imageIconUser); 
    }

    private CristoMessenger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setMessages(ArrayList msjs) throws IOException{
        
        this.mensjs = msjs;
        this.jTextArea1.setText("");
        
        Collections.sort(mensjs, Message.StuNameComparator);
        String check = "";
        
        //System.out.println("mensjss size en la vista locooo --< " + mensjs.size());
        for(int i = 0; i < mensjs.size(); i++){
            if(mensjs.get(i).getRead() == true){
                check = " " + "✓✓";
            } else {
                check = " " + "✓";
            }
            if (mensjs.get(i).getId_user_orig().equals(actualUser)){    
                this.jTextArea1.setText(this.jTextArea1.getText() + "\t\t\t" + mensjs.get(i).getText() + check + "\n");
            } else {
                this.jTextArea1.setText(this.jTextArea1.getText() + mensjs.get(i).getText() + check + "\n");
            }
        }
    }
    
    public void setActualUser(String login){
        actualUser = login;
        jLabelUserConnected.setText(login); 
    }
    
    public String getActualUser(){
        return actualUser;
    }
    
    
    public ArrayList getFriends(){
        return this.friendList;
    }
    
    public static void returnException(String exception){
        CristoMessenger.jTextAreaDebugWindow.setText(CristoMessenger.jTextAreaDebugWindow.getText() + "\n" + exception);
    }
    
    public void setFriendsOf(ArrayList<User> friendList){
        
        this.friendList = friendList;
        
        String[] names = new String[friendList.size()];
        
        for(int i = 0; i < friendList.size(); i++){
             
            if(friendList.get(i).getEstadoUsuario() == true){
                names[i] = friendList.get(i).getLogin() + " " + "CONNECTED";
            } else {
                names[i] = friendList.get(i).getLogin() + " " + "NOT_CONNECTED";
            }
           
        }
        
        this.friends = names;
        
        for(String a : names){
            this.numeroAmigos++;
        }
        
        this.jListFriends.setModel(new javax.swing.AbstractListModel(){
            String[] vect = names;
            
            @Override
            public int getSize() {
                return vect.length;
            }

            @Override
            public Object getElementAt(int i) {
                return vect[i];
            }
        
        });
        
        jListFriends.setCellRenderer(new CellRenderer());

        
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelChatWindow = new javax.swing.JPanel();
        jTextFieldMessageFromUser = new javax.swing.JTextField();
        jButtonSendMessage = new javax.swing.JButton();
        jTextFieldUserSelectedInListName = new javax.swing.JTextField();
        jLabelIconAboveSearch = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelIconUserConnected = new javax.swing.JLabel();
        jScrollPaneJListChat = new javax.swing.JScrollPane();
        jListFriends = new javax.swing.JList<>();
        jLabelUserConnected = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabelIconFriend = new javax.swing.JLabel();
        jPanelRegisterWindow = new javax.swing.JPanel();
        jTextFieldInsertLoginRegister = new javax.swing.JTextField();
        jTextFieldUserPasswordRegister = new javax.swing.JTextField();
        jTextFieldUserNameRegister = new javax.swing.JTextField();
        jTextFieldUserSurname1 = new javax.swing.JTextField();
        jTextFieldUserSurname2 = new javax.swing.JTextField();
        jTextFieldUserRepeatPasswordRegister = new javax.swing.JTextField();
        jButtonRegister = new javax.swing.JButton();
        jLabelIconRegisterWindow = new javax.swing.JLabel();
        jLabelMessageOfExistingUserRegisterWindow = new javax.swing.JLabel();
        jLabelErrorPasswordIncorrect = new javax.swing.JLabel();
        jPanelDebugWindow = new javax.swing.JPanel();
        jScrollPaneDebugWindow = new javax.swing.JScrollPane();
        jTextAreaDebugWindow = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setToolTipText("");

        jButtonSendMessage.setText(">>");
        jButtonSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageActionPerformed(evt);
            }
        });

        jTextFieldUserSelectedInListName.setEditable(false);

        jTextFieldSearch.setText("Search");
        jTextFieldSearch.setToolTipText("");
        jTextFieldSearch.setActionCommand("<Not Set>");

        jListFriends.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListFriendsMouseClicked(evt);
            }
        });
        jListFriends.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFriendsValueChanged(evt);
            }
        });
        jScrollPaneJListChat.setViewportView(jListFriends);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jTextArea1MouseWheelMoved(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanelChatWindowLayout = new javax.swing.GroupLayout(jPanelChatWindow);
        jPanelChatWindow.setLayout(jPanelChatWindowLayout);
        jPanelChatWindowLayout.setHorizontalGroup(
            jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPaneJListChat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelChatWindowLayout.createSequentialGroup()
                                .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(101, 101, 101))))
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addGap(148, 148, 148)
                        .addComponent(jLabelIconAboveSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabelUserConnected, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelIconUserConnected, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                                .addComponent(jTextFieldMessageFromUser, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelChatWindowLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldUserSelectedInListName, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jLabelIconFriend, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanelChatWindowLayout.setVerticalGroup(
            jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelChatWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelIconUserConnected, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUserConnected, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldUserSelectedInListName, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelIconFriend, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addComponent(jLabelIconAboveSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelChatWindowLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelChatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldMessageFromUser, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPaneJListChat))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Chat", jPanelChatWindow);

        jTextFieldInsertLoginRegister.setText("Login");

        jTextFieldUserPasswordRegister.setText("Password");

        jTextFieldUserNameRegister.setText("Name");

        jTextFieldUserSurname1.setText("Surname1");

        jTextFieldUserSurname2.setText("Surname2");

        jTextFieldUserRepeatPasswordRegister.setText("Repeat Password");

        jButtonRegister.setText("Register");
        jButtonRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegisterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRegisterWindowLayout = new javax.swing.GroupLayout(jPanelRegisterWindow);
        jPanelRegisterWindow.setLayout(jPanelRegisterWindowLayout);
        jPanelRegisterWindowLayout.setHorizontalGroup(
            jPanelRegisterWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRegisterWindowLayout.createSequentialGroup()
                .addGroup(jPanelRegisterWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRegisterWindowLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jButtonRegister))
                    .addGroup(jPanelRegisterWindowLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(jPanelRegisterWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldUserSurname2)
                            .addComponent(jTextFieldUserSurname1)
                            .addComponent(jTextFieldUserNameRegister)
                            .addComponent(jTextFieldInsertLoginRegister)
                            .addComponent(jTextFieldUserPasswordRegister)
                            .addComponent(jTextFieldUserRepeatPasswordRegister, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                            .addComponent(jLabelMessageOfExistingUserRegisterWindow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelErrorPasswordIncorrect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(138, 138, 138)
                        .addComponent(jLabelIconRegisterWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(109, Short.MAX_VALUE))
        );
        jPanelRegisterWindowLayout.setVerticalGroup(
            jPanelRegisterWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRegisterWindowLayout.createSequentialGroup()
                .addGroup(jPanelRegisterWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRegisterWindowLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldInsertLoginRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabelMessageOfExistingUserRegisterWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldUserNameRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldUserSurname1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldUserSurname2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jTextFieldUserPasswordRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(jTextFieldUserRepeatPasswordRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelErrorPasswordIncorrect, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))
                    .addGroup(jPanelRegisterWindowLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabelIconRegisterWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jButtonRegister)
                .addGap(70, 70, 70))
        );

        jTabbedPane1.addTab("Register", jPanelRegisterWindow);

        jTextAreaDebugWindow.setEditable(false);
        jTextAreaDebugWindow.setColumns(20);
        jTextAreaDebugWindow.setRows(5);
        jScrollPaneDebugWindow.setViewportView(jTextAreaDebugWindow);

        javax.swing.GroupLayout jPanelDebugWindowLayout = new javax.swing.GroupLayout(jPanelDebugWindow);
        jPanelDebugWindow.setLayout(jPanelDebugWindowLayout);
        jPanelDebugWindowLayout.setHorizontalGroup(
            jPanelDebugWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneDebugWindow, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE)
        );
        jPanelDebugWindowLayout.setVerticalGroup(
            jPanelDebugWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDebugWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneDebugWindow, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Debug", jPanelDebugWindow);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegisterActionPerformed
        
        Boolean existingUser = false;
        Boolean twoPasswdWll = false;
        
        jLabelMessageOfExistingUserRegisterWindow.setText("");
        jLabelErrorPasswordIncorrect.setText("");
        
        

        if(jTextFieldUserPasswordRegister.getText().equals(jTextFieldUserRepeatPasswordRegister.getText())){
            twoPasswdWll = true;
        } else {
            jLabelErrorPasswordIncorrect.setText("Las contraseñas no coinciden.");
        }
 
        jTextFieldInsertLoginRegister.setText("Login");
        jTextFieldUserNameRegister.setText("Name");
        jTextFieldUserSurname1.setText("Surname1");
        jTextFieldUserSurname2.setText("Surname2");
        jTextFieldUserPasswordRegister.setText("Password");
        jTextFieldUserRepeatPasswordRegister.setText("Repeat Password");
    }//GEN-LAST:event_jButtonRegisterActionPerformed

    private void jListFriendsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFriendsValueChanged
        
    }//GEN-LAST:event_jListFriendsValueChanged

    private void jButtonSendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageActionPerformed
        // TODO add your handling code here:
        String text = this.jTextFieldMessageFromUser.getText();
        if(!text.equals("") && text.length() < 1000){
            try {
                myKK.sendMessage(text);
            } catch (IOException ex) {
                Logger.getLogger(CristoMessenger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Message m = new Message();
        m.setId_user_orig(this.actualUser);
        m.setId_user_dest(this.getFocusFriend());
        m.setDate(sdf.format(timestamp));
        m.setText(text);
        m.setRead(0);
        m.setSent(1);
        this.mensjs.add(m);
        try {
            this.setMessages(mensjs);
        } catch (IOException ex) {
            Logger.getLogger(CristoMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.jTextFieldMessageFromUser.setText("");
    }//GEN-LAST:event_jButtonSendMessageActionPerformed
  
    public String getFocusFriend(){
        return this.focusFriend;
    }
    
    public void setFriendStatus(String status){
       this.jTextFieldUserSelectedInListName.setText(this.jTextFieldUserSelectedInListName.getText() + " " + status);
       
    }
    
    public void setFriendData(String data){
        this.jTextFieldUserSelectedInListName.setText("");
        this.jTextFieldUserSelectedInListName.setText(data);
    }
     
    private void jListFriendsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFriendsMouseClicked
        
        this.jTextArea1.setText("");
        String des = this.jListFriends.getSelectedValue();
        String dest = "";
        Boolean parar = false;
        for(int i = 0; i < des.length() && parar == false; i++){
            if(des.charAt(i) != ' '){
                dest += des.charAt(i);
            } else {
                parar = true;
            }
        }
        
        this.focusFriend = dest;
        
        
        
        try {
            
            if(this.mensjs != null){
                this.mensjs.clear();
            }
            
            this.myKK.getFriendData();
            this.myKK.getMessagesIniciarAccion();
            this.loadFriendPhoto();
            
            
        } catch (IOException ex) {
            Logger.getLogger(CristoMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        

        
    }//GEN-LAST:event_jListFriendsMouseClicked

    private void jTextArea1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jTextArea1MouseWheelMoved
        System.out.println("Que pasa demonio");
    }//GEN-LAST:event_jTextArea1MouseWheelMoved

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CristoMessenger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CristoMessenger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CristoMessenger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CristoMessenger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CristoMessenger().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRegister;
    private javax.swing.JButton jButtonSendMessage;
    private javax.swing.JLabel jLabelErrorPasswordIncorrect;
    private javax.swing.JLabel jLabelIconAboveSearch;
    private javax.swing.JLabel jLabelIconFriend;
    private javax.swing.JLabel jLabelIconRegisterWindow;
    private javax.swing.JLabel jLabelIconUserConnected;
    private javax.swing.JLabel jLabelMessageOfExistingUserRegisterWindow;
    private javax.swing.JLabel jLabelUserConnected;
    private javax.swing.JList<String> jListFriends;
    private javax.swing.JPanel jPanelChatWindow;
    private javax.swing.JPanel jPanelDebugWindow;
    private javax.swing.JPanel jPanelRegisterWindow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneDebugWindow;
    private javax.swing.JScrollPane jScrollPaneJListChat;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    public static javax.swing.JTextArea jTextAreaDebugWindow;
    private javax.swing.JTextField jTextFieldInsertLoginRegister;
    private javax.swing.JTextField jTextFieldMessageFromUser;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JTextField jTextFieldUserNameRegister;
    private javax.swing.JTextField jTextFieldUserPasswordRegister;
    private javax.swing.JTextField jTextFieldUserRepeatPasswordRegister;
    private javax.swing.JTextField jTextFieldUserSelectedInListName;
    private javax.swing.JTextField jTextFieldUserSurname1;
    private javax.swing.JTextField jTextFieldUserSurname2;
    // End of variables declaration//GEN-END:variables
}

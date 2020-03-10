package Controllers;


import Classes.Friend;
import Classes.User;
import Models.Friend_Model;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class Friend_Controller {
    
     Friend_Model a;
     
    public Friend_Controller(){
        a = new Friend_Model();
    }
     
    public void getFriendsOf(ArrayList<User> amigos, String id_user) throws SQLException{
        a.getFriendsOf(amigos, id_user);
    }
    
    public Boolean getRelation(String friend1, String friend2) throws SQLException{
        return a.getRelation(friend1, friend2);
    }
}

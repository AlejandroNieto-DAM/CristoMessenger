package Classes;


import java.awt.Component;
import java.awt.Image;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class CellRenderer extends DefaultListCellRenderer {
    
        
    
        //ImageIcon imageIcon = new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT));

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            ImageIcon imageIcon = new ImageIcon(new ImageIcon("friendIcons/" + index + ".jpg" ).getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT));

            
           // System.out.println("En el list cell renderer el index vale esto --> " + index);
             label.setIcon(imageIcon);
            
            //label.setHorizontalTextPosition(JLabel.RIGHT);            
            
            return label;
        }
    
}

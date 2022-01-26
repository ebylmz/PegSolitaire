import javax.swing.*;
import java.awt.*;

public class exp {
    public static void main (String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(600, 700);
    
    
        JButton btn = new JButton();
        btn.setBackground(ColorScheme.BLACK.getColor());
        btn.setForeground(ColorScheme.RED.getColor());
        btn.setActionCommand("P");
        btn.setIcon(new ImageIcon("../img/peg1.png"));
        btn.setSize(150, 150);
        frame.add(btn);
        
        frame.setVisible(true);
    }
}

import java.awt.GridLayout;
// import java.awt.BorderLayout;
import java.awt.Color;
// import java.awt.Dimension;
import java.awt.event.ActionListener;
// import java.security.InvalidParameterException;
// import java.util.Random;
import java.awt.event.ActionEvent;
// import javax.swing.JLabel;
// import javax.swing.ImageIcon;
import javax.swing.JButton;
// import javax.swing.JOptionPane;
import javax.swing.JPanel;
// import javax.swing.JButton;
// import javax.swing.JPanel;
// import javax.swing.SwingUtilities;

public class MenuPanel extends JPanel implements ActionListener{
    public static enum Option {NEW_GAME, CONTINUE_GAME, EXIT_GAME};

    private JButton[] __btn;
    private Option __selected; //!!

    public MenuPanel () {
        setBackground(Color.BLACK);
        setLayout(new GridLayout(3, 1, 10, 10));
        
        __btn = new JButton[3];
        __btn[0] = new JButton("New Game");
        __btn[1] = new JButton("Continue Game");
        __btn[2] = new JButton("Exit");
        
        for (var btn : __btn)
            btn.addActionListener(this);
        
        add(__btn[0]);
        add(__btn[1]);
        add(__btn[2]);
    }

    public Option getSelection () {
        return __selected;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == __btn[0]) {
            __selected = Option.NEW_GAME;
            System.out.println("New game button selected");
        }
        else if (e.getSource() == __btn[1]) {
            __selected = Option.CONTINUE_GAME;
            System.out.println("Coninute game button selected");
        }
        else if (e.getSource() == __btn[2]) {
            __selected = Option.EXIT_GAME;
            System.out.println("EXit button selected");
        }
    }
}

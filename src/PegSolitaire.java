// import java.awt.GridLayout;
import java.awt.BorderLayout;
// import java.awt.Color;
// import java.awt.Dimension;
// import java.awt.event.ActionListener;
// import java.security.InvalidParameterException;
// import java.util.Random;
// import java.util.concurrent.TimeUnit;
// import java.awt.event.ActionEvent;
import javax.swing.JFrame;
// import javax.swing.JLabel;
import javax.swing.ImageIcon;
// import javax.swing.JButton;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PegSolitaire extends JFrame {
    private GamePanel __gamePanel;
    private MenuPanel __menuPanel;

    public PegSolitaire () {
        super("PegSolitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());
        setVisible(true);    
        
        setIconImage(new ImageIcon("../img/peg.png").getImage());

        // getContentPane().setBackground(new Color(28, 34, 38)); //! NOT NEEDED I THINK
        

        // display menu panel
        /*
        __gamePanel = null;
        __menuPanel = new MenuPanel();
        add(__menuPanel);
        SwingUtilities.updateComponentTreeUI(this); 

         */

         //!!!!!!11 tmp
        __gamePanel = new GamePanel();
        add(__gamePanel);
         //!!!!!!11 tmp

        // update the game frame
        SwingUtilities.updateComponentTreeUI(this); 
    }

    public void start () {
        boolean isSelected = false;
        wait(3000); //!!!!!!!!!!!1111
        do {
            if (__menuPanel.getSelection() == MenuPanel.Option.NEW_GAME) {
                __gamePanel = new GamePanel();
                remove(__menuPanel);
                add(__gamePanel);
                SwingUtilities.updateComponentTreeUI(this); 
                isSelected = true;
                System.out.println("NEW GAME created");
            }
            else if (__menuPanel.getSelection() == MenuPanel.Option.CONTINUE_GAME) {
                remove(__menuPanel);
                add(__gamePanel);
                SwingUtilities.updateComponentTreeUI(this); 
                isSelected = true;
                System.out.printf("CONTINUE game\n");
            }
            else if (__menuPanel.getSelection() == MenuPanel.Option.EXIT_GAME)
                System.exit(1);
        } while (!isSelected);
    }

    private void wait (int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

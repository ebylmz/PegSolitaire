import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class PegSolitaire extends JFrame implements ActionListener {
    private GamePanel __gamePanel;
    private JButton __homeBtn;

    private MenuPanel __menuPanel;

    public PegSolitaire () {
        super("PegSolitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());
        
        setIconImage(new ImageIcon("../img/peg.png").getImage());

        // getContentPane().setBackground(new Color(28, 34, 38)); //! NOT NEEDED I THINK
        displayMenuPanel();
        // displayGamePanel();  //! this will be handled in actionPerformed
        
        setVisible(true);    
    }

    public void displayGamePanel () {
        __homeBtn = new JButton();
        __homeBtn.addActionListener(this);

        if (__gamePanel != null)
            remove(__gamePanel); 
        __gamePanel = new GamePanel(__homeBtn);
        add(__gamePanel);

        // update the game frame
        SwingUtilities.updateComponentTreeUI(this); 
    }

    public void displayMenuPanel () {
        if (__gamePanel != null)
            remove(__gamePanel);
        if (__menuPanel == null)
            __menuPanel = new MenuPanel();
        add(__menuPanel);
        // update the game frame
        SwingUtilities.updateComponentTreeUI(this); 
    }

	@Override
	public void actionPerformed (ActionEvent e) {
        if (e.getSource() == __homeBtn) {
            displayMenuPanel();
            // JOptionPane.showMessageDialog(this, "New Game", "new game", JOptionPane.OK_CANCEL_OPTION);
            // displayGamePanel();
        }
	}
}

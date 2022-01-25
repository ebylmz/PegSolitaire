import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

// import java.awt.GridLayout;
// import java.awt.Color;
// import javax.swing.JPanel;


public class PegSolitaire extends JFrame implements ActionListener {
    private GamePanel __gamePanel;
    private JButton __homeBtn;

    private JPanel __menuPanel;
    private JButton[] __menuBtn;
    private JLabel __welcomeLabel;

    public PegSolitaire () {
        super("PegSolitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("../img/logo.png").getImage());
        setLayout(new BorderLayout());
        setSize(600, 650);
        
        // set game and menu panels
        setMenuPanel();
        setGamePanel();

        // getContentPane().setBackground(new Color(28, 34, 38)); //! NOT NEEDED I THINK
        displayMenuPanel();
        // displayGamePanel();  //! this will be handled in actionPerformed
        
        setVisible(true);    
    }

    public void setMenuPanel () {
        // set null layout for menu panel
        __menuPanel = new JPanel(null); 
        __menuPanel.setBackground(new Color(28, 34, 38));
        
        // set common button properties
        __menuBtn = new JButton[3];
        
        int x = 70;   // x position of button
        int y = 100;   // y position of button 
        int verticalDistance = 100;

        for (int i = 0; i < __menuBtn.length; ++i, y += verticalDistance) {
            __menuBtn[i] = new JButton();
            __menuBtn[i].setSize(150, 50);
            __menuBtn[i].setLocation(x, y);
            __menuBtn[i].setBackground(new Color(143, 155, 166));
            __menuBtn[i].setForeground(new Color(0xE10550));            
            __menuBtn[i].addActionListener(this);
            __menuPanel.add(__menuBtn[i]);
        }

        // set specific button properties
        __menuBtn[0].setText("New Game");
        __menuBtn[1].setText("Continue");
        __menuBtn[2].setText("Exit");


        // set a welcome message
        __welcomeLabel = new JLabel();
        __welcomeLabel.setForeground(new Color(0xE10550));
        
        __welcomeLabel.setText(
            "<html><p>" +
                "<pre>      Welcome Warrior</pre>" +
                "<pre>           to the </pre>" +
                "<pre>   PegSolitaire Universe</pre>" +
            "</p></html>");
        
        __welcomeLabel.setIcon(new ImageIcon("../img/warrior.png"));
        __welcomeLabel.setLocation(300, 0);
        __welcomeLabel.setSize(300, 500);
        __welcomeLabel.setIconTextGap(20);
        
        __welcomeLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        __welcomeLabel.setHorizontalTextPosition(JLabel.CENTER);
        __welcomeLabel.setVerticalTextPosition(JLabel.BOTTOM);
        __menuPanel.add(__welcomeLabel);
    } 

    public void setGamePanel () {
        if (__gamePanel != null)
            remove(__gamePanel);

        __homeBtn = new JButton();
        __homeBtn.addActionListener(this);
        __gamePanel = new GamePanel(__homeBtn);
    }

    public void displayMenuPanel () {
        //! card layout !!
        // if currently game panel is displaying, remove it from frame
        if (__gamePanel != null)
            remove(__gamePanel);
        if (__menuPanel == null)
            setMenuPanel();

        // add menu panel and update the game frame
        add(__menuPanel);
        SwingUtilities.updateComponentTreeUI(this); 
    }

    public void displayGamePanel () {
        // if currently menu panel is displaying, remove it from frame
        if (__menuPanel != null)
            remove(__menuPanel);
        if (__gamePanel == null)
            setGamePanel();
            
        // add menu panel and update the game frame
        add(__gamePanel);
        SwingUtilities.updateComponentTreeUI(this); 
    }

	@Override
	public void actionPerformed (ActionEvent e) {
        if (e.getSource() == __homeBtn) {
            displayMenuPanel();
            // JOptionPane.showMessageDialog(this, "New Game", "new game", JOptionPane.OK_CANCEL_OPTION);
            // displayGamePanel();
        }
        // new game button
        else if (e.getSource() == __menuBtn[0]) {
            if (__gamePanel != null) {
                remove(__gamePanel); 
                __gamePanel = null;
            }
            displayGamePanel();
        }
        // continue button
        else if (e.getSource() == __menuBtn[1]) {
            displayGamePanel();
        }
        // exit button
        else if (e.getSource() == __menuBtn[2]) {
            System.exit(1);
        }
	}
}

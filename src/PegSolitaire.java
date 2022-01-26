import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

// import java.awt.GridLayout;
// import javax.swing.JPanel;

public class PegSolitaire extends JFrame implements ActionListener {
    private GamePanel __gamePanel;
    private JButton __homeBtn;
    
    private JPanel __mainMenuPanel;
    private JButton[] __menuBtn;
    private JLabel __welcomeLabel;

    private JPanel __gameSettingsPanel;
    private JRadioButton[] __gameTypeBtn;   // computer or user
    private JRadioButton[] __boardTypeBtn;  // six different board
    private JButton __backToMainMenuBtn;
    private JButton __createGameBtn;
    private GamePanel.BoardType __boardType;
    private GamePanel.GameMode __gameType;

    private JPanel __displayPanel; // the dislaying panel on top of JFrame

    public PegSolitaire () {
        super("PegSolitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("../img/logo.png").getImage());
        setLayout(new BorderLayout());
        setSize(600, 700);
        
        // set game and menu panels
        setMainMenuPanel();
        setGameSettingsPanel();
        // setGamePanel();

        // display main menu panel as start of the interface
        setDisplayPanel(__mainMenuPanel);
        // getContentPane().setBackground(ColorScheme.BLACK.getColor()); //! NOT NEEDED I THINK
        
        setVisible(true);    
    }

    public void setMainMenuPanel () {
        // set null layout for menu panel
        __mainMenuPanel = new JPanel(null); 
        __mainMenuPanel.setBackground(ColorScheme.BLACK.getColor());
        
        // set common button properties
        __menuBtn = new JButton[3];
        
        int x = 70;   // x position of button
        int y = 100;   // y position of button 
        int verticalDistance = 100;

        for (int i = 0; i < __menuBtn.length; ++i, y += verticalDistance) {
            __menuBtn[i] = menuBtn("", this, ColorScheme.GRAY, ColorScheme.RED, true);
            __menuBtn[i].setSize(150, 50);
            __menuBtn[i].setLocation(x, y);
            __mainMenuPanel.add(__menuBtn[i]);
        }

        // set specific button properties
        __menuBtn[0].setText("New Game");
        __menuBtn[1].setText("Continue");
        __menuBtn[2].setText("Exit");


        // set a welcome message
        __welcomeLabel = new JLabel();
        __welcomeLabel.setForeground(ColorScheme.RED.getColor());
        
        __welcomeLabel.setText(
            "<html><p>" +
                "<pre>  Warriors</pre>" +
                "<pre>     of</pre>" +
                "<pre>PegSolitaire</pre>" +
            "</p></html>");
        
        __welcomeLabel.setIcon(new ImageIcon("../img/warrior.png"));
        __welcomeLabel.setLocation(300, 0);
        __welcomeLabel.setSize(300, 500);
        __welcomeLabel.setIconTextGap(20);
        
        __welcomeLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        __welcomeLabel.setHorizontalTextPosition(JLabel.CENTER);
        __welcomeLabel.setVerticalTextPosition(JLabel.BOTTOM);
        __mainMenuPanel.add(__welcomeLabel);
    } 

    public void setGameSettingsPanel () {
        JPanel gameTypePanel = setGameTypePanel();
        JPanel boardTypePanel = setBoardTypePanel();
        JPanel commandPanel = setSettingsCommandPanel();
        
        // add two panel to the top Game Settings panel
        __gameSettingsPanel = new JPanel(new BorderLayout());
        ColorScheme.setColor(__gameSettingsPanel, ColorScheme.GRAY);
        __gameSettingsPanel.add(gameTypePanel, BorderLayout.NORTH);
        __gameSettingsPanel.add(boardTypePanel, BorderLayout.CENTER);
        __gameSettingsPanel.add(commandPanel, BorderLayout.SOUTH);
    }

    public JPanel setGameTypePanel () {
        JPanel gameSetPanel = new JPanel(new GridLayout(6, 2));
        ColorScheme.setColor(gameSetPanel, ColorScheme.GRAY);

        __gameTypeBtn = new JRadioButton[2];   
        ButtonGroup playGroup = new ButtonGroup();
        for (int i = 0; i < __gameTypeBtn.length; ++i) {
            __gameTypeBtn[i] = new JRadioButton(); 
            __gameTypeBtn[i].addActionListener(this);
            ColorScheme.setColor(__gameTypeBtn[i], ColorScheme.GRAY, ColorScheme.RED);
            // add related buttons to the same group
            playGroup.add(__gameTypeBtn[i]);
            gameSetPanel.add(__gameTypeBtn[i]);
        }        

        __gameTypeBtn[0].setText("Computer");
        __gameTypeBtn[1].setText("User");    
        return gameSetPanel;
    } 

    public JPanel setBoardTypePanel () {
        // set the board type buttons (six different board)
        JPanel boardTypePanel = new JPanel(new GridLayout(2, 3));
        boardTypePanel.setBackground(ColorScheme.GRAY.getColor());
        __boardTypeBtn = new JRadioButton[6];  // 
        ButtonGroup boardGroup = new ButtonGroup();
        for (int i = 0; i < __boardTypeBtn.length; ++i) {
            __boardTypeBtn[i] = new JRadioButton();
            // set the text at the bottom center of the button
            __boardTypeBtn[i].setHorizontalTextPosition(JButton.CENTER);
            __boardTypeBtn[i].setVerticalTextPosition(JButton.BOTTOM);
            ColorScheme.setColor(__boardTypeBtn[i], ColorScheme.GRAY, ColorScheme.BLACK);
            __boardTypeBtn[i].addActionListener(this);

            // add buttons to the same group to permit only one selection
            boardGroup.add(__boardTypeBtn[i]);
            boardTypePanel.add(__boardTypeBtn[i]);
        }        

        __boardTypeBtn[0].setText("French");
        __boardTypeBtn[0].setIcon(new ImageIcon("../img/b1.png"));
        __boardTypeBtn[1].setText("German");
        __boardTypeBtn[1].setIcon(new ImageIcon("../img/b1.png"));
        __boardTypeBtn[2].setText("Asymetrical");
        __boardTypeBtn[2].setIcon(new ImageIcon("../img/b1.png"));
        __boardTypeBtn[3].setText("English");
        __boardTypeBtn[3].setIcon(new ImageIcon("../img/b1.png"));
        __boardTypeBtn[4].setText("Diamond");
        __boardTypeBtn[4].setIcon(new ImageIcon("../img/b1.png"));
        __boardTypeBtn[5].setText("Triangular");
        __boardTypeBtn[5].setIcon(new ImageIcon("../img/b1.png"));
        return boardTypePanel;
    }

    public JPanel setSettingsCommandPanel () {
        // set start and back button
        JPanel commandPanel = new JPanel();
        ColorScheme.setColor(commandPanel, ColorScheme.GRAY);

        // create button not enable till board and game type selected 
        __createGameBtn = menuBtn("Create", this, ColorScheme.BLACK, ColorScheme.RED, false);

        __backToMainMenuBtn = menuBtn("Back", this, ColorScheme.BLACK, ColorScheme.RED, true);

        commandPanel.add(__createGameBtn);
        commandPanel.add(__backToMainMenuBtn);    

        return commandPanel;
    }

    public void setGamePanel () {
        if (__gamePanel != null)
            remove(__gamePanel);

        __homeBtn = new JButton();
        __homeBtn.addActionListener(this);
        __gamePanel = new GamePanel(__homeBtn, __gameType, __boardType);
    }

    public void setDisplayPanel (JPanel panel) {
        //! card layout !!
        if (__displayPanel != null)
            remove(__displayPanel);
        __displayPanel = panel;
        
        // add menu panel and update the game frame
        add(__displayPanel);
        SwingUtilities.updateComponentTreeUI(this); 
    }

	@Override
	public void actionPerformed (ActionEvent e) {
        // MAIN MENU EVENTS
        if (__displayPanel == __mainMenuPanel) {
            // new game button
            if (e.getSource() == __menuBtn[0]) {
                if (__gamePanel != null) {
                    // ask if user wants to save the game
                    //! NOT IMPLEMENTED YET
                    remove(__gamePanel); 
                    setGamePanel(); // set new Game Panel
                }
                // display game settings menu for settings of new game 
                setDisplayPanel(__gameSettingsPanel);
            }
            // continue button
            else if (e.getSource() == __menuBtn[1])
                setDisplayPanel(__gamePanel);
            // exit button
            else if (e.getSource() == __menuBtn[2]) 
                System.exit(1);
        }
        // GAME SETTINGS MENU EVENTS
        else if (__displayPanel == __gameSettingsPanel) {
            // COMMAND BUTTONS (CREATE & BACK)
            if (e.getSource() instanceof JButton) {
                if (e.getSource() == __backToMainMenuBtn) {
                    setGameSettingsPanel(); // clear settings panel
                    setDisplayPanel(__mainMenuPanel);
                }
                else if (e.getSource() == __createGameBtn) {
                    // create the game which properties specified
                    setGamePanel();
                    setDisplayPanel(__gamePanel);
                }
            }
            // GAME SETTING BUTTONS (CREATE & BACK)
            else if (e.getSource() instanceof JRadioButton) {
                boolean selected = false;

                // GAME TYPE BUTTONS
                for (int i = 0; i < __gameTypeBtn.length && !selected; ++i)
                    if (e.getSource() == __gameTypeBtn[i]) {
                        __gameType = __gameTypeBtn[i].getText().equals("User") ? 
                            GamePanel.GameMode.USER : GamePanel.GameMode.COMPUTER;
                        System.out.printf("selected mode: %s\n", __gameTypeBtn[i].getText());
                    }
                
                // BOARD TYPE BUTTONS
                for (int i = 0; i < __boardTypeBtn.length && !selected; ++i)
                    if (e.getSource() == __boardTypeBtn[i]) {
                        switch (__boardTypeBtn[i].getText()) {
                            case "French":
                                __boardType = GamePanel.BoardType.FRENCH; break;
                            case "German":
                                __boardType = GamePanel.BoardType.GERMAN; break;
                            case "Asymetrical":
                                __boardType = GamePanel.BoardType.ASYMETRICAL; break;
                            case "English":
                                __boardType = GamePanel.BoardType.ENGLISH; break;
                            case "Diamond":
                                __boardType = GamePanel.BoardType.DIAMOND; break;
                            case "Triangular":
                                __boardType = GamePanel.BoardType.TRIANGULAR; break;
                        }
                        selected = true;
                        System.out.printf("selected mode: %s\n", __boardTypeBtn[i].getText());
                    }
                    
                // if two selection made, enable the button which creates game  
                if (__boardType != null && __gameType != null)
                __createGameBtn.setEnabled(true);
            }
        }
        // GAME EVENTS
        else if (__displayPanel == __gamePanel) {
            if (e.getSource() == __homeBtn)
                setDisplayPanel(__mainMenuPanel);
        }
	}

    private JButton menuBtn (String text, ActionListener listener, ColorScheme bg, ColorScheme fg, boolean isEnable) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        ColorScheme.setColor(btn, bg, fg);
        btn.setEnabled(isEnable);
        return btn;
    }

    private JButton menuBtn (ImageIcon img, ActionListener listener, ColorScheme bg, ColorScheme fg, boolean isEnable) {
        JButton btn = new JButton(img);
        btn.addActionListener(listener);
        ColorScheme.setColor(btn, bg, fg);
        btn.setEnabled(isEnable);
        return btn;
    }
}

/**
 * @file    PegSolitaire.java
 * @author  Emirkan Burak Yılmaz 
 * @brief   Peg Solitaire Game Implementation
 * @version 0.1
 * @date    2022-01-28
 * 
 * @copyright Copyright (c) 2021
 */

package pegsolitaire;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import pegsolitaire.GamePanel.GameMode;

public class PegSolitaire extends JFrame {
    private final HomeButtonEventHandler HOME_BUTTON_EVENT_HANDLER = new HomeButtonEventHandler();
    private GamePanel __gamePanel;
    private JButton __homeBtn;  // turn backs to the main menu from game panel
    
    private JPanel __mainMenuPanel;
    private JButton[] __mainMenuBtn;    // keeps all the buttons at the main menu
    private JLabel __introLabel;        // intro for the main menu panel (contains text and image)

    private final GameSettingsEventHandler GAME_SETTINGS_EVENT_HANDLER = new GameSettingsEventHandler();
    private JPanel __gameSettingsPanel; 
    private JRadioButton[] __gameTypeBtn;   // computer or user
    private JRadioButton[] __boardTypeBtn;  // six different board
    private JButton __backToMainMenuBtn;    // turn backs to the main menu from settings panel
    private JButton __createGameBtn;        // creates new game
    private GamePanel.BoardType __boardType;
    private GamePanel.GameMode __gameType;

    private final LoadMenuEventHandler LOAD_MENU_EVENT_HANDLER = new LoadMenuEventHandler();
    private JPanel __loadMenuPanel;
    private ArrayList<String[]> __userList;
    private JRadioButton __userListBtn[];
    private int __selectedUserIndex;    // keeps the user index in __userListBtn array

    private JPanel __curDisplayPanel;   // the dislaying panel on top of JFrame

    /**
     * Initializes the game Peg Solitaire
     */
    public PegSolitaire () {
        super("PegSolitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("system/img/logo.png").getImage());
        setLayout(new BorderLayout());
        setSize(650, 750);
        
        // set game and menu panels
        setMainMenuPanel();
        setGameSettingsPanel();

        // display main menu panel as start of the interface
        displayPanel(__mainMenuPanel);
        
        setVisible(true);    
    }

    /**
     * Sets the main menu which contains new game, continue and exit options 
     */
    public void setMainMenuPanel () {
        // set null layout for menu panel
        __mainMenuPanel = new JPanel(null); 
        __mainMenuPanel.setBackground(ColorScheme.BLACK.getColor());
        
        // set common button properties
        __mainMenuBtn = new JButton[3];
        
        int x = 70;   // x position of button
        int y = 100;   // y position of button 
        int verticalDistance = 100;

        for (int i = 0; i < __mainMenuBtn.length; ++i, y += verticalDistance) {
            __mainMenuBtn[i] = menuBtn("", ColorScheme.BLUE, ColorScheme.RED, true);
            __mainMenuBtn[i].setSize(150, 50);
            __mainMenuBtn[i].setLocation(x, y);
            __mainMenuPanel.add(__mainMenuBtn[i]);
        }

        // set specific button properties
        __mainMenuBtn[0].setText("New Game");
        __mainMenuBtn[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // display game settings menu for settings of new game
                setGameSettingsPanel(); 
                displayPanel(__gameSettingsPanel);            }
        });        

        __mainMenuBtn[1].setText("Continue");
        __mainMenuBtn[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLoadMenuPanel();
                displayPanel(__loadMenuPanel);                  
            }
        });

        __mainMenuBtn[2].setText("Exit");
        __mainMenuBtn[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // destroy current frame/window and close the Java VM (if there is no other frame exist)
                dispose();
            }
        });

        // set a welcome message
        __introLabel = new JLabel();
        __introLabel.setForeground(ColorScheme.RED.getColor());
        
        __introLabel.setText(
            "<html><p>" +
                "<pre>  Warriors</pre>" +
                "<pre>     of</pre>" +
                "<pre>PegSolitaire</pre>" +
            "</p></html>");
        
        __introLabel.setIcon(new ImageIcon("system/img/warrior.png"));
        __introLabel.setLocation(300, 0);
        __introLabel.setSize(300, 500);
        __introLabel.setIconTextGap(20);
        
        __introLabel.setFont(new Font("MV Boli", Font.PLAIN, 18));
        __introLabel.setHorizontalTextPosition(JLabel.CENTER);
        __introLabel.setVerticalTextPosition(JLabel.BOTTOM);
        __mainMenuPanel.add(__introLabel);
    } 

    /**
     * Sets the game settings panel which user can choose board type and game mode
     */
    public void setGameSettingsPanel () {
        JPanel gameTypePanel = setGameTypePanel();
        JPanel boardTypePanel = setBoardTypePanel();
        JPanel commandPanel = setCommandPanel();
        
        JLabel gameModeText = new JLabel("Game Mode");
        ColorScheme.setColor(gameModeText, ColorScheme.BLACK, ColorScheme.RED);
        gameModeText.setFont(new Font("MV Boli", Font.PLAIN, 30));
        gameModeText.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel gameBoardText = new JLabel("Game Board");
        ColorScheme.setColor(gameBoardText, ColorScheme.BLACK, ColorScheme.RED);
        gameBoardText.setFont(new Font("MV Boli", Font.PLAIN, 30));
        gameBoardText.setHorizontalAlignment(JLabel.CENTER);

        // add two panel to the top Game Settings panel
        __gameSettingsPanel = new JPanel(new GridLayout(5, 1));
        ColorScheme.setColor(__gameSettingsPanel, ColorScheme.BLACK);
        __gameSettingsPanel.add(gameModeText);
        __gameSettingsPanel.add(gameTypePanel);
        __gameSettingsPanel.add(gameBoardText);
        __gameSettingsPanel.add(boardTypePanel);
        __gameSettingsPanel.add(commandPanel);


        // __gameSettingsPanel.add(gameTypePanel, BorderLayout.NORTH);
        // __gameSettingsPanel.add(boardTypePanel, BorderLayout.CENTER);
        // __gameSettingsPanel.add(commandPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets the game type selection panel
     * @return game type panel
     */
    private JPanel setGameTypePanel () {
        JPanel gameSetPanel = new JPanel(); 
        ColorScheme.setColor(gameSetPanel, ColorScheme.BLACK, ColorScheme.RED);

        // JLabel text = new JLabel("Game Mode");
        // ColorScheme.setColor(text, ColorScheme.BLACK, ColorScheme.RED);
        // text.setFont(new Font("MV Boli", Font.PLAIN, 30));
        // text.setHorizontalAlignment(JLabel.CENTER);
        // gameSetPanel.add(text);
        
        __gameTypeBtn = new JRadioButton[2];   
        ButtonGroup playGroup = new ButtonGroup();
        for (int i = 0; i < __gameTypeBtn.length; ++i) {
            __gameTypeBtn[i] = new JRadioButton(); 
            __gameTypeBtn[i].addActionListener(GAME_SETTINGS_EVENT_HANDLER);
            ColorScheme.setColor(__gameTypeBtn[i], ColorScheme.BLACK, ColorScheme.RED);
            // add related buttons to the same group
            playGroup.add(__gameTypeBtn[i]);
            gameSetPanel.add(__gameTypeBtn[i]);
        }        
        
        // initially set as null, user will choose game type
        __gameType = null; 
        __gameTypeBtn[0].setText("User");
        __gameTypeBtn[1].setText("Computer");
        
        return gameSetPanel;
    } 

    /**
     * Sets the board type panel which user can choose game board
     * @return board type panel
     */
    private JPanel setBoardTypePanel () {
        // set the board type buttons (six different board)
        JPanel boardTypePanel = new JPanel(/* new GridLayout(2, 3, 0, 50) */);
        ColorScheme.setColor(boardTypePanel, ColorScheme.BLACK, ColorScheme.RED);
        __boardTypeBtn = new JRadioButton[6];  // 
        ButtonGroup boardGroup = new ButtonGroup();
        for (int i = 0; i < __boardTypeBtn.length; ++i) {
            __boardTypeBtn[i] = new JRadioButton();
            // set the text at the bottom center of the button
            __boardTypeBtn[i].setHorizontalTextPosition(JButton.CENTER);
            __boardTypeBtn[i].setVerticalTextPosition(JButton.BOTTOM);
            __boardTypeBtn[i].setIconTextGap(20);
            ColorScheme.setColor(__boardTypeBtn[i], ColorScheme.BLACK, ColorScheme.RED);
            __boardTypeBtn[i].addActionListener(GAME_SETTINGS_EVENT_HANDLER);

            // add buttons to the same group to permit only one selection
            boardGroup.add(__boardTypeBtn[i]);
            boardTypePanel.add(__boardTypeBtn[i]);
        }        

        // initially set as null, user will choose board type
        __boardType = null;

        __boardTypeBtn[0].setText("French");
        __boardTypeBtn[0].setIcon(new ImageIcon("system/img/frenchBoard.png"));
        __boardTypeBtn[1].setText("German");
        __boardTypeBtn[1].setIcon(new ImageIcon("system/img/germanBoard.png"));
        __boardTypeBtn[2].setText("Asymetrical");
        __boardTypeBtn[2].setIcon(new ImageIcon("system/img/asymetricalBoard.png"));
        __boardTypeBtn[3].setText("English");
        __boardTypeBtn[3].setIcon(new ImageIcon("system/img/englishBoard.png"));
        __boardTypeBtn[4].setText("Diamond");
        __boardTypeBtn[4].setIcon(new ImageIcon("system/img/diamondBoard.png"));
        __boardTypeBtn[5].setText("Triangular");
        __boardTypeBtn[5].setIcon(new ImageIcon("system/img/triangularBoard.png"));
        return boardTypePanel;
    }

    /**
     * Sets the panel which user can create game or back to the main menu
     * @return command panel 
     */
    private JPanel setCommandPanel () {
        // set start and back button
        JPanel commandPanel = new JPanel();
        ColorScheme.setColor(commandPanel, ColorScheme.BLACK);

        Font menuFont = new Font("MV Boli", Font.PLAIN, 25);

        // create button not enable till board and game type selected 
        __createGameBtn = menuBtn("Create", ColorScheme.BLACK, ColorScheme.RED, false);
        __createGameBtn.setFont(menuFont);

        // create game event handler (create new game (computer or user))
        __createGameBtn.addActionListener(new ActionListener() {
            // make one movement per second 
            // (500ms select movement, 500ms apply movement)
            int delay = 500; 
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // This code will be called once the timeout of 1/2 seconds has been passed
                    // if user exit from game panel, stop the auto play
                    if (__curDisplayPanel != __gamePanel)
                        ((Timer)e.getSource()).stop();

                    if (__gamePanel.curMovement().start() == null) {
                        // if random movement cannot made that means game is over
                        if (__gamePanel.curMovement().setRandomMovement()) {
                            __gamePanel.curMovement().start().setSelected(true); 
                            __gamePanel.curMovement().end().setPossible(true);
                        }
                        else {
                            // display game score
                            JOptionPane.showMessageDialog(__gamePanel, String.format(
                                "Number of Movement: %d\n" + 
                                "   Remaining Peg: %d", 
                                __gamePanel.numOfMov(), __gamePanel.numOfPeg()
                            ), "Game is Over", JOptionPane.INFORMATION_MESSAGE);     
                            ((Timer)e.getSource()).stop();
                        }
                    }
                    else if (__gamePanel.move(__gamePanel.curMovement()))
                        __gamePanel.curMovement().setStart(null);
                }
            };

            @Override
            public void actionPerformed(ActionEvent e) {
                if (__curDisplayPanel == __loadMenuPanel) {
                    boolean done = false;
                    // get the password
                    do {
                        String password = JOptionPane.showInputDialog(__loadMenuPanel, "Enter your password", "User Login", JOptionPane.QUESTION_MESSAGE);
                        if (__userList.get(__selectedUserIndex)[1].equals(password)) {
                            done = true;
                            // create the game which properties specified
                            String username = __userList.get(__selectedUserIndex)[0];
                            setGamePanel(username);
                            displayPanel(__gamePanel);
                        }
                        else if (password == null) // user hits cancel
                            done = true;                        
                        else {
                            int selection = JOptionPane.showConfirmDialog(__loadMenuPanel, "Wrong password, try again", "Error", JOptionPane.ERROR_MESSAGE);
                            done = (selection == 0) ? false : true; // returns 0 for yes
                        }
                    } while (!done);
                }
                else {
                    if (__gameType == GamePanel.GameMode.COMPUTER)
                        new Timer(delay, taskPerformer).start();
                    // create the game which properties specified
                    setGamePanel();
                    displayPanel(__gamePanel);
                }                  
            }
        });

        __backToMainMenuBtn = menuBtn("Back", ColorScheme.BLACK, ColorScheme.RED, true);
        __backToMainMenuBtn.setFont(menuFont);
        __backToMainMenuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (__curDisplayPanel == __loadMenuPanel) {
                    setMainMenuPanel();
                    displayPanel(__mainMenuPanel);
                }
                else { // game settings panel
                    setGameSettingsPanel(); // clear settings panel
                    setMainMenuPanel();
                    displayPanel(__mainMenuPanel);
                }
            }
        });

        commandPanel.add(__createGameBtn);
        commandPanel.add(__backToMainMenuBtn);    

        return commandPanel;
    }

    /**
     * Sets load game menu which user can select and load the saved games
     */
    private void setLoadMenuPanel () {
        try (Scanner reader = new Scanner(new File("system/login.txt"));) {
            __userList = new ArrayList<String[]>();
            // read each username and password
            while (reader.hasNextLine()) {
                String[] user = reader.nextLine().split(", ");  // username, password
                __userList.add(user);
            }

            // create userListPanel
            JPanel usersPanel = new JPanel(new GridLayout(__userList.size(), 1));
            ColorScheme.setColor(usersPanel, ColorScheme.BLACK, ColorScheme.RED);

            // display the usernames with buttons
            __userListBtn = new JRadioButton[__userList.size()];
            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < __userListBtn.length; ++i) {
                __userListBtn[i] = new JRadioButton(__userList.get(i)[0]); 
                ColorScheme.setColor(__userListBtn[i], ColorScheme.BLACK, ColorScheme.RED);
                __userListBtn[i].addActionListener(LOAD_MENU_EVENT_HANDLER);
                group.add(__userListBtn[i]);
                usersPanel.add(__userListBtn[i]);
            }

            JPanel commandPanel = setCommandPanel();
            __loadMenuPanel = new JPanel(new GridLayout(2, 1));
            __loadMenuPanel.setBackground(ColorScheme.BLACK.getColor());
            __loadMenuPanel.add(usersPanel);
            __loadMenuPanel.add(commandPanel);
        }
        catch (FileNotFoundException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }    
    }
    
    private class LoadMenuEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // break must be exist, not any for condition 
            // if not user can made only one choose which 
            // is first selection even if another ratio button was selected
            for (int i = 0; i < __userListBtn.length; ++i)
                if (__userListBtn[i] == e.getSource()) {
                    __selectedUserIndex = i;
                    __createGameBtn.setEnabled(true);
                    break; 
                }
        }
    }

    /**
     * Sets the game panel for user to play the game
     * @param filename which contains the game board and other game specific datas
     * @return true for successful execution 
     */
    public boolean setGamePanel (String filename) {
        if (__gamePanel != null)
            remove(__gamePanel);

        __homeBtn = new JButton();
        __homeBtn.addActionListener(HOME_BUTTON_EVENT_HANDLER);
        __gamePanel = new GamePanel(__homeBtn, filename);
        return __gamePanel.gameBoard() != null;
    }


    /**
     * Sets the game panel
     */
    public void setGamePanel () {
        if (__gamePanel != null)
            remove(__gamePanel);

        __homeBtn = new JButton();
        __homeBtn.addActionListener(HOME_BUTTON_EVENT_HANDLER);
        __gamePanel = new GamePanel(__homeBtn, __gameType, __boardType);
    }

    private class HomeButtonEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // ask if user wants to save the progress (only in USER MODE)
            if (__gamePanel.gameMode() == GameMode.USER) {
                int select = JOptionPane.showConfirmDialog(
                    __gamePanel, "Save your progress?", "Save Progress", JOptionPane.YES_NO_CANCEL_OPTION);
                if (select == 0) // yes(0), no(1), cancel(2)
                    __gamePanel.saveGameButton().doClick();
            }
            
            setMainMenuPanel(); 
            displayPanel(__mainMenuPanel);                    
        }
    }

    /**
     * Display the given panel in game frame 
     * @param panel next panel for display the screen
     */
    public void displayPanel (JPanel panel) {
        if (__curDisplayPanel != panel) {
            if (__curDisplayPanel != null)
                remove(__curDisplayPanel);
            
            __curDisplayPanel = panel;
            
            // add menu panel and update the game frame
            add(__curDisplayPanel);
            SwingUtilities.updateComponentTreeUI(this); 
        }
    }

    private class GameSettingsEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean selected = false;

            // GAME TYPE BUTTONS
            for (int i = 0; i < __gameTypeBtn.length && !selected; ++i)
                if (e.getSource() == __gameTypeBtn[i]) {
                    __gameType = __gameTypeBtn[i].getText().equals("User") ? 
                        GamePanel.GameMode.USER : GamePanel.GameMode.COMPUTER;
                    selected = true;
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
                }
                
            // if two selection made, enable the button which creates game  
            if (__boardType != null && __gameType != null) 
                __createGameBtn.setEnabled(true);
            else
                __createGameBtn.setEnabled(false);            
        }
    }

    /**
     * Creates an menu button
     * @param text button text
     * @param listener action listener for button
     * @param bg background
     * @param fg foreground
     * @param isEnable enable for click or not
     * @return new created button
     */
    private JButton menuBtn (String text, ColorScheme bg, ColorScheme fg, boolean isEnable) {
        JButton btn = new JButton(text);
        ColorScheme.setColor(btn, bg, fg);
        btn.setEnabled(isEnable);
        return btn;
    }
}

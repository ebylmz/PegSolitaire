/**
 * @file    GamePanel.java
 * @author  Emirkan Burak Yılmaz 
 * @brief   Peg Solitaire Game Panel Implementation
 * @version 0.1
 * @date    2022-01-28
 * 
 * @copyright Copyright (c) 2021
 */

package pegsolitaire;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.border.Border;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pegsolitaire.Cell.CellType;


/** Game Panel of PegSolitaire */
public class GamePanel extends JPanel {
    /** Game/Play mode */ 
    public static enum GameMode {USER, COMPUTER}

    /** Board types */
    public static enum BoardType {FRENCH, GERMAN, ASYMETRICAL, ENGLISH, DIAMOND, TRIANGULAR}

    private final GamePlayEventHandler gamePlayEventHandler = new GamePlayEventHandler();
    private final SaveButtonHandler saveButtonHandler = new SaveButtonHandler(this);

    private JPanel __boardPanel;
    private Cell __board[][]; // game board for checking validty of movement

    private JPanel __topControlPanel; // contains undo and home button
    private JButton __undoBtn;  
    private JButton __homeBtn;  

    private JPanel __bottomControlPanel;  // contains the saveGame and auto movement button
    private JButton __saveGameBtn;
    private JButton __nextMovBtn;

    private GameMode __gameMode;
    private Movement __curMov; // keeps the current movement (necassary for undo movement)
    private Stack<Movement> __allMov;   // keeps all the movement that made so far
    private Vector<Cell> __nextPossibleCell; // keps the possible end cells for current movement
    private int __numOfMov;
    private int __numOfPeg;

    /**
     * Creates GamePanel with given configuration informations
     * @param homeButton home button to exit GamePanel
     * @param gameMode play mode of tge game
     * @param boardType board type of the game
     */
    public GamePanel (JButton homeButton, GameMode gameMode, BoardType boardType) {
        setLayout(new BorderLayout());
        // initialize the game board selected by user
        __gameMode = gameMode;
        setGameBoard(boardType);
        setTopControlPanel(homeButton);
        setBottomControlPanel();
    }

    /**
     * Creates the game panel with the given the file which contains configuration information such as game board, game type
     * @param homeButton
     * @param filename
     */
    public GamePanel (JButton homeButton, String filename) {
        setLayout(new BorderLayout());
        // first set Top control panel because loadGame function
        // uses homeButton in case of exceptions to return the main menu
        loadGame("user/boards/" + filename + ".txt");
        setTopControlPanel(homeButton);
        setBottomControlPanel();
    }

    /**
     * Checks if given user informations indicates registered user or not
     * @param username
     * @param password
     * @return 0 for registered, 1 for non-registered, 2 for registered but wrong password 
     */
    public int isRegistered (String username, String password) {
        int status = 1; // assume user is not registered
        try (Scanner reader = new Scanner(new File("user/login.txt"));) {

            while (reader.hasNextLine() && status == 1) {
                String[] user = reader.nextLine().split(", ");
                if (username.equals(user[0]))
                    status = (password.equals(user[1])) ? 0 : 2;
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Something went wrong");
            e.printStackTrace();
        }
        return status;
    }

    /**
     * Registeres given user
     * @param username
     * @param password
     */
    public void registerUser (String username, String password) {
        // open file in append mode
        try (FileWriter writer = new FileWriter("user/login.txt", true);) {
            writer.write(String.format("%s, %s\n", username, password));
        }
        catch (IOException e) {
            System.err.println("Something went wrong");
            e.printStackTrace();           
        }
    }

    /*** Game score */
    public double score () {
        // max score is 100 (when 1 peg left)
        return (double) numOfPeg() / 100.0;
    }

    /*** Number of movement that made so far */
    public int numOfMov () {return __numOfMov;}

    /*** Number of remaining peg */
    public int numOfPeg () {
        int n = 0;
        for (int i = 0; i < __board.length; ++i)
            for (int j = 0; j < __board[i].length; ++j)
                if (__board[i][j].getCellType() == Cell.CellType.PEG)
                    ++n;
        return n;
    }

    /*** SaveGame game button*/
    public JButton saveGameButton () {return __saveGameBtn;}

    /*** Game board*/
    public Cell[][] gameBoard() {return __board;}

    /*** All the movement that made so far*/
    public Stack<Movement> allMovements () {return __allMov;}

    /*** Current movement */
    public Movement curMovement () {return __curMov;}

    /**
     * Sets the top of the game panel
     * which contains undo and home buttons
     * @param homeButton
     */
    private void setTopControlPanel (final JButton homeButton) {
        Border emptyBorder = BorderFactory.createEmptyBorder();
        
        // add undo button
        __undoBtn = new JButton();
        __undoBtn.setBackground(ColorScheme.BLACK.getColor());
        //set border to empty
        __undoBtn.setBorder(emptyBorder);
        
        __undoBtn.setIcon(new ImageIcon("img/undo.png"));

        // initially not clickable
        __undoBtn.setEnabled(false); 

        // implement an event handler as anonymous inner class
        __undoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        
        // home button
        __homeBtn = homeButton; 
        __homeBtn.setBackground(ColorScheme.BLACK.getColor());
        __homeBtn.setIcon(new ImageIcon("img/home.png"));
        __homeBtn.setBorder(emptyBorder);
        
        // set top control panel which keeps undo and home buttons 
        __topControlPanel = new JPanel(new BorderLayout());
        __topControlPanel.setBackground(ColorScheme.BLACK.getColor());
        __topControlPanel.add(__undoBtn, BorderLayout.WEST);
        __topControlPanel.add(__homeBtn, BorderLayout.EAST);        
        // add control panel to the top of the super panel
        add(__topControlPanel, BorderLayout.NORTH);    
    }

    /**
     * Sets bottom control panel which contains auto movement and saveGame button
     */
    private void setBottomControlPanel () {
        Border emptyBorder = BorderFactory.createEmptyBorder();

        __nextMovBtn = new JButton();
        __nextMovBtn.setIcon(new ImageIcon("img/playAuto.png"));
        __nextMovBtn.setBackground(ColorScheme.BLACK.getColor());
        __nextMovBtn.setBorder(emptyBorder);

        // implement an event handler as anonymous inner class
        __nextMovBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRandom();
                if (isGameOver())
                    displayGameOverMessage();
            }
            
        });

        __saveGameBtn = new JButton();
        __saveGameBtn.setIcon(new ImageIcon("img/save.png"));
        __saveGameBtn.setBackground(ColorScheme.BLACK.getColor());
        __saveGameBtn.setBorder(emptyBorder);
        __saveGameBtn.addActionListener(saveButtonHandler);
        
        if (__gameMode == GameMode.COMPUTER) {
            __nextMovBtn.setEnabled(false);
            __saveGameBtn.setEnabled(false);
        }

        // set bottom control panel which keeps undo and home buttons 
        __bottomControlPanel = new JPanel(new BorderLayout());
        __bottomControlPanel.setBackground(ColorScheme.BLACK.getColor());
        __bottomControlPanel.add(__nextMovBtn, BorderLayout.WEST);
        __bottomControlPanel.add(__saveGameBtn, BorderLayout.EAST);
        // add control panel to the bottom of the super panel
        add(__bottomControlPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets the game board
     * @param board type
     */
    public void setGameBoard (BoardType t) {
        if (__boardPanel != null)
            remove(__boardPanel);
            
        // set Board Panel (keeps each buttons to represent cells of PegSolitaire)
        __boardPanel = new JPanel();
        __boardPanel.setBackground(ColorScheme.BLACK.getColor());
        __boardPanel.setBorder(null);
        switch (t) {
            case FRENCH: 
                setFrenchBoard();break;
            case GERMAN: 
                setGermanBoard(); break;
            case ASYMETRICAL:
                setAsymmetricalBoard();break;
            case ENGLISH: 
                setEnglishBoard(); break;
            case DIAMOND: 
                setDiamondBoard(); break;
            case TRIANGULAR: 
                setTriangleBoard(); break;
        }

        add(__boardPanel);  // add board panel to the JFrame
        __numOfMov = 0;
        __numOfPeg = numOfPeg();

        // reset/init movement for new board
        __curMov = new Movement(__board);
        __allMov = new Stack<Movement>();
    }

    private void setGermanBoard() {
        __boardPanel.setLayout(new GridLayout(9, 9)); 
        __board = new Cell[9][9];

        final CellType cellValue[][] = {
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.PEG, CellType.PEG, CellType.PEG, CellType.WALL, CellType.WALL, CellType.WALL},
            {CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG}
        };

        for (int i = 0; i < __board.length; ++i) {
            int col = (3 <= i && i <= 5) ? 1 : 0;

            for (int j = 0; j < __board[i].length; ++j)
                __board[i][j] = new Cell(cellValue[col][j], __boardPanel, gamePlayEventHandler);    
        }
        __board[4][4].setCellType(CellType.EMPTY);
    }

    private void setFrenchBoard () {
        __boardPanel.setLayout(new GridLayout(7, 7));
        __board = new Cell[7][7];

        for (int i = 0, n = 2, m = 5; i < 2; ++i, --n, ++m) {
            for (int j = 0; j < n; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
            
            for (int j = n; j < m; ++j)
                __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);

            for (int j = m; j < 7; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
        }

        for (int i = 2; i < 5; ++i)
            for (int j = 0; j < 7; ++j)
                __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);

        for (int i = 5, n = 1, m = 6; i < 7; ++i, ++n, --m) {
            for (int j = 0; j < n; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
            
            for (int j = n; j < m; ++j)
                __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);

            for (int j = m; j < 7; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
        }

        __board[2][3].setCellType(CellType.EMPTY);
    }

    private void setAsymmetricalBoard () {
        __boardPanel.setLayout(new GridLayout(8, 8)); 
        __board = new Cell[8][8];

        final CellType cellValue[][] = {
            {CellType.WALL, CellType.WALL, CellType.PEG, CellType.PEG, CellType.PEG, CellType.WALL, CellType.WALL, CellType.WALL},
            {CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG}
        };

        for (int i = 0; i < __board.length; ++i) {
            int col = (3 <= i && i <= 5) ? 1 : 0;
            for (int j = 0; j < __board[i].length; ++j) 
                __board[i][j] = new Cell(cellValue[col][j], __boardPanel, gamePlayEventHandler);  
        }
        __board[4][3].setCellType(CellType.EMPTY);
    }

    private void setEnglishBoard () {
        __boardPanel.setLayout(new GridLayout(7, 7)); 
        __board = new Cell[7][7];

        final Cell.CellType cellValue[][] = {
            {CellType.WALL, CellType.WALL, CellType.PEG, CellType.PEG, CellType.PEG, CellType.WALL, CellType.WALL},
            {CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG, CellType.PEG}
        };

        for (int i = 0; i < __board.length; ++i) {
            int col = (2 <= i && i <= 4) ? 1 : 0;
            for (int j = 0; j < __board[i].length; ++j) 
                __board[i][j] = new Cell(cellValue[col][j], __boardPanel, gamePlayEventHandler);  
        }
        __board[3][3].setCellType(CellType.EMPTY);
    }
    
    private void setDiamondBoard () {
        __boardPanel.setLayout(new GridLayout(9, 9));
        __board = new Cell[9][9];

        for (int i = 0, n = 4, m = 5; i < 4; ++i, --n, ++m) {
            for (int j = 0; j < n; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);

            for (int j = n; j < m; ++j)
                __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);

            for (int j = m; j < 9; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
        }

        for (int i = 0; i < 9; ++i)
            __board[4][i] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);

        for (int i = 5, n = 1, m = 8; i < 9; ++i, ++n, --m) {
            for (int j = 0; j < n; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);

            for (int j = n; j < m; ++j)
                __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);

            for (int j = m; j < 9; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
        }
        __board[4][4].setCellType(CellType.EMPTY);
    }

    private void setTriangleBoard () {
        __boardPanel.setLayout(new GridLayout(8, 8));
        __board = new Cell[8][8];

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < i; ++j)
                __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);
            for (int j = i; j < 8; ++j)
                __board[i][j] = new Cell(CellType.WALL , __boardPanel, gamePlayEventHandler);
        }
        __board[0][0].setCellType(CellType.EMPTY);
        __board[0][0].setEnabled(true);
    } 
    
    /**
     * Checks if game is over
     * @return true for ended game
     */
    public boolean isGameOver () {
        for (int i = 0; i < __board.length; ++i)
            for (var cell : __board[i])
                if (canMakeMovement(cell))
                    return false;
        return true;
    }

    /**
     * Checks if there can be some movement(s) for given cell button 
     * @param btn
     * @return true for valid movement
     */
    public boolean canMakeMovement (Cell c) {
        Movement mov = new Movement(__board, c);
        return      mov.setMovement(c, Movement.Direction.UP) ||
                    mov.setMovement(c, Movement.Direction.DOWN) ||
                    mov.setMovement(c, Movement.Direction.RIGHT) ||
                    mov.setMovement(c, Movement.Direction.LEFT);
    }

    private class SaveButtonHandler implements ActionListener {
        private JPanel topPanel;

        public SaveButtonHandler(JPanel topPanel) {this.topPanel = topPanel;}

        @Override
        public void actionPerformed(ActionEvent e) {
            // get the filename to saveGame the current game progress
            String username = JOptionPane.showInputDialog(
                topPanel, "Enter your user name", "Username", JOptionPane.QUESTION_MESSAGE);
            if (username != null) { // user enters cancel button
                Boolean done = false;
                do {
                    String password = JOptionPane.showInputDialog(
                        topPanel, "Enter your password", "Password",  JOptionPane.QUESTION_MESSAGE);
                    if (password == null)   // user hits cancel
                        done = true;
                    else {
                        switch (isRegistered(username, password)) {
                            case 0: // registered user
                                saveGame("user/boards/" + username + ".txt");
                                done = true;
                                break;
                            case 1: // non-registered user
                                registerUser(username, password);
                                saveGame("user/boards/" + username + ".txt");
                                done = true;
                                break;
                            case 2: // registered but wrong password
                                int select = JOptionPane.showConfirmDialog(topPanel, "Wrong password. Try again", "Error", JOptionPane.ERROR_MESSAGE);
                                if (select != 0)    // user hits no or cancel
                                    done = true;
                                break;
                        } 
                    }
                } while (!done);
            }    
        }
    }

    private class GamePlayEventHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Cell selectedCell = (Cell) e.getSource();
            // if this is first cell selection (start must be null)
            if (__curMov.start() == null) {
                // ignore selection of the cell which are Wall and Empty cells
                if (selectedCell.getCellType() == CellType.PEG) {
                    __curMov.setStart(selectedCell);
                    
                    __nextPossibleCell = __curMov.nextPossibleMov();
                    if (__nextPossibleCell == null)
                        __curMov.setStart(null);
                    else {
                        // set hover effect on selected cell                    
                        selectedCell.setSelected(true);

                        // show possible movements by hovering cells
                        for (var c : __nextPossibleCell)
                            c.setPossible(true);
                    }
                }
            }
            // if start cell was selected, current selected cell should be end cell
            else if (selectedCell != __curMov.start()) {
                // set hover effect on selected cell
                __curMov.setEnd(selectedCell);
                // apply movement
                if (move(__curMov)) {
                    if (isGameOver())
                        displayGameOverMessage();
                }
                else // movement fails, turn back original board view
                    __curMov.start().setSelected(false);

                // set current Movement as null for next movement
                __curMov.setStart(null);
                __curMov.setEnd(null);

                // leave the possible cells as unselected 
                for (var c : __nextPossibleCell)
                    if (c != selectedCell)
                        c.setPossible(false);
            }
        }
    }

    private void displayGameOverMessage () {
        JOptionPane.showMessageDialog(this, String.format(
            // "      Game is over\n" + 
            "Number of Movement: %d\n" + 
            "   Remaining Peg: %d", 
            __numOfMov, __numOfPeg
        ), "Game is Over", JOptionPane.INFORMATION_MESSAGE); 
    }

    /**
     * Applies given movement
     * @param mov
     * @return true for valid movement
     */
    public boolean move (Movement mov) {
        if (mov.isValidMovement()) {
            mov.start().setCellType(CellType.EMPTY);
            mov.jump().setCellType(CellType.EMPTY);
            mov.end().setCellType(CellType.PEG);
            
            ++__numOfMov;
            --__numOfPeg;
            // add the current movement to the movements stack (copy of it!)
            __allMov.push(mov.clone());
            if (__gameMode == GameMode.USER) {
                if (!__undoBtn.isEnabled())
                __undoBtn.setEnabled(true);
                if (isGameOver())
                __nextMovBtn.setEnabled(false);
            }
            return true;
        } else
            return false;
    }

    /**
     * Applies random movement 
     * @return false if there is no movement left means when the game is over
     */
    public boolean moveRandom () {
        Movement mov = new Movement(__board); 
        if (mov.setRandomMovement()) {
            move(mov);
            return true;
        }
        return false;
    }

    /**
     * undo last movement
     * @return false if there is no previos movement made
     */
    public boolean undo () {
        // if there is a valid movement made before, apply reverse of it
        if (__undoBtn.isEnabled()) {
            Movement lastMov = __allMov.pop();
            lastMov.start().setCellType(CellType.PEG);
            lastMov.jump().setCellType(CellType.PEG);
            lastMov.end().setCellType(CellType.EMPTY);
            --__numOfMov;
            ++__numOfPeg;
            
            if (__gameMode == GameMode.USER) {
                if (__allMov.size() == 0)
                    __undoBtn.setEnabled(false);
                if (!__nextMovBtn.isEnabled())
                    __nextMovBtn.setEnabled(true);
            }
            return true;
        }
        return false;
    }

    /**
     * Saves the current game status (board, numOfMov, etc.) to the given file
     * @param filename
     */
    public void saveGame (String filename) {
        try {
            FileWriter writer = new FileWriter(filename);        
            // each boards are rectangular (main boards are square, user defined ones must be rectangular)
            writer.write(String.format("%s %d %d %d\n", 
                __gameMode, __numOfMov, __board.length, __board[0].length));
            for (int i = 0; i < __board.length; ++i) {
                for (int j = 0; j < __board[i].length; ++j) {
                    writer.write(__board[i][j].toString()); 
                    if (j < __board[i].length - 1) writer.write(" ");
                }
                if (i < __board.length - 1) writer.write("\n");
            }
            writer.close();
        }
        catch (IOException e) {
            System.err.println("Something went wrong");
            e.printStackTrace();        
        }
        catch (NullPointerException e) {
            System.err.println("Empty Game Board");
          e.printStackTrace();        
        }
    }

    /**
     * Loads the game status (board, numOfMov, etc.) from the given file
     * @param filename
     */
    public void loadGame (String filename) {
        // scanner will close itself automaticly (required AutoCloseable interface)
        try (Scanner reader = new Scanner(new File(filename));) {
            // first line contains Game configurations
            // GameMode(string), NumOfMov(int) BoardRow(int) BoardCol(int)
            String gameMode = reader.next();
            int numOfMov = reader.nextInt(); 
            int row = reader.nextInt();
            int col = reader.nextInt();

            reader.nextLine();  // skip the rest of the line

            if (gameMode.equals("USER"))
                __gameMode = GameMode.USER;
            else if (gameMode.equals("COMPUTER"))
                __gameMode = GameMode.COMPUTER;
            else
                throw new IllegalArgumentException();

            if (__boardPanel != null)
                remove(__boardPanel);
            
            // set Board Panel (keeps each buttons to represent cells of PegSolitaire)
            __boardPanel = new JPanel(new GridLayout(row, col));
            __boardPanel.setBackground(ColorScheme.BLACK.getColor());
            __boardPanel.setBorder(null);

            __board = new Cell[row][col];
            for (int i = 0; i < row; ++i) {
                String line = reader.nextLine();
                for (int j = 0; j < col; ++j) {
                    // skip blank char and pass next value  
                    switch (line.charAt(j * 2)) {
                        case '.':
                            __board[i][j] = new Cell(CellType.EMPTY , __boardPanel, gamePlayEventHandler); 
                            break;
                        case 'P':
                            __board[i][j] = new Cell(CellType.PEG, __boardPanel, gamePlayEventHandler);
                            break;
                        case ' ':
                            //! since walls are unclickable, no action listener required
                            __board[i][j] = new Cell(CellType.WALL , __boardPanel, null);   
                            break;
                        default:
                            throw new IllegalArgumentException();  
                    }
                }
            }
            // add board panel to the JFrame
            add(__boardPanel);  
            __numOfMov = numOfMov;
            __numOfPeg = numOfPeg();
            __curMov = new Movement(__board);
            __allMov = new Stack<Movement>();            
        }
        catch (FileNotFoundException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid file format");
            __board = null; // set board as null, since not fully filled
            JOptionPane.showMessageDialog(
                this, "Given file not in suitable format", "loadGame Game", JOptionPane.ERROR_MESSAGE);
            __homeBtn.doClick();
        }
    }
}

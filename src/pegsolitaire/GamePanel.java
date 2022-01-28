package pegsolitaire;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Random;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.border.Border;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {
    public static enum GameMode {USER, COMPUTER}

    public static enum BoardType {FRENCH, GERMAN, ASYMETRICAL, ENGLISH, DIAMOND, TRIANGULAR}

    public static enum CellValue {PEG, EMPTY, WALL}

    final String REGISTERED_USER_FILE = "user/login.txt";//!!!!!!!!1
    final String USER_BOARD_PATH = "user/boards";

    // private JPanel __statusPanel;
    private JPanel __boardPanel;
    private JButton __gameBoard[][];
    final private ImageIcon __pegIcon = new ImageIcon("img/pegCell.png");
    final private ImageIcon __emptyIcon = new ImageIcon("img/emptyCell.png");
    final private ImageIcon __selectedCell = new ImageIcon("img/selectedCell.png");
    final private ImageIcon __possibleCell = new ImageIcon("img/possibleCell.png");

    private JPanel __topControlPanel;
    private JButton __undoBtn;
    private JButton __homeBtn;

    private JPanel __bottomControlPanel;
    private JButton __saveBtn;
    private JButton __nextMovBtn;
    

    private GameMode __gameMode;
    private Movement __curMov; // keeps the current movement (necassary for undo movement)
    private Stack<Movement> __allMov;
    private Vector<JButton> __nextPossibleBtn;
    private int __numOfMov;
    private int __numOfPeg;

    /* for new game */
    public GamePanel (JButton homeButton, GameMode gameMode, BoardType boardType) {
        setLayout(new BorderLayout());
        // getContentPane().setBackground(ColorScheme.BLACK.getColor()); //
    
        // initialize the game board selected by user
        setGameBoard(boardType);
        setTopControlPanel(homeButton);
        setBottomControlPanel();
        __gameMode = gameMode;
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
                System.out.printf("%s %s\n", user[0], user[1]);
                if (username.equals(user[0]))
                    status = (password.equals(user[1])) ? 0 : 2;
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Something went wrong");
            //! then what?
            e.printStackTrace();
        }
        return status;
    }

    /**
     * registeres given user
     * @param username
     * @param password
     */
    public void registerUser (String username, String password) {
        // open file in append mode
        try (FileWriter writer = new FileWriter("user/login.txt", true);) {
            writer.write(String.format("%s, %s\n", username, password));
        }
        catch (IOException e) {
            System.err.println("Something went wrong.");
            e.printStackTrace();           
        }
    }

    /* for load game */
    public GamePanel (JButton homeButton, String filename) {
        setLayout(new BorderLayout());
        // first set Top control panel because load function
        // uses homeButton in case of exceptions to return the main menu
        setTopControlPanel(homeButton);
        setBottomControlPanel();
        load("user/boards/" + filename + ".txt");
        //! random mode not implemented yet
    }

    public double score () {
        // max score is 100 (when 1 peg left)
        return (double) numOfPeg() / 100.0;
    }

    public int numOfMov () {return __numOfMov;}

    public int numOfPeg () {
        int n = 0;
        for (int i = 0; i < __gameBoard.length; ++i)
            for (int j = 0; j < __gameBoard[i].length; ++j)
                if (__gameBoard[i][j].getActionCommand().equals("P"))
                    ++n;
        return n;
    }

    public JButton saveButton () {return __saveBtn;}

    public JButton[][] gameBoard() {return __gameBoard;}

    public Stack<Movement> allMovements () {return __allMov;}

    public Movement curMovement () {return __curMov;}

    private void setTopControlPanel (final JButton homeButton) {
        Border emptyBorder = BorderFactory.createEmptyBorder();
        
        // add undo button
        __undoBtn = new JButton();
        __undoBtn.setBackground(ColorScheme.BLACK.getColor());
        //set border to empty
        __undoBtn.setBorder(emptyBorder);
        
        // __undoBtn.setHorizontalTextPosition(JButton.CENTER);
        // __undoBtn.setVerticalTextPosition(JButton.BOTTOM);
        // __undoBtn.setPreferredSize(new Dimension(50, 50));
        __undoBtn.setIcon(new ImageIcon("img/undo.png"));
        __undoBtn.addActionListener(this);
        // initially not clickable
        __undoBtn.setEnabled(false); 
        
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

    private void setBottomControlPanel () {
        Border emptyBorder = BorderFactory.createEmptyBorder();

        __nextMovBtn = new JButton();
        __nextMovBtn.setIcon(new ImageIcon("img/playAuto.png"));
        __nextMovBtn.setBackground(ColorScheme.BLACK.getColor());
        __nextMovBtn.addActionListener(this);
        __nextMovBtn.setBorder(emptyBorder);

        __saveBtn = new JButton();
        __saveBtn.setIcon(new ImageIcon("img/save.png"));
        __saveBtn.setBackground(ColorScheme.BLACK.getColor());
        __saveBtn.addActionListener(this);
        __saveBtn.setBorder(emptyBorder);
        
        // set bottom control panel which keeps undo and home buttons 
        __bottomControlPanel = new JPanel(new BorderLayout());
        __bottomControlPanel.setBackground(ColorScheme.BLACK.getColor());
        __bottomControlPanel.add(__nextMovBtn, BorderLayout.WEST);
        __bottomControlPanel.add(__saveBtn, BorderLayout.EAST);
        // add control panel to the bottom of the super panel
        add(__bottomControlPanel, BorderLayout.SOUTH);
    }

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
        // setGameStatus(); //!! NOT SURE
        // reset/init movement for new board
        __curMov = new Movement(__gameBoard);
        __allMov = new Stack<Movement>();
    }

    private void setGermanBoard() {
        __boardPanel.setLayout(new GridLayout(9, 9)); 
        __gameBoard = new JButton[9][9];

        final String cellValue[][] = {
            {" ", " ", " ", "P", "P", "P", " ", " ", " "},
            {"P", "P", "P", "P", "P", "P", "P", "P", "P"}
        };

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (3 <= i && i <= 5) ? 1 : 0;

            for (int j = 0; j < __gameBoard[i].length; ++j)
                __gameBoard[i][j] = cellValue[col][j].equals("P") ? 
                    cellButton(__boardPanel, CellValue.PEG) : cellButton(__boardPanel, CellValue.WALL);    
        }
        __gameBoard[4][4].setActionCommand(".");
        __gameBoard[4][4].setIcon(__emptyIcon);
    }

    private void setFrenchBoard () {
        __boardPanel.setLayout(new GridLayout(7, 7));
        __gameBoard = new JButton[7][7];

        for (int i = 0, n = 2, m = 5; i < 2; ++i, --n, ++m) {
            for (int j = 0; j < n; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
            
            for (int j = n; j < m; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);

            for (int j = m; j < 7; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
        }

        for (int i = 2; i < 5; ++i)
            for (int j = 0; j < 7; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);

        for (int i = 5, n = 1, m = 6; i < 7; ++i, ++n, --m) {
            for (int j = 0; j < n; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
            
            for (int j = n; j < m; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);

            for (int j = m; j < 7; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
        }

        __gameBoard[2][3].setActionCommand(".");
        __gameBoard[2][3].setIcon(__emptyIcon);
    }

    private void setAsymmetricalBoard () {
        __boardPanel.setLayout(new GridLayout(8, 8)); 
        __gameBoard = new JButton[8][8];

        final String cellValue[][] = {
            {" ", " ", "P", "P", "P", " ", " ", " "},
            {"P", "P", "P", "P", "P", "P", "P", "P"}
        };

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (3 <= i && i <= 5) ? 1 : 0;
            for (int j = 0; j < __gameBoard[i].length; ++j) 
                __gameBoard[i][j] = cellButton(__boardPanel, 
                    cellValue[col][j].equals("P") ? CellValue.PEG : CellValue.WALL);  
        }
        __gameBoard[4][3].setActionCommand(".");
        __gameBoard[4][3].setIcon(__emptyIcon);
    }

    private void setEnglishBoard () {
        __boardPanel.setLayout(new GridLayout(7, 7)); 
        __gameBoard = new JButton[7][7];

        final String cellValue[][] = {
            {" ", " ", "P", "P", "P", " ", " "},
            {"P", "P", "P", "P", "P", "P", "P"}
        };

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (2 <= i && i <= 4) ? 1 : 0;
            for (int j = 0; j < __gameBoard[i].length; ++j) 
                __gameBoard[i][j] = cellButton(__boardPanel, 
                    cellValue[col][j].equals("P") ? CellValue.PEG : CellValue.WALL);  
        }
        __gameBoard[3][3].setActionCommand(".");
        __gameBoard[3][3].setIcon(__emptyIcon);
    }
    
    private void setDiamondBoard () {
        __boardPanel.setLayout(new GridLayout(9, 9));
        __gameBoard = new JButton[9][9];

        for (int i = 0, n = 4, m = 5; i < 4; ++i, --n, ++m) {
            for (int j = 0; j < n; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);

            for (int j = n; j < m; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);

            for (int j = m; j < 9; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
        }

        for (int i = 0; i < 9; ++i)
            __gameBoard[4][i] = cellButton(__boardPanel, CellValue.PEG);

        for (int i = 5, n = 1, m = 8; i < 9; ++i, ++n, --m) {
            for (int j = 0; j < n; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);

            for (int j = n; j < m; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);

            for (int j = m; j < 9; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
        }
        __gameBoard[4][4].setActionCommand(".");
        __gameBoard[4][4].setIcon(__emptyIcon);
    }

    private void setTriangleBoard () {
        __boardPanel.setLayout(new GridLayout(8, 8));
        __gameBoard = new JButton[8][8];

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < i; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);
            for (int j = i; j < 8; ++j)
                __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
        }
        __gameBoard[0][0].setActionCommand(".");
        __gameBoard[0][0].setIcon(__emptyIcon);
        __gameBoard[0][0].setEnabled(true);
    } 
    
    private JButton cellButton (JPanel panel, CellValue val) {
        JButton btn;
        switch (val) {
            case PEG:
                btn = createPegButton(); break;
            case WALL:
                btn = createWallButton(); break;
            case EMPTY:
                btn = createEmptyButton(); break; 
            default:
                btn = null;
        }
        // add new button to the given panel 
        if (btn != null)
            panel.add(btn); 
        return btn;
    }

    private JButton createEmptyButton () {
        JButton btn = new JButton();
        //create an empty border
        Border emptyBorder = BorderFactory.createEmptyBorder();
        //set border to empty
        btn.setBorder(emptyBorder);
        ColorScheme.setColor(btn,  ColorScheme.BLACK, ColorScheme.RED);
        btn.setActionCommand(".");
        btn.setIcon(__emptyIcon);
        btn.addActionListener(this);
        return btn;
    }

    private JButton createPegButton () {
        JButton btn = new JButton();
        //create an empty border
        Border emptyBorder = BorderFactory.createEmptyBorder();
        //set border to empty
        btn.setBorder(emptyBorder);
        ColorScheme.setColor(btn,  ColorScheme.BLACK, ColorScheme.RED);
        btn.setActionCommand("P");
        btn.setIcon(__pegIcon);
        btn.addActionListener(this);
        return btn;
    }
    
    private JButton createWallButton () {
        JButton btn = new JButton();
        //create an empty border
        Border emptyBorder = BorderFactory.createEmptyBorder();
        //set border to empty
        btn.setBorder(emptyBorder);
        ColorScheme.setColor(btn,  ColorScheme.BLACK, ColorScheme.RED);
        btn.setActionCommand(" ");
        btn.setEnabled(false); // set wall buttons as non-clickable
        btn.addActionListener(this);
        return btn;
    }

    public boolean isGameOver() {
        for (int i = 0; i < __gameBoard.length; ++i)
            for (var btn : __gameBoard[i])
                if (canMakeMovement(btn))
                    return false;
        return true;
    }

    public boolean canMakeMovement(JButton btn) {
        Movement mov = new Movement(__gameBoard, btn);
        return      mov.setMovement(btn, Movement.Direction.UP) ||
                    mov.setMovement(btn, Movement.Direction.DOWN) ||
                    mov.setMovement(btn, Movement.Direction.RIGHT) ||
                    mov.setMovement(btn, Movement.Direction.LEFT);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        JButton selectedBtn = (JButton) e.getSource();
        // UNDO EVENT
        if (selectedBtn == __undoBtn) 
            undo();
        // ONE AUTO MOVEMENT EVENT
        else if (selectedBtn == __nextMovBtn)
            moveRandom();
        // SAVE EVENT
        else if (selectedBtn == __saveBtn) {
            // get the filename to save the current game progress
            String username = JOptionPane.showInputDialog(
                this, "Enter your user name", "Username", JOptionPane.QUESTION_MESSAGE);
            if (username != null) { // user enters cancel button
                Boolean done = false;
                do {
                    String password = JOptionPane.showInputDialog(
                        this, "Enter your password", "Password",  JOptionPane.QUESTION_MESSAGE);
                    if (password == null)   // user hits cancel
                        done = true;
                    else {
                        switch (isRegistered(username, password)) {
                            case 0: // registered user
                                save("user/boards/" + username + ".txt");
                                done = true;
                                break;
                            case 1: // non-registered user
                                registerUser(username, password);
                                save("user/boards/" + username + ".txt");
                                done = true;
                                break;
                            case 2: // registered but wrong password
                                int select = JOptionPane.showConfirmDialog(this, "Wrong password. Try again", "Error", JOptionPane.ERROR_MESSAGE);
                                if (select != 0)    // user hits no or cancel
                                    done = true;
                                break;
                        } 
                    }
                } while (!done);
            }    
        }
        // REST OF THEM GAME BOARD BUTTON EVENTS
        else if (__curMov.start() == null) {
            // ignore selection of the cell which are Wall(" ") or Empty(".") cells
            System.out.println("NEW SELECTION");
            if (selectedBtn.getActionCommand().equals("P")) {
                __curMov.setStart(selectedBtn);
                
                __nextPossibleBtn = __curMov.nextPossibleMov();
                if (__nextPossibleBtn == null)
                    __curMov.setStart(null);
                else {
                    // set hover effect on selected button                    
                    // selectedBtn.setBackground(ColorScheme.WHITE.getColor()); 
                    selectedBtn.setIcon(__selectedCell);

                    // show possible movements by hovering buttons
                    for (var btn : __nextPossibleBtn)
                        btn.setIcon(__possibleCell);
                }
            }
        }
        // if start button was selected, current selected button should be end button
        else if (selectedBtn != __curMov.start()) {
            // set hover effect on selected button
            // selectedBtn.setBackground(ColorScheme.WHITE.getColor()); 
            __curMov.setEnd(selectedBtn);
            // apply movement
            if (move(__curMov)) {
                if (isGameOver()) 
                    //! ask user to next step (restart game, main menu ...)
                    JOptionPane.showMessageDialog(this, String.format(
                                // "      Game is over\n" + 
                                "Number of Movement: %d\n" + 
                                "   Remaining Peg: %d", 
                                __numOfMov, __numOfPeg
                    ), "Game is Over", JOptionPane.INFORMATION_MESSAGE);    
            }
            else
                __curMov.start().setIcon(__pegIcon);

            // set current Movement as null for next movement
            __curMov.setStart(null);
            __curMov.setEnd(null);

            // set possible buttons as their previos 
            for (var btn : __nextPossibleBtn)
                if (btn != selectedBtn)
                    btn.setIcon(__emptyIcon);
        }
    }

    public boolean move (Movement mov) {
        if (mov.isValidMovement()) {
            mov.start().setActionCommand(".");
            mov.start().setIcon(__emptyIcon);
            mov.jump().setActionCommand(".");
            mov.jump().setIcon(__emptyIcon);
            mov.end().setActionCommand("P");
            mov.end().setIcon(__pegIcon);
            
            ++__numOfMov;
            --__numOfPeg;
            // add the current movement to the movements stack (copy of it!)
            __allMov.push(mov.clone());  
            if (!__undoBtn.isEnabled())
                __undoBtn.setEnabled(true);
            if (isGameOver())
                __nextMovBtn.setEnabled(false);
            return true;
        } else
            return false;
    }

    public boolean moveRandom () {
        GamePanel.Movement mov = new Movement(__gameBoard); 
        if (mov.setRandomMovement()) {
            move(mov);
            return true;
        }
        return false;
    }

    public boolean undo() {
        // if there is a valid movement made before, apply reverse of it
        if (__undoBtn.isEnabled()) {
            Movement lastMov = __allMov.pop();
            lastMov.start().setActionCommand("P");
            lastMov.start().setIcon(__pegIcon);
            lastMov.jump().setActionCommand("P");
            lastMov.jump().setIcon(__pegIcon);
            lastMov.end().setActionCommand(".");
            lastMov.end().setIcon(__emptyIcon);
            --__numOfMov;
            ++__numOfPeg;
            
            if (__allMov.size() == 0)
                __undoBtn.setEnabled(false);
            if (!__nextMovBtn.isEnabled())
                __nextMovBtn.setEnabled(true);
            return true;
        }
        return false;
    }

    public void save (String filename) {
        try {
            FileWriter writer = new FileWriter(filename);        
            // each boards are rectangular (main boards are square, user defined ones must be rectangular)
            writer.write(String.format("%s %d %d %d\n", 
                __gameMode, __numOfMov, __gameBoard.length, __gameBoard[0].length));
            for (int i = 0; i < __gameBoard.length; ++i) {
                for (int j = 0; j < __gameBoard[i].length; ++j) {
                    writer.write(__gameBoard[i][j].getActionCommand());
                    if (j < __gameBoard[i].length - 1) writer.write(" ");
                }
                if (i < __gameBoard.length - 1) writer.write("\n");
            }
            writer.close();
        }
        catch (IOException e) {
            System.err.println("Something went wrong.");
            e.printStackTrace();        
        }
        catch (NullPointerException e) {
            System.err.println("Empty Game Board.");
            e.printStackTrace();        
        }
    }

    public void load (String filename) {
        // scanner will close itself automaticly (required AutoCloseable interface)
        try (Scanner reader = new Scanner(new File(filename));) {
            // first line contains Game configurations
            // GameMode(string), NumOfMov(int) BoardRow(int) BoardCol(int)
            String str = reader.next();
            int numOfMov = reader.nextInt(); 
            int row = reader.nextInt();
            int col = reader.nextInt();

            reader.nextLine();  // skip the rest of the line

            GameMode gameMode;  //!! NOT USED YET
            if (str.equalsIgnoreCase("USER"))
                gameMode = GameMode.USER;
            else if (str.equalsIgnoreCase("COMPUTER"))
                gameMode = GameMode.COMPUTER;
            else
                throw new IllegalArgumentException();

            if (__boardPanel != null)
                remove(__boardPanel);
            
            // set Board Panel (keeps each buttons to represent cells of PegSolitaire)
            __boardPanel = new JPanel(new GridLayout(row, col));
            __boardPanel.setBackground(ColorScheme.BLACK.getColor());
            __boardPanel.setBorder(null);

            __gameBoard = new JButton[row][col];
            for (int i = 0; i < row; ++i) {
                String line = reader.nextLine();
                for (int j = 0; j < col; ++j) {
                    
                    System.out.printf("%c ", line.charAt(j * 2));
                    // skip blank char and pass next value  
                    switch (line.charAt(j * 2)) {
                        case '.':
                            __gameBoard[i][j] = cellButton(__boardPanel, CellValue.EMPTY);
                            break;
                        case 'P':
                            __gameBoard[i][j] = cellButton(__boardPanel, CellValue.PEG);
                            break;
                        case ' ':
                            __gameBoard[i][j] = cellButton(__boardPanel, CellValue.WALL);
                            break;
                        default:
                            System.out.printf("%c\n", line.charAt(j * 2));
                            throw new IllegalArgumentException();   //!!
                    }
                }
                System.out.printf("\n");
            }

            add(__boardPanel);  // add board panel to the JFrame
            __gameMode = gameMode;
            __numOfMov = numOfMov;
            __numOfPeg = numOfPeg();
            // setGameStatus(); //!! NOT SURE
            // reset/init movement for new board
            __curMov = new Movement(__gameBoard);
            __allMov = new Stack<Movement>();            
        }
        catch (FileNotFoundException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid file format");
            __gameBoard = null; // set board as null, since not fully filled
            JOptionPane.showMessageDialog(
                this, "Given file not in suitable format", "Load Game", JOptionPane.ERROR_MESSAGE);
            __homeBtn.doClick();
        }
    }

    public static class Movement implements Cloneable {
        private JButton[][] __board; // game board for checking validty of movement
        private JButton __startBtn; // start position of movement
        private JButton __jumpBtn; // jump position of movement (between start and end)
        private JButton __endBtn; // end position of movement

        public static enum Direction {UP, DOWN, LEFT, RIGHT}

        public Movement(JButton[][] board, JButton start, JButton end) {
            __board = board;
            try {
                setStart(start);
                setEnd(end);
            } catch (InvalidParameterException e) {
                __startBtn = __endBtn = __jumpBtn = null;
                System.err.println("Invalid parameter for Movement Constructor");
            }
        }

        public Movement(JButton[][] board, JButton start) {this(board, start, null);}

        public Movement(JButton[][] board) {this(board, null, null);}

        public Movement() {this(null, null, null);}

        public JButton start() {
            return __startBtn;
        }

        public JButton end() {
            return __endBtn;
        }

        public JButton jump() {
            return __jumpBtn;
        }

        public Vector<JButton> nextPossibleMov () {
            Vector<JButton> v = new Vector<JButton>();
            if (start() != null)
                for (Direction d : Direction.values())
                    if (setMovement(start(), d))
                        v.add(end());
            return v.size() > 0 ? v : null;
        }

        public void setStart(JButton start) throws InvalidParameterException {
            // be sure given JButton is in the current game board
            if (start != null && findLocation(start) == null)
                throw new InvalidParameterException("given JButtons not exist in game board");
            __startBtn = start;
        }

        public void setEnd(JButton end) {
            // be sure given JButton is in the current game board
            if (end != null && findLocation(end) == null)
                throw new InvalidParameterException("given JButtons not exist in game board");
            __endBtn = end;
        }

        public void setJump() throws InvalidParameterException {
            if (__board == null || start() == null || end() == null)
                throw new NullPointerException("no enough information to find jump button");

            int[] startIndexes = findLocation(start());
            int[] endIndexes = findLocation(end());

            if (startIndexes != null && endIndexes != null) {
                int row = -1; // jump button row
                int col = -1; // jump button coloumn

                // starBtn and endBtn are at same row
                if (startIndexes[0] == endIndexes[0]) {
                    row = endIndexes[0];

                    int diff = endIndexes[1] - startIndexes[1];
                    if (diff == 2)
                        col = endIndexes[1] - 1;
                    else if (diff == -2)
                        col = endIndexes[1] + 1;
                }
                // starBtn and endBtn are at same coloumn
                else if (startIndexes[1] == endIndexes[1]) {
                    col = endIndexes[1];

                    int diff = endIndexes[0] - startIndexes[0];
                    if (diff == 2)
                        row = endIndexes[0] - 1;
                    else if (diff == -2)
                        row = endIndexes[0] + 1;
                }

                // be sure jump row and col are in range, otherwise set it as null
                __jumpBtn = (0 <= row && row < __board.length && 0 <= col && col < __board[row].length)
                        ? __board[row][col]
                        : null;
            }
        }

        public void setBoard(JButton[][] board) {
            __board = board;
            // be sure given buttons are still valid
            if (findLocation(__startBtn) == null)
                __startBtn = null;
            if (findLocation(__endBtn) == null)
                __startBtn = null;
            if (findLocation(__jumpBtn) == null)
                __jumpBtn = null;
        }

        public boolean setMovement (JButton start, Direction d) throws InvalidParameterException {
            try {
                setStart(start);    // can throw InvalidParameterException
                boolean r = false;
                if (start().getActionCommand().equals("P")) {
                   int[] indexes = findLocation(start); 
                   if (indexes != null) {
                        switch (d) {
                            case UP: 
                                r = setUpMovement(indexes[0], indexes[1]);
                                break;
                            case DOWN: 
                                r = setDownMovement(indexes[0], indexes[1]);
                                break;
                            case LEFT: 
                                r = setLeftMovement(indexes[0], indexes[1]);
                                break;
                            case RIGHT: 
                                r = setRightMovement(indexes[0], indexes[1]);
                                break;
                        }
                    }
                }
                return r;
            }
            catch (InvalidParameterException e) {
                System.err.printf("start JButton is invalid parameter for setting");
                throw e;
            }            
        }

        public boolean setRandomMovement () {
            Random rand = new Random();
            // choose an random starting position
            int row = rand.nextInt(__board.length);
            int col = rand.nextInt(__board[row].length);
    
            // start with selected position (row, col) and try each cell to make movement
            for (int i = 0; i < __board.length; ++i) {
                for (int j = 0; j < __board[i].length; ++j) {
                    // check movement
                    setStart(__board[row][col]);
                    if (    
                        setMovement(__board[row][col], Movement.Direction.RIGHT) ||
                        setMovement(__board[row][col], Movement.Direction.LEFT) ||
                        setMovement(__board[row][col], Movement.Direction.UP) ||
                        setMovement(__board[row][col], Movement.Direction.DOWN)
                    ) 
                        return true;
                    // iterate coloumn
                    col = (col == __board[row].length - 1) ? 0 : col + 1;
                }
                // iterate row
                row = (row == __board.length - 1) ? 0 : row + 1;
            }
            return false;
        }

        private boolean setUpMovement(int row, int col) {
            if (0 <= row - 2 && __board[row - 1][col].getActionCommand().equals("P") &&
                __board[row - 2][col].getActionCommand().equals(".")) {
                __jumpBtn = __board[row - 1][col];
                __endBtn = __board[row - 2][col];
                return true;
            } 
            return false;
        }

        private boolean setDownMovement(int row, int col) {
            if (row + 2 < __board.length && __board[row + 1][col].getActionCommand().equals("P") &&
                __board[row + 2][col].getActionCommand().equals(".")) {
                __jumpBtn = __board[row + 1][col];
                __endBtn = __board[row + 2][col];
                return true;
            } 
            return false;
        }

        private boolean setLeftMovement(int row, int col) {
            if (0 <= col - 2 && __board[row][col - 1].getActionCommand().equals("P") &&
                __board[row][col - 2].getActionCommand().equals(".")) {
                __jumpBtn = __board[row][col - 1];
                __endBtn = __board[row][col - 2];
                return true;
            }

            return false;
        }

        private boolean setRightMovement(int row, int col) {
            if (col + 2 < __board[col].length && __board[row][col + 1].getActionCommand().equals("P") &&
                __board[row][col + 2].getActionCommand().equals(".")) {
                __jumpBtn = __board[row][col + 1];
                __endBtn = __board[row][col + 2];
                return true;
            }
            return false;
        }

        public boolean isValidMovement() {
            setJump();
            // jump becomes null, if start and end buttons are not in proper position
            return jump() != null &&
                    __startBtn.getActionCommand().equals("P") &&
                    __jumpBtn.getActionCommand().equals("P") &&
                    __endBtn.getActionCommand().equals(".");
        }

        public int[] findLocation(JButton btn) throws NullPointerException {
            int indexes[] = null;
            if (__board != null && btn != null) {
                for (int i = 0; i < __board.length && indexes == null; ++i)
                    for (int j = 0; j < __board[i].length && indexes == null; ++j)
                        if (__board[i][j] == btn) {
                            indexes = new int[2];
                            indexes[0] = i; // assign row
                            indexes[1] = j; // assign col
                        }
            }
            return indexes;
        }

        public Movement clone() {
            try {
                Movement r = (Movement) super.clone();
                r.__board = __board;
                r.__startBtn = __startBtn;
                r.__endBtn = __endBtn;
                r.__jumpBtn = __jumpBtn;
                return r;
            } catch (CloneNotSupportedException e) {
                // this will never be happen
                return null;
            }
        }
    } // end of Movement Class
}

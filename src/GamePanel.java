import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
// import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

    public static enum BoardType {FRENCH, GERMAN, ASYMETRICAL, ENGLISH, DIAMOND, TRIANGULAR}

    // private JPanel __statusPanel;
    private JPanel __boardPanel;
    private JButton __gameBoard[][];

    private JPanel __controlPanel;
    private JButton __undoBtn;
    private JButton __homeBtn;
    
    private Movement __curMov; // keeps the current movement (necassary for undo movement)
    private Vector<JButton> __nextPossibleBtn;
    private Stack<Movement> __movements;
    private int __numOfMov;
    private int __numOfPeg;

    final private Color BG_MAIN_COLOR = new Color(28, 34, 38);
    final private Color BG_SEC_COLOR = new Color(143, 155, 166);
    final private Color HOVER_COLOR = new Color(0xE10550);
    final private Color FG_MAIN_COLOR = new Color(0xE10550);
    final private Color FG_SEC_COLOR = new Color(0x129468);

    public GamePanel (JButton homeButton) {
        setLayout(new BorderLayout());

        // getContentPane().setBackground(BG_MAIN_COLOR); //

        //! initialize the game board selected by user
        setGameBoard(BoardType.GERMAN);
        
        // set status panel (shows current game status such as numOfMov, numOfPeg)
       /*
        __statusPanel = new JPanel();
        __statusPanel.setLayout(new BorderLayout());
        __statusPanel.setPreferredSize(new Dimension(600, 50)); // 600 with & 100 height
        __statusPanel.setBackground(BG_MAIN_COLOR);
        add(__statusPanel, BorderLayout.NORTH);

        // set __textField as center of __statusPanel
        __textField = new JLabel();
        __textField.setBackground(BG_MAIN_COLOR);
        __textField.setForeground(FG_MAIN_COLOR);
        __textField.setHorizontalAlignment(JLabel.CENTER);
        __textField.setText(String.format("#peg: %d  #movements: 0", __numOfPeg, __numOfMov));
        __textField.setOpaque(true); // ??????????????????????
        __statusPanel.add(__textField, BorderLayout.CENTER);
    */
        
        // add undo button
        __undoBtn = new JButton();
        __undoBtn.setBackground(BG_MAIN_COLOR);
        // __undoBtn.setForeground(FG_MAIN_COLOR);
        // __undoBtn.setHorizontalTextPosition(JButton.CENTER);
        // __undoBtn.setVerticalTextPosition(JButton.BOTTOM);
        __undoBtn.setIcon(new ImageIcon("../img/undo.png"));

        // __undoBtn.setPreferredSize(new Dimension(50, 50));
        __undoBtn.addActionListener(this);
        __undoBtn.setEnabled(false); // initially not clickable
        
        // home button
        __homeBtn = homeButton; // homeButton given as parameter
        __homeBtn.setBackground(BG_MAIN_COLOR);
        __homeBtn.setIcon(new ImageIcon("../img/home.png"));
        // __homeBtn.addActionListener(this);
        
        // set control panel which keeps undo and home buttons 
        __controlPanel = new JPanel(new BorderLayout());
        add(__controlPanel, BorderLayout.NORTH);    // at the top of the super panel
        __controlPanel.setBackground(BG_MAIN_COLOR);
        __controlPanel.add(__undoBtn, BorderLayout.WEST);
        __controlPanel.add(__homeBtn, BorderLayout.EAST);
        
        // update the game frame
        // SwingUtilities.updateComponentTreeUI(this);  
    }

    public double score () {
        // max score is 100 (when 1 peg left)
        return (double) numOfPeg() / 100.0;
    }

    public int numOfPeg () {
        int n = 0;
        for (int i = 0; i < __gameBoard.length; ++i)
            for (int j = 0; j < __gameBoard[i].length; ++j)
                if (__gameBoard[i][j].getText().equals("P"))
                    ++n;
        return n;
    }

    /*
    private void setGameStatus () {
        if (__textField != null)
            __textField.setText(String.format("#peg: %d \t #movements: %d", __numOfPeg, __numOfMov));
    }
    */

    public void setGameBoard (BoardType t) {
        switch (t) {
            case FRENCH: 
                setFrenchBoard();break;
            case GERMAN: 
                setGermanBoard(); break;
            case ASYMETRICAL: break;
            case ENGLISH: break;
            case DIAMOND: break;
            case TRIANGULAR: break;
        }
        __numOfMov = 0;
        __numOfPeg = numOfPeg();
        // setGameStatus(); //!! NOT SURE
        // reset/init movement for new board
        __curMov = new Movement(__gameBoard);
        __movements = new Stack<Movement>();
    }

    private void setGermanBoard() {
        // set board Panel (keeps each buttons to represent cells of PegSolitaire)
        if (__boardPanel != null)
            remove(__boardPanel);
        __boardPanel = new JPanel();
        __boardPanel.setLayout(new GridLayout(9, 9)); 
        __boardPanel.setBackground(BG_MAIN_COLOR);
        add(__boardPanel);

        final String cellValue[][] = {
                { "", "", "", "P", "P", "P", "", "", "" },
                {"P", "P", "P", "P", "P", "P", "P", "P", "P"}
        };

        __gameBoard = new JButton[9][9];

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (3 <= i && i < 6) ? 1 : 0;

            for (int j = 0; j < __gameBoard[i].length; ++j) {
                __gameBoard[i][j] = new JButton();
                __gameBoard[i][j].setOpaque(true); // ????????????? is needed
                if (cellValue[col][j].equals("P")) {
                    __gameBoard[i][j].setText("P");
                    __gameBoard[i][j].setBackground(BG_MAIN_COLOR);
                    __gameBoard[i][j].setForeground(FG_MAIN_COLOR);
                    __gameBoard[i][j].addActionListener(this);
                } else {
                    // set non-clicable buttons (Walls)
                    __gameBoard[i][j].setBackground(BG_SEC_COLOR);
                    __gameBoard[i][j].setEnabled(false);
                }
                __boardPanel.add(__gameBoard[i][j]);
            }
        }
        __gameBoard[4][4].setText(" "); // center empty cell
    }

    private void setFrenchBoard () {
        if (__boardPanel != null)
            remove(__boardPanel);

        // set board Panel (keeps each buttons to represent cells of PegSolitaire)
        __boardPanel = new JPanel();
        __boardPanel.setLayout(new GridLayout(10, 10));
        __boardPanel.setBackground(BG_MAIN_COLOR);
        add(__boardPanel);

        __gameBoard = new JButton[10][10];

        for (int i = 0; i < __gameBoard.length; ++i) {
            for (int j = 0; j < __gameBoard[i].length; ++j) {
                __gameBoard[i][j] = new JButton();
                if (i < 4 && j < 4) {
                    __gameBoard[i][j].setText("P");
                    __gameBoard[i][j].setBackground(BG_MAIN_COLOR);
                    __gameBoard[i][j].setForeground(FG_MAIN_COLOR);
                    __gameBoard[i][j].addActionListener(this);
                }
                else {
                    __gameBoard[i][j].setBackground(BG_SEC_COLOR);
                    __gameBoard[i][j].setEnabled(false);
                }
                __boardPanel.add(__gameBoard[i][j]);
            }
        }
        __gameBoard[0][0].setText(" ");
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

        //! FOR TEST PURPOSE        
        int[] indexes = mov.findLocation(btn); 
        if (mov.setMovement(btn, Movement.Direction.DOWN))
            System.out.printf("[%d][%d]: D\n", indexes[0], indexes[1]);
        if (mov.setMovement(btn, Movement.Direction.UP))
            System.out.printf("[%d][%d]: U\n", indexes[0], indexes[1]);
        if (mov.setMovement(btn, Movement.Direction.LEFT))
            System.out.printf("[%d][%d]: L\n", indexes[0], indexes[1]);
        if (mov.setMovement(btn, Movement.Direction.RIGHT))
            System.out.printf("[%d][%d]: R\n", indexes[0], indexes[1]);
        //! FOR TEST PURPOSE        

        return  mov.setMovement(btn, Movement.Direction.UP) ||
                    mov.setMovement(btn, Movement.Direction.DOWN) ||
                    mov.setMovement(btn, Movement.Direction.RIGHT) ||
                    mov.setMovement(btn, Movement.Direction.LEFT);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        JButton selectedBtn = (JButton) e.getSource();
        if (selectedBtn == __undoBtn) 
            undo();
        else if (__curMov.start() == null) {
        // ignore selection of the cell which are Wall("") or Empty(" ") cells
        if (selectedBtn.getText().equals("P")) {
                __curMov.setStart(selectedBtn);
                
                selectedBtn.setForeground(FG_SEC_COLOR); // set hover effect on selected button
                __nextPossibleBtn = __curMov.nextPossibleMov();
                if (__nextPossibleBtn != null)
                for (var btn : __nextPossibleBtn)
                btn.setBackground(HOVER_COLOR); // set hover effect on selected button
            }
        }
        // if start button was selected, current selected button should be end button
        else if (selectedBtn != __curMov.start()) {
            selectedBtn.setForeground(FG_SEC_COLOR); // set hover effect on selected button
            __curMov.setEnd(selectedBtn);
            // apply movement
            if (move(__curMov)) {
                if (isGameOver()) 
                    JOptionPane.showMessageDialog(this, String.format(
                                "      Game is over\n" + 
                                "Number of Movement: %d\n" + 
                                "   Remaining Peg: %d", 
                                __numOfMov, __numOfPeg
                    ), "Game is Over", JOptionPane.INFORMATION_MESSAGE);    
            }
            // else JOptionPane.showMessageDialog(null, "Illegal movement", "Error", JOptionPane.ERROR_MESSAGE);

            // set selected buttons background color as default
            __curMov.start().setForeground(FG_MAIN_COLOR);
            __curMov.end().setForeground(FG_MAIN_COLOR);
            // set current Movement as null for next movement
            __curMov.setStart(null);
            __curMov.setEnd(null);

            if (__nextPossibleBtn != null)
                for (var btn : __nextPossibleBtn)
                    btn.setBackground(BG_MAIN_COLOR); // set hover effect on selected button
        }
    }

    public boolean move (Movement mov) {
        if (mov.isValidMovement()) {
            mov.start().setText(" ");
            mov.jump().setText(" ");
            mov.end().setText("P");
            ++__numOfMov;
            --__numOfPeg;
            // add the current movement to the movements stack (copy of it!)
            __movements.push(mov.clone());  
            if (!__undoBtn.isEnabled())
                __undoBtn.setEnabled(true);
            return true;
        } else
            return false;
    }

    public boolean moveRandom () {
        Random rand = new Random();
        // choose an random starting position
        int row = rand.nextInt(__gameBoard.length);
        int col = rand.nextInt(__gameBoard[row].length);

        // start with selected position (row, col) and try each cell to make movement
        for (int i = 0; i < __gameBoard.length; ++i) {
            for (int j = 0; j < __gameBoard[i].length; ++j) {
                // check movement
                __curMov.setStart(__gameBoard[row][col]);
                if (    
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.RIGHT) ||
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.LEFT) ||
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.UP) ||
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.DOWN)
                    ) {
                    move(__curMov);
                    return true;
                }
                // iterate coloumn
                col = (col == __gameBoard[row].length - 1) ? 0 : col + 1;
            }
            // iterate row
            row = (row == __gameBoard.length - 1) ? 0 : row + 1;
        }
        return false;
    }

    public boolean undo() {
        // if (__curMov.start() != null && __curMov.jump() != null && __curMov.end() // != null) {
        // if there is a valid movement made before, apply reverse of it
        if (__undoBtn.isEnabled()) {
            Movement lastMov = __movements.pop();
            lastMov.start().setText("P");
            lastMov.jump().setText("P");
            lastMov.end().setText(" ");
            --__numOfMov;
            ++__numOfPeg;
            
            if (__movements.size() == 0)
                __undoBtn.setEnabled(false);
            return true;
        }
        return false;
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
            if (start() != null) {
                for (Direction d : Direction.values())
                    if (setMovement(start(), d))
                        v.add(end());
            }
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
                if (start().getText().equals("P")) {
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

        private boolean setUpMovement(int row, int col) {
            if (0 <= row - 2 && __board[row - 1][col].getText().equals("P")
                    && __board[row - 2][col].getText().equals(" ")) {
                __jumpBtn = __board[row - 1][col];
                __endBtn = __board[row - 2][col];
                return true;
            } 
            return false;
        }

        private boolean setDownMovement(int row, int col) {
            if (row + 2 < __board.length && __board[row + 1][col].getText().equals("P")
                    && __board[row + 2][col].getText().equals(" ")) {
                __jumpBtn = __board[row + 1][col];
                __endBtn = __board[row + 2][col];
                return true;
            } 
            return false;
        }

        private boolean setLeftMovement(int row, int col) {
            if (0 <= col - 2 && __board[row][col - 1].getText().equals("P")
                    && __board[row][col - 2].getText().equals(" ")) {
                __jumpBtn = __board[row][col - 1];
                __endBtn = __board[row][col - 2];
                return true;
            }

            return false;
        }

        private boolean setRightMovement(int row, int col) {
            if (col + 2 < __board[col].length && __board[row][col + 1].getText().equals("P")
                    && __board[row][col + 2].getText().equals(" ")) {
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
                    __startBtn.getText().equals("P") &&
                    __jumpBtn.getText().equals("P") &&
                    __endBtn.getText().equals(" ");
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

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
// import javax.swing.border.Border;
import javax.swing.SwingUtilities;

public class PegSolitaire extends JFrame {    
    private JPanel __statusPanel;
    private JPanel __boardPanel;
    private JLabel __textField;
    private JButton __gameBoard[][];
    private JButton __undoBtn;
    private JButton __resetBtn; //! NOT IMPLAMENTED YET
    private Movement __curMove;   // keeps the current movement (necassary for undo movement)
    private Movement __lastMove;
    private int __numOfMov;
    private int __score;
    final private Color BG_MAIN_COLOR = new Color(28, 34, 38);
    final private Color BG_SEC_COLOR = new Color(143, 155, 166);
    final private Color HOVER_COLOR = new Color(0xF42181);
    final private Color FG_COLOR = new Color(217, 143, 7);
    private EventHandler handler;


    public PegSolitaire () {
        super("Peg Solitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());
        setVisible(true);
        
        setIconImage(new ImageIcon("../img/peg.png").getImage());
        handler = new EventHandler();

        getContentPane().setBackground(BG_MAIN_COLOR);   //???????????????????????????????

        // set board Panel (keeps each buttons to represent cells of PegSolitaire)
        __boardPanel = new JPanel();
        //! setBoard function shoul implement these
        __boardPanel.setLayout(new GridLayout(9, 9)); //!!!!!!
        __boardPanel.setBackground(BG_MAIN_COLOR);
        add(__boardPanel);
        //! Select the board type

        // initialize the game board with related informations (init __score, __numOfMov) 
        SetGermanBoard();
        // initialize movement by giving current game board (__gameBoard)
        __lastMove = null;
        __curMove = new Movement(__gameBoard); 
        
        // set status panel (shows current game status such as __score, __numOfMov)
        __statusPanel = new JPanel();
        __statusPanel.setLayout(new BorderLayout());
        __statusPanel.setPreferredSize(new Dimension(600, 50));    // 600 with & 100 height
        __statusPanel.setBackground(BG_MAIN_COLOR);
        add(__statusPanel, BorderLayout.NORTH);

        // set __textField as center of __statusPanel
        __textField = new JLabel();
        __textField.setBackground(BG_MAIN_COLOR);
        __textField.setForeground(FG_COLOR);
        __textField.setHorizontalAlignment(JLabel.CENTER);
        __textField.setText(String.format("Score: %d  #movements: 0", __score));
        __textField.setOpaque(true);    //??????????????????????
        __statusPanel.add(__textField, BorderLayout.CENTER);
    
        // add undo button
        ImageIcon undoIcon = new ImageIcon("../img/undo.png");
        __undoBtn = new JButton();
        __undoBtn.setBackground(BG_SEC_COLOR);
        // __undoBtn.setForeground(FG_COLOR);
        // __undoBtn.setHorizontalTextPosition(JButton.CENTER);
        // __undoBtn.setVerticalTextPosition(JButton.BOTTOM);
        __undoBtn.setIcon(undoIcon);

        // __undoBtn.setPreferredSize(new Dimension(50, 50));
        __undoBtn.addActionListener(handler);
        __undoBtn.setEnabled(false);    // initially not clickable
        __statusPanel.add(__undoBtn, BorderLayout.WEST);

        setGameStatus(score(), 0);
        // update the game frame
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void setGameStatus (int score, int numOfMov) {
        __score = score;
        __numOfMov = numOfMov;
        if (__textField != null)
            __textField.setText(String.format("remaining peg: %d \t #movements: %d", __score, __numOfMov));
    }

    public int score () {
        int score = 0;
        for (int i = 0; i < __gameBoard.length; ++i)
            for (int j = 0; j < __gameBoard[i].length; ++j)
                if (__gameBoard[i][j].getText().equals("P"))
                    ++score;
        return score;
    }

    public void SetGermanBoard () {
        final String cellValue[][] = { 
            {"" , "", "", "P", "P", "P", "", "", ""}, 
            {"P", "P", "P", "P", "P", "P", "P", "P", "P"}};
        
        __gameBoard = new JButton[9][9];

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (3 <= i && i < 6) ? 1 : 0;
            
            for (int j = 0; j < __gameBoard[i].length; ++j) {
                __gameBoard[i][j] = new JButton();
                __gameBoard[i][j].addActionListener(handler);
                __gameBoard[i][j].setOpaque(true);  //????????????? is needed
                if (cellValue[col][j].equals("P")) {
                    __gameBoard[i][j].setText("P");
                    __gameBoard[i][j].setBackground(BG_MAIN_COLOR);
                    __gameBoard[i][j].setForeground(FG_COLOR);
                }
                else {
                    // set non-clicable buttons (Walls)
                    __gameBoard[i][j].setBackground(BG_SEC_COLOR);
                    __gameBoard[i][j].setEnabled(false);    
                }
                __boardPanel.add(__gameBoard[i][j]);
            }
        }
        __gameBoard[4][4].setText(" "); // center empty cell
    }   

    public boolean canMakeMovement (JButton btn) {
        Movement mov = new Movement (__gameBoard, btn);
        return  mov.setDownMovement() || 
                mov.setUpMovement() ||
                mov.setLeftMovement() ||
                mov.setRightMovement();
    }

    public boolean isGameOver () {
        for (int i = 0; i < __gameBoard.length; ++i)    
            for (var btn : __gameBoard[i])
                if (canMakeMovement(btn)) 
                    return false;
        return true;
    }

    private class EventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // // ignore selection of the cell which are Wall("") or Empty(" ") cells
            JButton selectedBtn = (JButton) e.getSource();
            if (selectedBtn == __undoBtn)
                undo();
            else if (__curMove.start() == null) {
                __curMove.setStart(selectedBtn);
                selectedBtn.setForeground(HOVER_COLOR); // set hover effect on selected button
            }
            // if start button was selected, current selected button should be end button
            else if (selectedBtn != __curMove.start()) {
                selectedBtn.setForeground(HOVER_COLOR); // set hover effect on selected button
                __curMove.setEnd(selectedBtn);
                // apply movement
                if (move(__curMove)) {
                    //! is it true to be place here 
                    // check if game is over after each legal movement 
                    if (isGameOver()) 
                        JOptionPane.showMessageDialog(null, String.format("Game is over. Your score: %d", __score));
                }
                /*
                else
                    JOptionPane.showMessageDialog(null, "Illegal movement", "Error", JOptionPane.ERROR_MESSAGE);
                */

                // set selected buttons background color as default
                __curMove.start().setForeground(FG_COLOR);
                __curMove.end().setForeground(FG_COLOR);
                // set current Movement as null for next movement
                __curMove.setStart(null);        
                __curMove.setEnd(null);        
            }
        }
    }

    public boolean move (Movement mov) {
        if (mov.isValidMovement()) {
            mov.start().setText(" ");
            mov.jump().setText(" ");
            mov.end().setText("P");
            setGameStatus(__score - 1, __numOfMov + 1);
            
            // set last movement as given validated movement
            __lastMove = mov.clone();
            if (!__undoBtn.isEnabled())
                __undoBtn.setEnabled(true);
            return true;
        }
        else 
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
                __curMove.setStart(__gameBoard[row][col]);
                if (__curMove.setRightMovement() || 
                    __curMove.setLeftMovement() ||
                    __curMove.setUpMovement() || 
                    __curMove.setDownMovement()) {
                        move(__curMove);
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

    public boolean undo () {
        // if existing movement is valid, apply reverse of it
        // if (__curMove.start() != null && __curMove.jump() != null && __curMove.end() != null) {
        if (__undoBtn.isEnabled()) {
            __lastMove.start().setText("P");
            __lastMove.jump().setText("P");
            __lastMove.end().setText(" ");
            setGameStatus(__score + 1, __numOfMov - 1);
            __undoBtn.setEnabled(false);

            // undo is permitted for just one step, 
            // so after undo there should be no movement left 
            // lastMove = null
            return true;
        }
        return false;
    }    

    public static class Movement implements Cloneable {
        private JButton[][] __board;    // game board for checking validty of movement
        private JButton __startBtn;     // start position of movement
        private JButton __jumpBtn;      // jump position of movement (between start and end)
        private JButton __endBtn;       // end position of movement

        public Movement (JButton[][] board, JButton start, JButton end) {
            __board = board;
            try {
                setStart(start);
                setEnd(end);
            }
            catch (InvalidParameterException e) {
                __startBtn = __endBtn = __jumpBtn = null;
                System.err.println("Invalid parameter for constructor");
            }
        }

        public Movement (JButton[][] board, JButton start) {this(board, start, null);}

        public Movement (JButton[][] board) {this(board, null, null);}
        
        public Movement () {this(null, null, null);}

        public JButton start () {return  __startBtn;}
        public JButton end () {return __endBtn;}
        public JButton jump () {return __jumpBtn;}

        public void setStart (JButton start) throws InvalidParameterException {
            // be sure given JButton is in the current game board
            if (start != null && findLocation(start) == null)
                throw new InvalidParameterException("given JButtons not exist in game board");
            __startBtn = start;
        }

        public void setEnd (JButton end) {
            // be sure given JButton is in the current game board
            if (end != null && findLocation(end) == null)
                throw new InvalidParameterException("given JButtons not exist in game board");
            __endBtn = end;
        }

        public void setJump () throws InvalidParameterException {
            if (__board == null || start() == null || end() == null)
                throw new NullPointerException("no enough information to find jump button");
    
            int[] startIndexes = findLocation(start());
            int[] endIndexes = findLocation(end());
            
            if (startIndexes != null && endIndexes != null) {
                int row = -1;    // jump button row
                int col = -1;    // jump button coloumn

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
                __jumpBtn = (0 <= row && row < __board.length && 0 <= col && col < __board[row].length) ?
                    __board[row][col] : null;
            }
        }

        public void setBoard (JButton[][] board) {
            __board = board;
            // reset the movement positions
            // __startBtn = __jumpBtn = __endBtn = null;
        } 

        public boolean setUpMovement () throws NullPointerException {
            int[] indexes = findLocation(__startBtn);
            if (indexes == null)
                throw new NullPointerException("null start position for movement");

            int row = indexes[0];
            int col = indexes[1];
            if (0 <= row - 2 && __board[row - 1][col].getText().equals("P") && __board[row - 2][col].getText().equals(" ")) {                
                __jumpBtn = __board[row - 1][col];
                __endBtn = __board[row - 2][col];
                return true;
            }
            else
                return false;
        }

        public boolean setDownMovement () throws NullPointerException {
            int[] indexes = findLocation(__startBtn);
            if (indexes == null)
                throw new NullPointerException("null start position for movement");

            int row = indexes[0];
            int col = indexes[1];
            if (row + 2 < __board.length && __board[row + 1][col].getText().equals("P") && __board[row + 2][col].getText().equals(" ")) {
                __jumpBtn = __board[row + 1][col];
                __endBtn = __board[row + 2][col];
                return true;
            }
            else 
                return false;
        }

        public boolean setLeftMovement () throws NullPointerException {
            int[] indexes = findLocation(__startBtn);
            if (indexes == null)
                throw new NullPointerException("null start position for movement");

            int row = indexes[0];
            int col = indexes[1];
            if (0 <= col - 2 && __board[row][col - 1].getText().equals("P") && __board[row][col - 2].getText().equals(" ")) {
                __jumpBtn = __board[row][col - 1];
                __endBtn = __board[row][col - 2];
                return true;
            }
            else 
                return false;
        }

        public boolean setRightMovement () throws NullPointerException {
            int[] indexes = findLocation(__startBtn);
            if (indexes == null)
                throw new NullPointerException("null start position for movement");

            int row = indexes[0];
            int col = indexes[1];
            if (col + 2 < __board[col].length && __board[row][col + 1].getText().equals("P") && __board[row][col + 2].getText().equals(" ")) {
                __jumpBtn = __board[row][col + 1];
                __endBtn = __board[row][col + 2];
                return true;
            }
            else 
                return false;
        }

        public boolean isValidMovement () {
            setJump();
            // jump becomes null, if start and end buttons are not in proper position
            return  jump() != null &&
                    __startBtn.getText().equals("P") && 
                    __jumpBtn.getText().equals("P") && 
                    __endBtn.getText().equals(" ");
        }

        public int[] findLocation (JButton btn) throws NullPointerException {
            if (__board == null || btn == null)
                throw new NullPointerException("null parameter");
            
            int indexes[] = null;

            for (int i = 0; i < __board.length && indexes == null; ++i) 
                for (int j = 0; j < __board[i].length && indexes == null; ++j)
                    if (__board[i][j] == btn) {
                        indexes = new int[2];
                        indexes[0] = i; // assign row
                        indexes[1] = j; // assign col
                    }
            return indexes;
        }

        public Movement clone () {
            try {
                Movement r = (Movement) super.clone();
                r.__board = __board;
                r.__startBtn = __startBtn;
                r.__endBtn = __endBtn;
                r.__jumpBtn = __jumpBtn;
                return r;
            }
            catch (CloneNotSupportedException e) {
                // this will never be happen
                return null;
            }
        }
    } // end of Movement Class 
}

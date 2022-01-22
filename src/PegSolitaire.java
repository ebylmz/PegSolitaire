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
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class PegSolitaire extends JFrame implements ActionListener {    
    private JPanel __statusPanel;
    private JPanel __boardPanel;
    private JLabel __textField;
    private JButton __cellButtons[][];
    private JButton __undoBtn;
    private JButton __startBtn;
    private JButton __endBtn;
    private Movement __mov;   // keeps the current movement (necassary for undo movement)
    private int __numOfMov;
    private int __score;
    private Color __bgMainColor;
    private Color __bgSecColor;
    private Color __bgHoverColor;
    private Color __fgColor;

    public PegSolitaire () {
        super("Peg Solitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLayout(new BorderLayout());
        setVisible(true);

        __bgMainColor = new Color(28, 34, 38);
        __bgSecColor = new Color(143, 155, 166);
        __fgColor = new Color(217, 143, 7);
        __bgHoverColor = new Color(0, 63, 99);
        getContentPane().setBackground(__bgMainColor);   //???????????????????????????????

        // set board Panel (keeps each buttons to represent cells of PegSolitaire)
        __boardPanel = new JPanel();
        //! setBoard function shoul implement these
        __boardPanel.setLayout(new GridLayout(9, 9)); //!!!!!!
        __boardPanel.setBackground(__bgMainColor);
        add(__boardPanel);
        //! Select the board type

        // initialize the game board with related informations (init __score, __numOfMov) 
        SetGermanBoard();
        setScore();
        __numOfMov = 0;
        // initialize movement by giving current game board (__cellButtons)
        __mov = new Movement(__cellButtons); 
        __startBtn = __endBtn = null;
        
        // set status panel (shows current game status such as __score, __numOfMov)
        __statusPanel = new JPanel();
        __statusPanel.setLayout(new BorderLayout());
        __statusPanel.setPreferredSize(new Dimension(600, 100));    // 600 with & 100 height
        add(__statusPanel, BorderLayout.NORTH);

        // set __textField as center of __statusPanel
        __textField = new JLabel();
        __textField.setBackground(__bgMainColor);
        __textField.setForeground(__fgColor);
        __textField.setHorizontalAlignment(JLabel.CENTER);
        __textField.setText(String.format("Score: %d \t #movements: 0", __score));
        __textField.setOpaque(true);
        __statusPanel.add(__textField, BorderLayout.EAST);
    
        // add undo button
        __undoBtn = new JButton("undo");
        // __undoBtn.setPreferredSize(new Dimension(50, 50));
        __undoBtn.addActionListener(this);
        __statusPanel.add(__undoBtn, BorderLayout.WEST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton selectedBtn = (JButton) e.getSource();
        // ignore selection of the cell which are Wall("") or Empty(" ") cells
        if (selectedBtn == __undoBtn) {
            System.out.println("fsdlfnksdfndsklgnfdklgn");
            __mov.undoMovement();
        }
        else if (! selectedBtn.getText().equals("")) {
            // if start button is selected, make movement with end buttons
            if (__startBtn == null) {
                __startBtn = selectedBtn;
                // set hover effect to selected btn)
                __startBtn.setBackground(__bgHoverColor);
            }
            else if (e.getSource() != __startBtn) {
                // set and apply movement
                __endBtn = selectedBtn;
                __startBtn.setBackground(__bgHoverColor);
                __mov.setPosition(__startBtn, __endBtn);
                if (__mov.applyMovement()) {
                    ++__numOfMov; // new movement made
                    --__score;    // one peg removed, update the game __score
                    __textField.setText(String.format("Score: %d \t #movements: %d", __score, __numOfMov));
                }
                else
                    JOptionPane.showMessageDialog(this, "Illegal movement", "Error", JOptionPane.ERROR_MESSAGE);
    
                // set button backgroundColor as default
                __startBtn.setBackground(__bgMainColor);
                __endBtn.setBackground(__bgMainColor);
    
                // set movement references as null for next movement                       
                __startBtn = __endBtn = null;   
    
                //! is it true to be place here 
                // Check if game is over 
                if (isGameOver()) 
                    JOptionPane.showMessageDialog(
                        this, String.format("Game is over. Your __score: %d", __score));
            }
        }
    }

    public void SetGermanBoard () {
        final String cellValue[][] = { 
            {"", "", "", "P", "P", "P", "", "", ""}, 
            {"P", "P", "P", "P", "P", "P", "P", "P", "P"}};
        
        __cellButtons = new JButton[9][9];

        for (int i = 0; i < __cellButtons.length; ++i) {
            int col = (3 <= i && i < 6) ? 1 : 0;

            for (int j = 0; j < __cellButtons[i].length; ++j) {
                __cellButtons[i][j] = new JButton(cellValue[col][j]);
                __cellButtons[i][j].addActionListener(this);
                __cellButtons[i][j].setOpaque(true);  //????????????? is needed
                if (cellValue[col][j].equals("P")) {
                    __cellButtons[i][j].setBackground(__bgMainColor);
                    __cellButtons[i][j].setForeground(__fgColor);
                }
                else {
                    __cellButtons[i][j].setBackground(__bgSecColor);
                }
                __boardPanel.add(__cellButtons[i][j]);
            }
        }
        __cellButtons[4][4].setText(" "); // center empty cell
    }   

    public boolean isGameOver () {
        for (int i = 0; i < __cellButtons.length; ++i)    
            for (int j = 0; j < __cellButtons[i].length; ++j)
                if (__mov.isMovable(__cellButtons[i][j]))   //!!!!!!!!!!!!
                    return false;
        return true;
    }

    public void setScore () {
        __score = 0;
        for (int i = 0; i < __cellButtons.length; ++i)
            for (int j = 0; j < __cellButtons[i].length; ++j)
                if (__cellButtons[i][j].getText().equals("P"))
                    ++__score;
    }
    

    public static class Movement {
        private JButton[][] __board;
        private JButton __startBtn;
        private JButton __jumpBtn;
        private JButton __endBtn;

        public Movement (JButton[][] board, JButton start, JButton end) {
            __board = board;
            __startBtn = start;
            __endBtn = end;
            __jumpBtn = null;

        }

        public Movement (JButton[][] board, JButton start) {this(board, start, null);}

        public Movement (JButton[][] board) {this(board, null, null);}
        
        public Movement () {this(null, null, null);}

        public JButton start () {return  __startBtn;}
        public JButton end () {return __endBtn;}
        public JButton jump () {return __jumpBtn;}
        public JButton[][] board () {return __board;}

        public void setPosition (JButton start, JButton end) {
            // be sure given JButtons are in current board
            if (findLocation(start) != null && (end == null || findLocation(end) != null)) {
                __startBtn = start;
                __endBtn = end;
                // __jumpBtn is depend on start and end btn so set as null 
                __jumpBtn = null;  // !!!
            }
            else 
                throw new InvalidParameterException("given JButtons not exist in current board");
        }

        public void setPosition (JButton start) throws InvalidParameterException {setPosition(start, null);}

        public void setBoard (JButton[][] board) {
            __board = board;
            // reset the movement positions
            __startBtn = __jumpBtn = __endBtn = null;
        }

        public boolean undoMovement () {
            // if existing movement is valid, apply revere of it
            if (isValidMovement()) {
                __startBtn.setText("P");
                __jumpBtn.setText("P");
                __endBtn.setText(" ");
                return true;
            }
            else 
                return false;
        }

        public boolean isMovable (JButton start) {
            setPosition(start); //!!!
            return isMovable();
        }

        public boolean isMovable () throws NullPointerException {
            if (start() == null)
                throw new NullPointerException("null start referance");

            boolean r = false;

            if (start().getText().equals("P")) {
                int[] indexes = findLocation(start());
                //! it would be better to check if __startBtn different than null in <Direction>Movement
                if (indexes != null)
                    return  rightMovement(__startBtn, __jumpBtn, __endBtn)  ||
                            leftMovement(__startBtn, __jumpBtn, __endBtn) ||
                            upMovement(__startBtn, __jumpBtn, __endBtn) ||
                            downMovement(__startBtn, __jumpBtn, __endBtn);
            }
            return r;
        }

        public boolean applyMovement () {
            if (isValidMovement()) {
                start().setText(" ");
                jump().setText(" ");
                end().setText("P");
                return true;
            }
            else 
                return false;
        }

        public boolean applyRandomMovement () {
            Random rand = new Random();
            // choose an random starting position
            int row = rand.nextInt(__board.length);
            int col = rand.nextInt(__board[row].length);

            // start with selected position (row, col) and try each cell to make movement
            for (int i = 0; i < __board.length; ++i) {
                for (int j = 0; j < __board[i].length; ++j) {
                    // check movement
                    setPosition(__board[row][col]);
                    if (rightMovement(__startBtn, __jumpBtn, __endBtn) || leftMovement(__startBtn, __jumpBtn, __endBtn) ||
                        upMovement(__startBtn, __jumpBtn, __endBtn) || downMovement(__startBtn, __jumpBtn, __endBtn)) {
                            applyMovement();
                            return true;
                    }
                    // iterate coloumn
                    col = (col == __board[row].length - 1) ? 0 : col + 1;
                }
                // iterate row
                row = (row == __board.length - 1) ? 0 : row + 1;
            }
            return false;
        }

        public boolean upMovement (JButton start, JButton jump, JButton end) throws InvalidParameterException {
            int[] indexes = findLocation(start);
            if (indexes == null)
                throw new InvalidParameterException("start either null or not in the board");

            int row = indexes[0];
            int col = indexes[1];
            if (0 <= row - 2 && __board[row - 1][col].getText().equals("P") && __board[row - 2][col].getText().equals(" ")) {                
                jump = __board[row - 1][col];
                end = __board[row - 2][col];
                return true;
            }
            else
                return false;
        }

        public boolean downMovement (JButton start, JButton jump, JButton end) throws InvalidParameterException {
            int[] indexes = findLocation(start);
            if (indexes == null)
                throw new InvalidParameterException("start either null or not in the board");

            int row = indexes[0];
            int col = indexes[1];
            if (row + 2 < __board.length && __board[row + 1][col].getText().equals("P") && __board[row + 2][col].getText().equals(" ")) {
                jump = __board[row + 1][col];
                end = __board[row + 2][col];
                return true;
            }
            else 
                return false;
        }

        public boolean leftMovement (JButton start, JButton jump, JButton end) throws InvalidParameterException {
            int[] indexes = findLocation(start);
            if (indexes == null)
                throw new InvalidParameterException("start either null or not in the board");

            int row = indexes[0];
            int col = indexes[1];
            if (0 <= col - 2 && __board[row][col - 1].getText().equals("P") && __board[row][col - 2].getText().equals(" ")) {
                jump = __board[row][col - 1];
                end = __board[row][col - 2];
                return true;
            }
            else 
                return false;
        }

        public boolean rightMovement (JButton start, JButton jump, JButton end) throws InvalidParameterException {
            int[] indexes = findLocation(start);
            if (indexes == null)
                throw new InvalidParameterException("start either null or not in the board");

            int row = indexes[0];
            int col = indexes[1];
            if (col + 2 < __board[col].length && __board[row][col + 1].getText().equals("P") && __board[row][col + 2].getText().equals(" ")) {
                jump = __board[row][col + 1];
                end = __board[row][col + 2];
                return true;
            }
            else 
                return false;
        }

        public boolean isValidMovement () {
            // find and assign the __jumpBtn if it's legal
            if (__jumpBtn == null) {
                int[] indexes = findLocation(__startBtn, __endBtn); 
                // jump indexes cannot create becaue of inproper start and end positions
                if (indexes == null)
                    return false;
                __jumpBtn = __board[indexes[0]][indexes[1]];
            }

            return  __startBtn.getText().equals("P") && 
                    __jumpBtn.getText().equals("P") && 
                    __endBtn.getText().equals(" ");
        }

        public int[] findLocation (JButton btn) throws NullPointerException {
            if (board() == null || btn == null)
                throw new NullPointerException("null parameter");
            
            int indexes[] = null;

            for (int i = 0; i < __board.length && indexes == null; ++i) 
                for (int j = 0; j < __board[i].length && indexes == null; ++j)
                    if (__board[i][j].equals(btn)) {
                        indexes = new int[2];
                        indexes[0] = i; // assign row
                        indexes[1] = j; // assign col
                    }
            return indexes;
        }

        // returns jump indexes of movement
        public int[] findLocation (JButton start, JButton end) throws NullPointerException {
            if (board() == null || start == null || end == null)
                throw new NullPointerException("null game board");

            int[] startIndexes = findLocation(__startBtn);
            int[] endIndexes = findLocation(__endBtn);
            int[] jumpIndexes = null;
            int row = -1;    // jump button row
            int col = -1;    // jump button coloumn

            if (startIndexes != null && endIndexes != null) {
                // starBtn and __endBtn are at same row
                if (startIndexes[0] == endIndexes[0]) {
                    row = endIndexes[0];
                    
                    int diff = endIndexes[1] - startIndexes[1];
                    if (diff == 2)
                        col = endIndexes[1] - 1;
                    else if (diff == -2)
                        col = endIndexes[1] + 1;
                }
                // starBtn and __endBtn are at same coloumn
                else if (startIndexes[1] == endIndexes[1]) {
                    col = endIndexes[1];
                    
                    int diff = endIndexes[0] - startIndexes[0];
                    if (diff == 2)
                        row = endIndexes[0] - 1;
                    else if (diff == -2)
                        row = endIndexes[0] + 1;
                }
                
                // check if jump indexes in range 
                if (0 <= row && row < __board.length && 0 <= col && col < __board[row].length) {
                    jumpIndexes = new int[2];
                    jumpIndexes[0] = row;
                    jumpIndexes[1] = col;
                }
            }
            return jumpIndexes;
        }
    } // end of Movement Class 
}

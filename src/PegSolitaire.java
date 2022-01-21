import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PegSolitaire extends JFrame implements ActionListener {
    private JPanel statusPanel;
    private JPanel boardPanel;
    private JLabel textField;
    private  JButton cellButtons[][];
    private JButton __startBtn;
    private JButton __endBtn;
    private Movement mov;   // keeps the movement for undo movement
    private int numOfMov;
    private int score;

    public PegSolitaire () {
        super("Peg Solitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(50, 50, 50));   //???????????????????????????????
        setVisible(true);

        // set board Panel (keeps each buttons to represent cells of PegSolitaire)
        boardPanel = new JPanel();
        //! setBoard function shoul implement these
        boardPanel.setLayout(new GridLayout(9, 9)); //!!!!!!
        boardPanel.setBackground(new Color(0, 0, 0));
        add(boardPanel);
        //! Select the board type
        // initialize the game board with related informations (init score, numOfMov) 
        SetGermanBoard();
        setScore();
        numOfMov = 0;
        // set movement information as null
        mov = new Movement(); 
        __startBtn = __endBtn = null;
        
        // set status panel (shows current game status such as score, numOfMov)
        statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBounds(0, 600, 800, 200); //!!!!!!!!!1
        add(statusPanel, BorderLayout.SOUTH);   // add statusPanel(JPanel) to JFrame

        // set textField as center of statusPanel
        textField = new JLabel();
        textField.setBackground(new Color(25, 25, 25));
        textField.setForeground(new Color(25, 255, 0));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText(String.format("Score: %d \t #movements: 0", score));
        textField.setOpaque(true);
        statusPanel.add(textField);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // if start button is selected, make movement with end buttons
        if (__startBtn == null) {
            __startBtn = (JButton) e.getSource();
            // set selected effect (hover)
            __startBtn.setBackground(new Color(90, 90, 90));
        }
        else if (e.getSource() != __startBtn) {
            // set and apply movement
            __endBtn = (JButton) e.getSource();
            mov.set(__startBtn, __endBtn);
            if (mov.applyMovement()) {
                ++numOfMov; // new movement made
                --score;    // one peg removed, update the game score
                textField.setText(String.format("Score: %d \t #movements: %d", score, numOfMov));
            }
            else
                JOptionPane.showMessageDialog(this, "Illegal movement", "Error", JOptionPane.ERROR_MESSAGE);

            // set button backgroundColor as default
            __startBtn.setBackground(new Color(20, 20, 20));
            __endBtn.setBackground(new Color(20, 20, 20));

            // set movement references as null for next movement                       
            __startBtn = __endBtn = null;   

            //! is it true to be place here 
            // Check if game is over 
            if (isGameOver()) 
                JOptionPane.showMessageDialog(
                    this, String.format("Game is over. Your score: %d", score));
        }
    }

    public void SetGermanBoard () {
        final String cellValue[][] = { 
            {"", "", "", "P", "P", "P", "", "", ""}, 
            {"P", "P", "P", "P", "P", "P", "P", "P", "P"}};
        
        cellButtons = new JButton[9][9];

        for (int i = 0; i < cellButtons.length; ++i) {
            int col = (3 <= i && i < 6) ? 1 : 0;
                
            for (int j = 0; j < cellButtons[i].length; ++j) {
                cellButtons[i][j] = new JButton(cellValue[col][j]);
                cellButtons[i][j].addActionListener(this);
                cellButtons[i][j].setOpaque(true);  //????????????? is needed
                cellButtons[i][j].setBackground(new Color(20, 20, 20));
                cellButtons[i][j].setForeground(new Color(255, 255, 255));
                boardPanel.add(cellButtons[i][j]);
            }
        }
        cellButtons[4][4].setText(" "); // center empty cell
    }   

    public boolean isGameOver () {
        for (int i = 0; i < cellButtons.length; ++i)    
            for (int j = 0; j < cellButtons[i].length; ++j)
                if (mov.isMovable(cellButtons[i][j]))   ///!!!!!!!!!!!!
                    return false;
        return true;
    }

    public void setScore () {
        score = 0;
        for (int i = 0; i < cellButtons.length; ++i)
            for (int j = 0; j < cellButtons[i].length; ++j)
                if (cellButtons[i][j].getText().equals("P"))
                    ++score;
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

        public void set (JButton start, JButton end) {
            // __jumpBtn is depend on start and end btn so set as null 
            __startBtn = start;
            __endBtn = end;
            __jumpBtn = null; 
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
            __startBtn = start;
            return isMovable();
        }

        public boolean isMovable () {
            if (__startBtn != null && __startBtn.getText().equals("P")) {
                int[] indexes = findLocation(__startBtn);
                if (indexes == null)
                    return false;
                else {
                    //! NOT IMPLEMENTED YET
                    // up movement
                    // down movement
                    // left movement
                    // right movement
                }
            }
        }

        public boolean applyMovement () {
            if (isValidMovement()) {
                __startBtn.setText(" ");
                __jumpBtn.setText(" ");
                __endBtn.setText("P");
                return true;
            }
            else 
                return false;
        }

        public int[] findLocation (JButton btn) {
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

        // returns jump indexes of movement
        public int[] findLocation (JButton start, JButton end) {
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

import java.awt.GridLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class PegSolitaire extends JFrame implements ActionListener {
    public enum CellValue {PEG(), WALL, EMPTY} 

    private  JButton cellButtons[][];
    private GridLayout gridLayout;
    private Container container;
    private JButton startBtn;
    private JButton endBtn;
    private Movement mov;   // keeps the movement for undo movement
    private int numOfMov;
    private int score;

    public PegSolitaire () {
        super("Peg Solitaire");

        gridLayout = new GridLayout(9, 9);  //!!
        setLayout(gridLayout);
        container = getContentPane();
        
        mov = new Movement(); // empty movement
        startBtn = endBtn = null;
        
        //! Select the board type 
        SetGermanBoard();
        setScore(); // set initial score of the board
        numOfMov = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // if start button is selected, make movement with end buttons
        if (startBtn == null)
            startBtn = (JButton) e.getSource();
        else {
            // set and apply movement
            endBtn = (JButton) e.getSource();
            mov.set(startBtn, endBtn);
            if (mov.applyMovement()) {
                ++numOfMov; // new movement made
                --score;    // update the score (one peg removed)
                //! change the display content as numOfMov and score
            }
            else
                JOptionPane.showMessageDialog(this, "Illegal movement", "Error", JOptionPane.ERROR_MESSAGE);

            // set movement references as null for next movement                       
            startBtn = endBtn = null;   
        }
        
        // Check if game is over 
        if (isGameOver()) 
            JOptionPane.showMessageDialog(
                this, String.format("Game is over. Your score: %d", score));
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
                add(cellButtons[i][j]);
            }
        }
        cellButtons[4][4].setText(" "); // center empty cell
    }   

    public boolean isGameOver () {
        for (int i = 0; i < cellButtons.length; ++i)    
            for (int j = 0; j < cellButtons[i].length; ++j)
                if (Movement.isMovable(cellButtons[i][j]))
                    return false;
        return true;
    }

    public int setScore () {
        int count = 0;
        for (int i = 0; i < cellButtons.length; ++i)
            for (int j = 0; j < cellButtons[i].length; ++j)
                if (cellButtons[i][j].getText().equals("P"))
                    ++count;
        return count;
    }
    

    public class Movement {
        private JButton startBtn;
        private JButton jumpBtn;
        private JButton endBtn;

        public Movement (JButton start, JButton end) {
            startBtn = start;
            endBtn = end;
            jumpBtn = null;
        }

        public Movement () {
            this(null, null);
        }
        
        public void set (JButton start, JButton end) {
            // jumpBtn is depend on start and end btn so set as null 
            startBtn = start;
            endBtn = end;
            jumpBtn = null; 
        }

        public boolean undoMovement () {
            // if existing movement is valid, apply revere of it
            if (isValidMovement()) {
                startBtn.setText("P");
                jumpBtn.setText("P");
                endBtn.setText(" ");
                return true;
            }
            else 
                return false;
        }

        public boolean applyMovement () {
            if (isValidMovement()) {
                startBtn.setText(" ");
                jumpBtn.setText(" ");
                endBtn.setText("P");
                return true;
            }
            else 
                return false;
        }

        public int[] findLocation (JButton btn) {
            int indexes[] = null;

            for (int i = 0; i < cellButtons.length && indexes == null; ++i) 
                for (int j = 0; j < cellButtons[i].length && indexes == null; ++j)
                    if (cellButtons[i][j].equals(btn)) {
                        indexes = new int[2];
                        indexes[0] = i; // assign row
                        indexes[1] = j; // assign col
                    }
            return indexes;
        }

        public boolean isValidMovement () {
            // find and assign the jumpBtn if it's legal
            if (jumpBtn == null) {
                int[] indexes = findLocation(startBtn, endBtn); 
                // jump indexes cannot create becaue of inproper start and end positions
                if (indexes == null)
                    return false;
                jumpBtn = cellButtons[indexes[0]][indexes[1]];
            }

            return  startBtn.getText().equals("P") && 
                    jumpBtn.getText().equals("P") && 
                    endBtn.getText().equals(" ");
        }

        // returns jump indexes of movement
        public int[] findLocation (JButton start, JButton end) {
            int[] startIndexes = findLocation(startBtn);
            int[] endIndexes = findLocation(endBtn);
            int[] jumpIndexes = null;
            int row = -1;    // jump button row
            int col = -1;    // jump button coloumn

            if (startIndexes != null && endIndexes != null) {
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
                
                // check if jump indexes in range 
                if (0 <= row && row < cellButtons.length && 0 <= col && col < cellButtons[row].length) {
                    jumpIndexes = new int[2];
                    jumpIndexes[0] = row;
                    jumpIndexes[1] = col;
                }
            }
            return jumpIndexes;
        }
    } // end of Movement Class 

}

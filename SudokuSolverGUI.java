
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

public class SudokuSolverGUI extends JFrame {

    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];
    private HashSet<Integer>[] rows = new HashSet[SIZE];
    private HashSet<Integer>[] cols = new HashSet[SIZE];
    private HashSet<Integer>[] subgrids = new HashSet[SIZE];
    private boolean manualMode = false;
    private JTextField selectedCell;

    public SudokuSolverGUI() {
        setTitle("Sudoku Solver");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background color for the main window
        getContentPane().setBackground(Color.WHITE);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(SIZE, SIZE));
        gridPanel.setBackground(Color.WHITE);

        for (int row = 0; row < SIZE; row++) {
            rows[row] = new HashSet<>();
            cols[row] = new HashSet<>();
            subgrids[row] = new HashSet<>();
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setForeground(Color.DARK_GRAY); // Text color
                cells[row][col].addActionListener(new CellClickListener(row, col));

                // Set borders for 3x3 subgrids
                Border border = BorderFactory.createMatteBorder(
                    row % 3 == 0 ? 2 : 1, // top border
                    col % 3 == 0 ? 2 : 1, // left border
                    row % 3 == 2 ? 2 : 1, // bottom border
                    col % 3 == 2 ? 2 : 1, // right border
                    Color.LIGHT_GRAY // border color
                );
                cells[row][col].setBorder(border);
                cells[row][col].setBackground(Color.WHITE); // Cell background color

                gridPanel.add(cells[row][col]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 2, 5, 5));
        buttonPanel.setBackground(Color.WHITE); // Button panel background color

        JButton loadButton = new JButton("Load Puzzle");
        loadButton.setBackground(new Color(173, 216, 230)); // Light blue background
        loadButton.setForeground(Color.BLACK); // Text color
        loadButton.setFont(new Font("Arial", Font.BOLD, 16));
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPuzzle();
            }
        });
        buttonPanel.add(loadButton);

        JButton solveButton = new JButton("Automatic Solve");
        solveButton.setBackground(new Color(144, 238, 144)); // Light green background
        solveButton.setForeground(Color.BLACK); // Text color
        solveButton.setFont(new Font("Arial", Font.BOLD, 16));
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        solvePuzzle();
                    }
                }).start();
            }
        });
        buttonPanel.add(solveButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(255, 182, 193)); // Light pink background
        clearButton.setForeground(Color.BLACK); // Text color
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBoard();
            }
        });
        buttonPanel.add(clearButton);

        JButton manualModeButton = new JButton("Manual Mode");
        manualModeButton.setBackground(new Color(255, 255, 204)); // Light yellow background
        manualModeButton.setForeground(Color.BLACK); // Text color
        manualModeButton.setFont(new Font("Arial", Font.BOLD, 16));
        manualModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manualMode = !manualMode;
                manualModeButton.setText(manualMode ? "Manual Mode On" : "Manual Mode Off");
                if (manualMode) {
                    clearBoard();
                }
            }
        });
        buttonPanel.add(manualModeButton);

        for (int i = 1; i <= 9; i++) {
            JButton numberButton = new JButton(String.valueOf(i));
            numberButton.setBackground(new Color(211, 211, 211)); // Light gray background
            numberButton.setForeground(Color.BLACK); // Text color
            numberButton.setFont(new Font("Arial", Font.BOLD, 16));
            int number = i;
            numberButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fillNumber(number);
                }
            });
            buttonPanel.add(numberButton);
        }

        add(buttonPanel, BorderLayout.EAST);
    }

    private void loadPuzzle() {
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = puzzle[row][col];
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(new Color(211, 211, 211)); // Light gray background
                    rows[row].add(puzzle[row][col]);
                    cols[col].add(puzzle[row][col]);
                    subgrids[(row / 3) * 3 + col / 3].add(puzzle[row][col]);
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                    cells[row][col].setBackground(Color.WHITE); // White background for editable cells
                }
            }
        }
    }

    private void solvePuzzle() {
        if (solve()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(SudokuSolverGUI.this, "Sudoku Solved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(SudokuSolverGUI.this, "No solution exists for the given Sudoku board.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    private void clearBoard() {
        for (int row = 0; row < SIZE; row++) {
            rows[row].clear();
            cols[row].clear();
            subgrids[row].clear();
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setBackground(Color.WHITE); // Reset background color
                board[row][col] = 0;
            }
        }
    }

    private boolean isValid(int row, int col, int num) {
        if (rows[row].contains(num) || cols[col].contains(num) || subgrids[(row / 3) * 3 + col / 3].contains(num)) {
            return false;
        }
        return true;
    }

    private boolean solve() {
        int[] empty = findEmptyCell();
        if (empty == null) {
            return true;
        }
        int row = empty[0];
        int col = empty[1];

        for (int num = 1; num <= 9; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                rows[row].add(num);
                cols[col].add(num);
                subgrids[(row / 3) * 3 + col / 3].add(num);
                cells[row][col].setText(String.valueOf(num));

                try {
                    Thread.sleep(5); // Delay of 5 milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (solve()) {
                    return true;
                }

                // Undo move
                board[row][col] = 0;
                rows[row].remove(num);
                cols[col].remove(num);
                subgrids[(row / 3) * 3 + col / 3].remove(num);
                cells[row][col].setText("");
            }
        }
        return false;
    }

    private int[] findEmptyCell() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    private void fillNumber(int number) {
        if (selectedCell != null && manualMode) {
            int row = selectedCell.getY() / cells[0][0].getHeight();
            int col = selectedCell.getX() / cells[0][0].getWidth();
            if (isValid(row, col, number)) {
                cells[row][col].setText(String.valueOf(number));
                cells[row][col].setBackground(Color.WHITE);
                board[row][col] = number;
                rows[row].add(number);
                cols[col].add(number);
                subgrids[(row / 3) * 3 + col / 3].add(number);
                selectedCell = null; // Deselect cell after placing the number
            } else {
                cells[row][col].setBackground(Color.RED);
                Timer timer = new Timer(500, e -> cells[row][col].setBackground(Color.WHITE));
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private class CellClickListener implements ActionListener {
        private final int row, col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (manualMode) {
                if (selectedCell != null) {
                    selectedCell.setBackground(Color.WHITE); // Deselect the previously selected cell
                }
                selectedCell = cells[row][col];
                selectedCell.setBackground(Color.LIGHT_GRAY); // Highlight the selected cell
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuSolverGUI frame = new SudokuSolverGUI();
            frame.setVisible(true);
        });
    }
}


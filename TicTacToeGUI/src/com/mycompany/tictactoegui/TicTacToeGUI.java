package com.mycompany.tictactoegui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class TicTacToeGUI extends JFrame {
    private JButton[][] cells;
    private char currentPlayer;
    private JLabel statusLabel;
    private JTextField player1NameField;
    private JTextField player2NameField;
    private JLabel player1Label;
    private JLabel player2Label;
    private int player1Score;
    private int player2Score;
    private DefaultTableModel scoreTableModel;
    private TicTacToe game;
    private JButton restartButton;
    private boolean isPlayerVsPlayer;

    public TicTacToeGUI(int boardSize, boolean isPlayerVsPlayer) {
        this.isPlayerVsPlayer = isPlayerVsPlayer;
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(boardSize, boardSize));
        cells = new JButton[boardSize][boardSize];
        currentPlayer = 'X';
        game = new TicTacToe(boardSize);

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
                button.setBackground(Color.WHITE);
                int finalRow = row, finalCol = col;
                button.addActionListener(e -> {
                    if (cells[finalRow][finalCol].getText().isEmpty()) {
                        cells[finalRow][finalCol].setText(String.valueOf(currentPlayer));
                        cells[finalRow][finalCol].setForeground(currentPlayer == 'X' ? Color.RED : Color.ORANGE);
                        game.makeMove(finalRow, finalCol, currentPlayer);
                        if (game.isWon(currentPlayer)) {
                            String winnerName = getPlayerName(currentPlayer);
                            showWinnerMessage(winnerName);
                            updateScore(currentPlayer);
                            disableAllCells();
                        } else if (game.isDraw()) {
                            showDrawMessage();
                            disableAllCells();
                        } else {
                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                            statusLabel.setText(getPlayerName(currentPlayer) + "'s turn");
                            if (!isPlayerVsPlayer && currentPlayer == 'O') {
                                makeComputerMove();
                            }
                        }
                    }
                });
                cells[row][col] = button;
                gamePanel.add(button);
            }
        }

        JPanel settingsPanel = new JPanel();
        player1Label = new JLabel("Player 1:");
        player1NameField = new JTextField(10);
        player2Label = new JLabel("Player 2");
        player2NameField = new JTextField(10);

        settingsPanel.add(player1Label);
        settingsPanel.add(player1NameField);
        if (isPlayerVsPlayer) {
            settingsPanel.add(player2Label);
            settingsPanel.add(player2NameField);
        }

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        statusLabel = new JLabel(getPlayerName(currentPlayer) + "'s turn");


        JPanel scorePanel = new JPanel(new BorderLayout());
        JLabel scoreLabel = new JLabel("Scorecard", JLabel.CENTER);

        scoreTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Player", "Score"});
        JTable scoreTable = new JTable(scoreTableModel);
        scoreTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(200, 100));

        scorePanel.add(scoreLabel, BorderLayout.NORTH);
        scorePanel.add(scrollPane, BorderLayout.CENTER);

        add(gamePanel, BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
        add(restartButton, BorderLayout.EAST);
        add(scorePanel, BorderLayout.WEST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String getPlayerName(char player) {
        if (player == 'X') {
            return player1NameField.getText();
        } else {
            return isPlayerVsPlayer ? player2NameField.getText() : "Player 2";
        }
    }

    private void updateScore(char player) {
        if (player == 'X') {
            player1Score++;
            updateScoreTable(player1NameField.getText(), player1Score);
        } else {
            player2Score++;
            updateScoreTable(isPlayerVsPlayer ? player2NameField.getText() : "Player 2", player2Score);
        }
    }

    private void updateScoreTable(String playerName, int score) {
        int rowCount = scoreTableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            if (scoreTableModel.getValueAt(i, 0).equals(playerName)) {
                scoreTableModel.setValueAt(score, i, 1);
                return;
            }
        }
        scoreTableModel.addRow(new Object[]{playerName, score});
    }

    private void disableAllCells() {
        for (JButton[] row : cells) {
            for (JButton cell : row) {
                cell.setEnabled(false);
            }
        }
    }

    private void restartGame() {
        for (JButton[] row : cells) {
            for (JButton cell : row) {
                cell.setText("");
                cell.setEnabled(true);
                cell.setBackground(Color.WHITE);
            }
        }
        currentPlayer = 'X';
        game.reset();
        statusLabel.setText(getPlayerName(currentPlayer) + "'s turn");
        if (!isPlayerVsPlayer && currentPlayer == 'O') {
            makeComputerMove();
        }
    }

    private void showWinnerMessage(String winnerName) {
        String message = "Congratulations, " + winnerName + "! You won!";
        JOptionPane.showMessageDialog(this, message, "Winner", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDrawMessage() {
        JOptionPane.showMessageDialog(this, "It's a draw!", "Draw", JOptionPane.INFORMATION_MESSAGE);
    }

    private void makeComputerMove() {
        int boardSize = game.getBoardSize();
        int[] move = game.getComputerMove();
        if (move != null) {
            int row = move[0];
            int col = move[1];
            cells[row][col].setText(String.valueOf(currentPlayer));
            cells[row][col].setForeground(currentPlayer == 'X' ? Color.RED : Color.ORANGE);
            game.makeMove(row, col, currentPlayer);
            if (game.isWon(currentPlayer)) {
                String winnerName = getPlayerName(currentPlayer);
                showWinnerMessage(winnerName);
                updateScore(currentPlayer);
                disableAllCells();
            } else if (game.isDraw()) {
                showDrawMessage();
                disableAllCells();
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                statusLabel.setText(getPlayerName(currentPlayer) + "'s turn");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int boardSize = askBoardSize();
            boolean isPlayerVsPlayer = askGameMode();
            new TicTacToeGUI(boardSize, isPlayerVsPlayer);
        });
    }

    private static int askBoardSize() {
        String[] options = {"3x3", "4x4", "5x5"};
        int choice = JOptionPane.showOptionDialog(null, "Select board size", "Board Size", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        return choice + 3;
    }

    private static boolean askGameMode() {
        String[] options = {"Player vs Player", "Player vs PC"};
        int choice = JOptionPane.showOptionDialog(null, "Select game mode", "Game Mode", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        return choice == 0;
    }
}

class TicTacToe {
    private char[][] board;
    private int boardSize;

    public TicTacToe(int boardSize) {
        this.boardSize = boardSize;
        board = new char[boardSize][boardSize];
        initializeBoard();
    }

    private void initializeBoard() {
        for (char[] row : board) {
            java.util.Arrays.fill(row, ' ');
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void makeMove(int row, int col, char player) {
        board[row][col] = player;
    }

    public boolean isWon(char player) {
        for (int i = 0; i < boardSize; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] != player)
                    rowWin = false;
                if (board[j][i] != player)
                    colWin = false;
            }
            if (rowWin || colWin)
                return true;
        }

        boolean diag1Win = true;
        boolean diag2Win = true;
        for (int i = 0; i < boardSize; i++) {
            if (board[i][i] != player)
                diag1Win = false;
            if (board[i][boardSize - 1 - i] != player)
                diag2Win = false;
        }
        return diag1Win || diag2Win;
    }

    public boolean isDraw() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ')
                    return false;
            }
        }
        return true;
    }

    public void reset() {
        initializeBoard();
    }

    public int[] getComputerMove() {

        int[] move = null;
        int availableMoves = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ')
                    availableMoves++;
            }
        }
        if (availableMoves > 0) {
            int randomMove = (int) (Math.random() * availableMoves) + 1;
            int count = 0;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (board[i][j] == ' ') {
                        count++;
                        if (count == randomMove) {
                            move = new int[]{i, j};
                            break;
                        }
                    }
                }
            }
        }
        return move;
    }
}

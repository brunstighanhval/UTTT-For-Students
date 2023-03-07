package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.ArrayList;
import java.util.List;

public class NeverLoseBot implements IBot {
    private static final String BOTNAME = "NeverLoseBot";

    private List<Character> board;
    private char currentWinner;

    public NeverLoseBot() {
        this.board = makeBoard();
        this.currentWinner = '-';
    }

    private static List<Character> makeBoard() {
        List<Character> board = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            board.add(' ');
        }
        return board;
    }

    public void printBoard() {
        for (int i = 0; i < 3; i++) {
            List<Character> row = board.subList(i * 3, (i + 1) * 3);

        }
    }

    private static void printBoardNums() {
        // 0 | 1 | 2
        List<String> numberBoard = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                numberBoard.add(String.valueOf(i * 3 + j));
            }
        }
        for (int i = 0; i < 3; i++) {
            List<String> row = numberBoard.subList(i * 3, (i + 1) * 3);

        }
    }

public boolean doMove(int square, char letter) {
        if (board.get(square) == ' ') {
            board.set(square, letter);
            if (winner(square, letter)) {
                currentWinner = letter;
            }
            return true;
        }
        return false;
    }

    private boolean winner(int square, char letter) {
        // check the row
        int rowInd = square / 3;
        List<Character> row = board.subList(rowInd * 3, (rowInd + 1) * 3);
        if (row.stream().allMatch(s -> s == letter)) {
            return true;
        }
        int colInd = square % 3;
        List<Character> column = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            column.add(board.get(colInd + i * 3));
        }
        if (column.stream().allMatch(s -> s == letter)) {
            return true;
        }
        if (square % 2 == 0) {
            List<Character> diagonal1 = new ArrayList<>();
            diagonal1.add(board.get(0));
            diagonal1.add(board.get(4));
            diagonal1.add(board.get(8));
            if (diagonal1.stream().allMatch(s -> s == letter)) {
                return true;
            }
            List<Character> diagonal2 = new ArrayList<>();
            diagonal2.add(board.get(2));
            diagonal2.add(board.get(4));
            diagonal2.add(board.get(6));
            if (diagonal2.stream().allMatch(s -> s == letter)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public IMove doMove(IGameState state) {
        return null;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
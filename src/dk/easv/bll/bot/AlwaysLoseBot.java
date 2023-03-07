package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlwaysLoseBot implements IBot {
    private static final String BOTNAME = "AlwaysLoseBot";

    protected int[][] preferredMoves = {
            {1, 1},
            {0, 0}, {2, 2}, {0, 2}, {2, 0},
            {0, 1}, {2, 1}, {1, 0}, {1, 2}};
    private String player;

    @Override
    public IMove doMove(IGameState state) {
        int macroX = state.getField().getMacroboard().length / 2;
        int macroY = state.getField().getMacroboard()[0].length / 2;
        List<IMove> winMoves = getWinningMoves(state);
        if (!winMoves.isEmpty()) {
            return winMoves.get(0);
        }
        IMove middleMove = StartsSetMiddel(state);
        if (middleMove != null && state.getField().getPlayerId(middleMove.getX(), middleMove.getY()) == IField.EMPTY_FIELD && state.getMoveNumber() == 0) {
            return middleMove;}

        // Check if the middle spot of a corner is available
        if(state.getField().getBoard()[0][0].equals(IField.EMPTY_FIELD) && state.getField().getMacroboard()[0][0].equals(IField.AVAILABLE_FIELD)){
            return new Move(0, 0); // Middle of top left corner
        }
        if(state.getField().getBoard()[2][2].equals(IField.EMPTY_FIELD) && state.getField().getMacroboard()[0][2].equals(IField.AVAILABLE_FIELD)){
            return new Move(2, 2); // Middle of top right corner
        }
        if(state.getField().getBoard()[6][6].equals(IField.EMPTY_FIELD) && state.getField().getMacroboard()[2][0].equals(IField.AVAILABLE_FIELD)){
            return new Move(6, 6); // Middle of bottom left corner
        }
        if(state.getField().getBoard()[8][8].equals(IField.EMPTY_FIELD) && state.getField().getMacroboard()[2][2].equals(IField.AVAILABLE_FIELD)){
            return new Move(8, 8); // Middle of bottom right corner
        }
        // Check if the middle spot of the board is available
        if(state.getField().getBoard()[macroX][macroY].equals(IField.EMPTY_FIELD) && state.getField().getMacroboard()[macroX][macroY].equals(IField.AVAILABLE_FIELD)) {
            return new Move(macroX, macroY); // Middle of the board
        }
        if (state.getMoveNumber() > 0) {
            for (int[] move : preferredMoves) {
                int x = move[0];
                int y = move[1];
                if (state.getField().getPlayerId(x, y) == IField.EMPTY_FIELD) {
                    for (IMove availableMove : state.getField().getAvailableMoves()) {
                        if (availableMove.getX() == x && availableMove.getY() == y) {
                            return availableMove;
                        }
                    }
                }
            }
        }

        return state.getField().getAvailableMoves().get(0);
    }

    private IMove StartsSetMiddel(IGameState state) {
        int macroX = state.getField().getMacroboard().length / 2;
        int macroY = state.getField().getMacroboard()[0].length / 2;

        if (state.getField().getBoard()[macroX][macroY].equals(IField.EMPTY_FIELD) && state.getField().getMacroboard()[macroX][macroY].equals(IField.AVAILABLE_FIELD)) {
            return new Move(macroX, macroY);
        }

        IField field = state.getField();
        int boardSize = field.getBoard().length;
        int middle = boardSize / 2;

        if (state.getMoveNumber() == 0 && field.getPlayerId(middle, middle) == IField.EMPTY_FIELD) {
            // Check if the middle spot is available
            return new Move(middle, middle);
        }

        if (state.getField().getMacroboard()[0][0].equals(IField.AVAILABLE_FIELD)) {
            // Check if the top left corner is available
            return new Move(0, 0);
        }

        if (state.getField().getMacroboard()[0][2].equals(IField.AVAILABLE_FIELD)) {
            // Check if the top right corner is available
            return new Move(0, 2);
        }

        if (state.getField().getMacroboard()[2][0].equals(IField.AVAILABLE_FIELD)) {
            // Check if the bottom left corner is available
            return new Move(2, 0);
        }

        if (state.getField().getMacroboard()[2][2].equals(IField.AVAILABLE_FIELD)) {
            // Check if the bottom right corner is available
            return new Move(2, 2);
        }

        return null;
    }
    public boolean isWinningMove(IGameState state, IMove move, String player) {
        // Clones the array and all values to a new array, so we don't mess with the game
        String[][] board = Arrays.stream(state.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);

        //Places the player in the game. Sort of a simulation.
        board[move.getX()][move.getY()] = player;

        int startX = move.getX() - (move.getX() % 3);
        int startY = move.getY() - (move.getY() % 3);

        // Check row
        for (int i = 0; i < 3; i++) {
            if (board[startX + i][move.getY()].equals(player)) {
                if (board[startX + i][startY].equals(player) &&
                        board[startX + i][startY + 1].equals(player) &&
                        board[startX + i][startY + 2].equals(player)) {
                    return true;
                }
            }
        }

        // Check column
        for (int i = 0; i < 3; i++) {
            if (board[move.getX()][startY + i].equals(player)) {
                if (board[startX][startY + i].equals(player) &&
                        board[startX + 1][startY + i].equals(player) &&
                        board[startX + 2][startY + i].equals(player)) {
                    return true;
                }
            }
        }

        // Check diagonal
        if (board[startX][startY].equals(player)) {
            if (board[startX + 1][startY + 1].equals(player) &&
                    board[startX + 2][startY + 2].equals(player)) {
                return true;
            }
        }
        if (board[startX][startY + 2].equals(player)) {
            if (board[startX + 1][startY + 1].equals(player) &&
                    board[startX + 2][startY].equals(player)) {
                return true;
            }
        }

        return false;
    }
    /*
    private boolean isWinningMove(IGameState state, IMove move, String player){
        // Clones the array and all values to a new array, so we don't mess with the game
        String[][] board = Arrays.stream(state.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);

        //Places the player in the game. Sort of a simulation.
        board[move.getX()][move.getY()] = player;

        int startX = move.getX()-(move.getX()%3);
        if(board[startX][move.getY()]==player)
            if (board[startX][move.getY()] == board[startX+1][move.getY()] &&
                    board[startX+1][move.getY()] == board[startX+2][move.getY()])
                return true;

        int startY = move.getY()-(move.getY()%3);
        if(board[move.getX()][startY]==player)
            if (board[move.getX()][startY] == board[move.getX()][startY+1] &&
                    board[move.getX()][startY+1] == board[move.getX()][startY+2])
                return true;


        if(board[startX][startY]==player)
            if (board[startX][startY] == board[startX+1][startY+1] &&
                    board[startX+1][startY+1] == board[startX+2][startY+2])
                return true;

        if(board[startX][startY+2]==player)
            if (board[startX][startY+2] == board[startX+1][startY+1] &&
                    board[startX+1][startY+1] == board[startX+2][startY])
                return true;

        return false;
    }

     */
    private List<IMove> getWinningMoves(IGameState state){
        String player = "1";
        if(state.getMoveNumber()%2==0)
            player="0";

        List<IMove> avail = state.getField().getAvailableMoves();

        List<IMove> winningMoves = new ArrayList<>();
        for (IMove move:avail) {
            if(isWinningMove(state,move,player))
                winningMoves.add(move);
        }
        return winningMoves;
    }



    @Override
    public String getBotName() {
        return BOTNAME;
    }
















/*
// Continue with the existing logic to check for preferred moves
            for (int[] move : preferredMoves) {
                int x = move[0];
                int y = move[1];
                if (state.getField().getPlayerId(x, y) == IField.EMPTY_FIELD) {
                    return new Move(x, y);
                }
            }
        return state.getField().getAvailableMoves().get(0);
 */


    /*
    List<int[]> newPreferredMoves = new ArrayList<>();
        for (int[] move : preferredMoves) {

            if (!(move[0] == 0 && move[1] == 0) && !(move[0] == 0 && move[1] == 2) &&
                    !(move[0] == 2 && move[1] == 0) && !(move[0] == 2 && move[1] == 2)) {
                newPreferredMoves.add(move);
            }

        }
        return state.getField().getAvailableMoves().get(0);
     */

    /*
    @Override
public IMove doMove(IGameState state) {
    List<IMove> winMoves = getWinningMoves(state);
    if (!winMoves.isEmpty()) {
        return winMoves.get(0);
    }

    List<IMove> availableMoves = state.getField().getAvailableMoves();
    for (int i = 0; i < preferredMoves.length; i++) {
        int[] move = preferredMoves[i];
        IMove preferredMove = new Move(move[0], move[1]);
        if (availableMoves.contains(preferredMove)) {
            return preferredMove;
        }
    }

    // If the middle is not available, just return the first available move.
    return availableMoves.get(0);
}
     */




}
package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class butbut implements IBot{

    private static final String BOTNAME = "butbut";
    @Override
    public IMove doMove(IGameState state){
        List<IMove> winMoves = getWinningMoves(state);
        if(!winMoves.isEmpty()) {
            return winMoves.get(0);
        }

        List<int[]> newPreferredMoves = new ArrayList<>();
        for (int[] move : preferredMoves) {

            if (!(move[0] == 0 && move[1] == 0) && !(move[0] == 0 && move[1] == 2) &&
                    !(move[0] == 2 && move[1] == 0) && !(move[0] == 2 && move[1] == 2)) {
                newPreferredMoves.add(move);
            }
        }

        Collections.shuffle(newPreferredMoves);

        for (int[] move : newPreferredMoves) {
            if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD)) {
                for (int[] selectedMove : preferredMoves) {
                    if (!(selectedMove[0] == 0 && selectedMove[1] == 0) && !(selectedMove[0] == 0 && selectedMove[1] == 2) &&
                            !(selectedMove[0] == 2 && selectedMove[1] == 0) && !(selectedMove[0] == 2 && selectedMove[1] == 2)) {
                        int x = move[0] * 3 + selectedMove[0];
                        int y = move[1] * 3 + selectedMove[1];
                        if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD)) {
                            return new Move(x, y);
                        }
                    }
                }
            }
        }

        return state.getField().getAvailableMoves().get(0);
    }

    protected int[][] preferredMoves = {
            {1, 1}, //Center
            {0, 1}, {2, 1}, {1, 0}, {1, 2},//Outer Middles ordered across
            {0, 0}, {2, 2}, {0, 2}, {2, 0}}; //Corners ordered across



    // Compile a list of all available winning moves
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



    public String getBotName() {
        return BOTNAME;
    }
}

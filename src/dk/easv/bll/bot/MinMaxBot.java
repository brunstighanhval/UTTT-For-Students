package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.List;

public class MinMaxBot implements IBot {
    private static final String BOTNAME = "MinMaxBot";


    @Override
    public String getBotName() {
        return BOTNAME;
    }

    private IGameState state;



    @Override
    public IMove doMove(IGameState state) {
        this.state = state;
        char[][] board = state.getField().getBoard();
        char currentPlayer = state.getMoveNumber() % 2 == 0 ? X_PLAYER : O_PLAYER;
        int maxDepth = 5;

        IMove bestMove = null;
        while (bestMove == null) {
            int[] move = getBestMove(board, currentPlayer, maxDepth);
            bestMove = (IMove) isValidMove(board, move[0], move[1]);
        }

        return bestMove;
    }

    private boolean isValidMove(char[][]board, int row, int col) {
        List<IMove> avail = state.getField().getAvailableMoves();
        return avail;
    }


    // Constants for player X and player O
    private static final char X_PLAYER = 'X';
    private static final char O_PLAYER = 'O';
    public int[] getBestMove(char[][] board, char currentPlayer, int maxDepth) {
        int[] bestMove = new int[2];
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Loop through all possible moves
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (isValidMove(board, i, j)) {
                    char[][] newGameState = makeMove(board, i, j, currentPlayer);
                    int score = minMax(newGameState, currentPlayer, maxDepth - 1, alpha, beta, false);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }
    private char[][] makeMove(char[][] iGameState, int row, int col, char player) {
        // Create a new game state to represent the move
        char[][] newGameState = new char[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                newGameState[i][j] = iGameState[i][j];
            }
        }

        // Place the player's symbol in the specified row and column
        newGameState[row][col] = player;

        return newGameState;
    }


    /**
     * Method to get the best score for the given player using the MinMax algorithm
     *
     * @param IGameState   Current state of the game
     * @param player      The player for which to find the best score
     * @param depth       The current depth in the game tree
     * @param alpha       The alpha value for alpha-beta pruning
     * @param beta        The beta value for alpha-beta pruning
     * @param isMaxPlayer True if the current player is the maximizing player, false otherwise
     * @return The best score for the player
     */
    private int minMax(char[][] IGameState, char player, int depth, int alpha, int beta, boolean isMaxPlayer) {
        // Check if the game is over or if we have reached the maximum depth
        if (isGameOver(IGameState) || depth == 0) {
            return getScore(IGameState, player);
        }

        // Set the initial score to the worst possible value for the current player
        int score = isMaxPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Loop through all possible moves
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (isValidMove(IGameState, i, j)) {
                    char[][] newGameState = makeMove(IGameState, i, j, isMaxPlayer ? X_PLAYER : O_PLAYER);
                    int moveScore = minMax(newGameState, player, depth - 1, alpha, beta, !isMaxPlayer);

                    // Update the score based on whether the current player is the maximizing player
                    if (isMaxPlayer && moveScore > score) {
                        score = moveScore;
                        alpha = Math.max(alpha, score);
                    } else if (!isMaxPlayer && moveScore < score) {
                        score = moveScore;
                        beta = Math.min(beta, score);
                    }

                    // Perform alpha-beta pruning if the score exceeds the alpha or beta value
                    if (alpha >= beta) {
                        return score;
                    }
                }
            }
        }

        return score;
    }



    /**
     * Method to check if the game is over
     *
     * @param IGameState Current state of the game
     * @return True if the game is over, false otherwise
     */
    private boolean isGameOver(char[][] IGameState) {
        // Check for a win in each sub-grid
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                if (hasWon(IGameState, i, j)) {
                    return true;
                }
            }
        }

        // Check for a tie by counting the number of empty cells
        int emptyCells = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (IGameState[i][j] == '-') {
                    emptyCells++;
                }
            }
        }
        return emptyCells == 0;
    }

    /**
     * Method to get the score for the given player in the current state of the game
     *
     * @param IGameState Current state of the game
     * @param player    The player for which to find the score
     * @return The score for the player
     */
    private int getScore(char[][] IGameState, char player) {
        // Check for a win
        if (hasWon(IGameState, 0, 0)) {
            return player == X_PLAYER ? 1 : -1;
        }

        // Check for a sub-grid win
        int subGridScore = 0;
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                char[][] subGrid = new char[3][3];
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        subGrid[k][l] = IGameState[i + k][j + l];
                    }
                }
                if (hasWon(subGrid, 0, 0)) {
                    subGridScore += subGrid[1][1] == player ? 1 : -1;
                }
            }
        }

        // Check for a tie
        int emptyCells = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (IGameState[i][j] == '-') {
                    emptyCells++;
                }
            }
        }
        if (emptyCells == 0) {
            return 0;
        }

        // Calculate the score as a weighted sum of the above scores
        int score = 0;
        score += 100 * subGridScore;
        score += 10 * getTwoInARowScore(IGameState, player);
        score += 1 * getEmptyCellsScore(IGameState, player);
        return score;
}

    /**
     * Method to get the score for the given player based on the number of two-in-a-rows they have in the current state of the game
     *
     * @param IGameState Current state of the game
     * @param player The player for which to find the score
     * @return The score for the player based on two-in-a-rows
     */
    private int getTwoInARowScore(char[][] IGameState, char player) {
        int score = 0;

        // Check rows
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                for (int k = 0; k < 3; k++) {
                    int count = 0;
                    for (int l = 0; l < 3; l++) {
                        if (IGameState[i + k][j + l] == player) {
                            count++;
                        }
                        if (IGameState[i + k][j + l] == '-' && count == 2) {
                            score++;
                        }
                    }
                }
            }
        }

        // Check columns
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                for (int k = 0; k < 3; k++) {
                    int count = 0;
                    for (int l = 0; l < 3; l++) {
                        if (IGameState[i + l][j + k] == player) {
                            count++;
                        }
                        if (IGameState[i + l][j + k] == '-' && count == 2) {
                            score++;
                        }
                    }
                }
            }
        }

        // Check diagonals
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                int count = 0;
                for (int k = 0; k < 3; k++) {
                    if (IGameState[i + k][j + k] == player) {
                        count++;
                    }
                    if (IGameState[i + k][j + k] == '-' && count == 2) {
                        score++;
                    }
                }
                count = 0;
                for (int k = 0; k < 3; k++) {
                    if (IGameState[i + k][j + 2 - k] == player) {
                        count++;
                    }
                    if (IGameState[i + k][j + 2 - k] == '-' && count == 2) {
                        score++;
                    }
                }
            }
        }

        return score;
    }

    /**
     * Method to get the score for the given player based on the number of empty cells in the current state of the game
     *
     * @param IGameState Current state of the game
     * @param player The player for which to find the score
     * @return The score for the player based on empty cells
     */
    private int getEmptyCellsScore(char[][] IGameState, char player) {
        int score = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (IGameState[i][j] == '-') {
                    score += IGameState[(i / 3) * 3][(j / 3) * 3] == player ? 1 : -1;
                }
            }
        }
        return score;
    }

    /**
     * Method to check if the given player has won in the specified sub-grid
     *
     * @param IGameState Current state of the game
     * @param j       The row index of the top-left cell of the sub-grid
     * @param i         The column index of the top-left cell of the sub-grid
     * @return True if the player has won, false otherwise
     */
    private boolean hasWon(int i, char[][] IGameState, int j) {

        // Check rows
        for (int row = i; row < i + 3; row++) {
            if (IGameState[row][j] == IGameState[row][j + 1] && IGameState[row][j] == IGameState[row][j + 2]) {
                return true;
            }
        }

        // Check columns
        for (int col = j; col < j + 3; col++) {
            if (IGameState[i][col] == IGameState[i + 1][col] && IGameState[i][col] == IGameState[i + 2][col]) {
                return true;
            }
        }

        // Check diagonals
        if (IGameState[i][j] == IGameState[i + 1][j + 1] && IGameState[i][j] == IGameState[i + 2][j + 2]) {
            return true;
        }

        if (IGameState[i + 2][j] == IGameState[i + 1][j + 1] && IGameState[i + 2][j] == IGameState[i][j + 2]) {
            return true;
        }

        return false;
    }

    /**
     * Method to check if a given sub-grid has been won by the player at the given position
     *
     * @param subGrid The sub-grid to check
     * @param row The row of the player's position in the sub-grid
     * @param col The column of the player's position in the sub-grid
     * @return True if the sub-grid has been won by the player at the given position, false otherwise
     */
    private boolean hasWon(char[][] subGrid, int row, int col) {
        char player = subGrid[row][col];
        if (player == '-') {
            return false;
        }
        return (subGrid[row][(col + 1) % 3] == player && subGrid[row][(col + 2) % 3] == player) ||
                (subGrid[(row + 1) % 3][col] == player && subGrid[(row + 2) % 3][col] == player) ||
                (row == col && subGrid[(row + 1) % 3][(col + 1) % 3] == player && subGrid[(row + 2) % 3][(col + 2) % 3] == player) ||
                (row + col == 2 && subGrid[(row + 1) % 3][(col + 2) % 3] == player && subGrid[(row + 2) % 3][(col + 1) % 3] == player);
    }



}


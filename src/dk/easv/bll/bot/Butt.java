package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

public class Butt implements IBot {
    private static final String BOTNAME = "Butt";

    /*
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[][] board = new int[9][9];
        int[] macroBoard = new int[9];
        int player = 1;

        while (true) {
            // Print the current state of the board
            printBoard(board, macroBoard);

            // Check if the game is over
            int winner = checkWinner(macroBoard);
            if (winner != 0) {
                System.out.println("Player " + winner + " wins!");
                break;
            } else if (isBoardFull(board)) {
                System.out.println("Draw!");
                break;
            }

            // Get the player's move
            int[] move = getPlayerMove(scanner, board, macroBoard, player);

            // Update the board
            board[move[0]][move[1]] = player;
            macroBoard[move[1] / 3 * 3 + move[0] / 3] = checkWinner(board[move[1] / 3 * 3 + move[0] / 3]);

            // Switch to the other player
            player = 3 - player;
        }
    }

     */

    // Prints the current state of the board
    public static void printBoard(int[][] board, int[] macroBoard) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    System.out.print(" ");
                } else if (board[i][j] == 1) {
                    System.out.print("X");
                } else if (board[i][j] == 2) {
                    System.out.print("O");
                }

                if (j % 3 == 2) {
                    System.out.print(" ");
                }
            }

            System.out.print(" ");

            if (macroBoard[i] == 0) {
                System.out.println(" ");
            } else if (macroBoard[i] == 1) {
                System.out.println("X");
            } else if (macroBoard[i] == 2) {
                System.out.println("O");
            }

            if (i % 3 == 2) {
                System.out.println();
            }
        }

        System.out.println();
    }

    // Checks if a player has won the game
    public static int checkWinner(int[] board) {
        for (int i = 0; i < 3; i++) {
            if (board[i * 3] != 0 && board[i * 3] == board[i * 3 + 1] && board[i * 3 + 1] == board[i * 3 + 2]) {
                return board[i * 3];
            }

            if (board[i] != 0 && board[i] == board[i + 3] && board[i + 3] == board[i + 6]) {
                return board[i];
            }
        }

        if (board[0] != 0 && board[0] == board[4] && board[4] == board[8]) {
            return board[0];
        }

        if (board[2] != 0 && board[2] == board[4] && board[4] == board[6]) {
            return board[2];
        }

        return 0;
    }

    // Checks if the entire board is full
    public static boolean isBoardFull(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }


    public IMove doMove(IGameState state) {
        int[][] board = state.getField().getBoard();
        int[] macroBoard = state.getField().getMacroboard();
        int player = state.getMoveNumber() % 2 + 1;

        // Get all legal moves
        List<IMove> legalMoves = state.getField().getAvailableMoves();

        // Initialize bestMove and bestScore
        IMove bestMove = legalMoves.get(0);
        int bestScore = Integer.MIN_VALUE;

        // Loop through all legal moves
        for (IMove move : legalMoves) {
            // Clone the board and macroBoard
            int[][] clonedBoard = cloneBoard(board);
            int[] clonedMacroBoard = clonedBoard[move.getX()][move.getY()] == 0 ? cloneArray(macroBoard) : new int[9];

            // Make the move
            clonedBoard[move.getX()][move.getY()] = player;
            clonedMacroBoard[move.getY() / 3 * 3 + move.getX() / 3] = checkWinner(getLocalBoard(clonedBoard, move.getX() / 3, move.getY() / 3));

            // Calculate the score
            int score = minimax(clonedBoard, clonedMacroBoard, player, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            // Update bestMove and bestScore if necessary
            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }

        return bestMove;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
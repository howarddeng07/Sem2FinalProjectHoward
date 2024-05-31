//Howard's sem 2 final project (Mr Ewing)
import java.util.ArrayList;
import java.util.Scanner;

public class ChessMoveGenerator {
    private static final String[][] board = new String[8][8];
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeBoard();
        if (!getUserInputForPiecePlacement()) {
            System.out.println("Invalid setup: Both kings must be placed on the board.");
            return;
        }
        printBoard();

        while (true) {
            System.out.println("Enter the position of the piece to check moves for (e.g., E2) or 'exit' to quit:");
            String input = scanner.nextLine().toUpperCase();
            if ("EXIT".equals(input)) {
                break;
            }
            ArrayList<String> moves = generateMoves(input);
            if (moves != null) {
                System.out.println("Possible moves: " + moves);
                System.out.println("Total moves: " + moves.size());
            }
        }
        scanner.close();
    }

    private static void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = "X";
            }
        }
    }

    private static boolean getUserInputForPiecePlacement() {
        int friendlyPiecesCount = 0, opposingPiecesCount = 0;
        boolean hasKing = false, hasOpponentKing = false;
        int kingCount = 0, opponentKingCount = 0;

        System.out.println("Enter piece placements (e.g., K at C2, OK at D4). Type 'X' when finished:");
        while (true) {
            System.out.print("Enter piece and position (or 'X'): ");
            String input = scanner.nextLine().toUpperCase();
            if ("X".equals(input)) {
                break;
            }
            try {
                String[] parts = input.split(" AT ");
                String piece = parts[0].trim();
                String position = parts[1].trim();

                // Validate piece type and position
                if (!isValidPiece(piece) || !isValidPosition(position)) {
                    System.out.println("Invalid piece or position. Please enter a valid piece and position.");
                    continue;
                }

                int row = 8 - Character.getNumericValue(position.charAt(1));
                int col = position.charAt(0) - 'A';

                // Count pieces and enforce limits and rules
                if (piece.startsWith("O")) {
                    if (opposingPiecesCount >= 16) {
                        System.out.println("Cannot place more than 16 opposing pieces.");
                        continue;
                    }
                    opposingPiecesCount++;
                    if ("OK".equals(piece)) {
                        opponentKingCount++;
                        if (opponentKingCount > 1) {
                            System.out.println("Only one opposing King can be placed.");
                            continue;
                        }
                        hasOpponentKing = true;
                    }
                } else {
                    if (friendlyPiecesCount >= 16) {
                        System.out.println("Cannot place more than 16 friendly pieces.");
                        continue;
                    }
                    friendlyPiecesCount++;
                    if ("K".equals(piece)) {
                        kingCount++;
                        if (kingCount > 1) {
                            System.out.println("Only one King can be placed.");
                            continue;
                        }
                        hasKing = true;
                    }
                }

                // Place the piece on the board
                board[row][col] = piece;
            } catch (Exception e) {
                System.out.println("Invalid input format. Please try again.");
            }
        }
        return hasKing && hasOpponentKing;
    }

    private static boolean isValidPiece(String piece) {
        return piece.matches("^(K|Q|R|B|N|P|OK|OQ|OR|OB|ON|OP)$");
    }

    private static boolean isValidPosition(String position) {
        return position.matches("^[A-H][1-8]$");
    }

    private static void printBoard() {
        System.out.println("  A B C D E F G H");
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println((8 - i));
        }
        System.out.println("  A B C D E F G H");
    }

    private static ArrayList<String> generateMoves(String position) {
        if (position.length() != 2) {
            System.out.println("Invalid position format!");
            return null;
        }

        int row = 8 - Character.getNumericValue(position.charAt(1));
        int col = position.charAt(0) - 'A';

        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            System.out.println("Invalid position!");
            return null;
        }
        String piece = board[row][col];
        ArrayList<String> moves = new ArrayList<>();

        switch (piece) {
            case "K":
            case "OK":
                addKingMoves(row, col, moves, piece);
                break;
            case "Q":
            case "OQ":
                addQueenMoves(row, col, moves, piece);
                break;
            case "R":
            case "OR":
                addRookMoves(row, col, moves, piece);
                break;
            case "B":
            case "OB":
                addBishopMoves(row, col, moves, piece);
                break;
            case "N":
            case "ON":
                addKnightMoves(row, col, moves, piece);
                break;
            case "P":
                addPawnMoves(row, col, moves, piece, false);
                break;
            case "OP":
                addPawnMoves(row, col, moves, piece, true);
                break;
            default:
                System.out.println("No piece found at the specified position.");
                return null;
        }

        return moves;
    }

    private static void addKingMoves(int row, int col, ArrayList<String> moves, String piece) {
        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int newRow = row + rowOffsets[i];
            int newCol = col + colOffsets[i];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                String target = board[newRow][newCol];
                if (target.equals("X") || isOpponentPiece(target, piece)) {
                    moves.add("" + (char) ('A' + newCol) + (8 - newRow));
                }
            }
        }
    }

    private static void addQueenMoves(int row, int col, ArrayList<String> moves, String piece) {
        addLinearMoves(row, col, moves, piece, 1, 0);  // Down
        addLinearMoves(row, col, moves, piece, -1, 0); // Up
        addLinearMoves(row, col, moves, piece, 0, 1);  // Right
        addLinearMoves(row, col, moves, piece, 0, -1); // Left
        addLinearMoves(row, col, moves, piece, 1, 1);  // Down-Right
        addLinearMoves(row, col, moves, piece, 1, -1); // Down-Left
        addLinearMoves(row, col, moves, piece, -1, 1); // Up-Right
        addLinearMoves(row, col, moves, piece, -1, -1); // Up-Left
    }

    private static void addRookMoves(int row, int col, ArrayList<String> moves, String piece) {
        addLinearMoves(row, col, moves, piece, 1, 0);  // Down
        addLinearMoves(row, col, moves, piece, -1, 0); // Up
        addLinearMoves(row, col, moves, piece, 0, 1);  // Right
        addLinearMoves(row, col, moves, piece, 0, -1); // Left
    }

    private static void addBishopMoves(int row, int col, ArrayList<String> moves, String piece) {
        addLinearMoves(row, col, moves, piece, 1, 1);  // Down-Right
        addLinearMoves(row, col, moves, piece, 1, -1); // Down-Left
        addLinearMoves(row, col, moves, piece, -1, 1); // Up-Right
        addLinearMoves(row, col, moves, piece, -1, -1); // Up-Left
    }

    private static void addKnightMoves(int row, int col, ArrayList<String> moves, String piece) {
        int[][] offsets = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}};
        for (int[] offset : offsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                String target = board[newRow][newCol];
                if (target.equals("X") || isOpponentPiece(target, piece)) {
                    moves.add("" + (char) ('A' + newCol) + (8 - newRow));
                }
            }
        }
    }

    private static void addPawnMoves(int row, int col, ArrayList<String> moves, String piece, boolean opponent) {
        int direction = opponent ? 1 : -1;
        // Standard move (forward to empty square)
        if (isValidMove(row + direction, col, piece, false)) {
            moves.add("" + (char) ('A' + col) + (8 - (row + direction)));
        }
        // Captures (diagonally to an opponent's piece)
        for (int side : new int[]{-1, 1}) {
            if (isValidMove(row + direction, col + side, piece, true)) {
                moves.add("" + (char) ('A' + (col + side)) + (8 - (row + direction)));
            }
        }
    }

    private static void addLinearMoves(int row, int col, ArrayList<String> moves, String piece, int rowIncrement, int colIncrement) {
        int newRow = row + rowIncrement;
        int newCol = col + colIncrement;
        while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
            String target = board[newRow][newCol];
            if (target.equals("X")) {
                moves.add("" + (char) ('A' + newCol) + (8 - newRow));
            } else {
                if (isOpponentPiece(target, piece)) {
                    moves.add("" + (char) ('A' + newCol) + (8 - newRow));
                }
                break; // Stop at the first piece encountered
            }
            newRow += rowIncrement;
            newCol += colIncrement;
        }
    }

    private static boolean isValidMove(int row, int col, String piece, boolean isCapture) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return false;
        }
        String target = board[row][col];
        if (isCapture) {
            return isOpponentPiece(target, piece);
        } else {
            return target.equals("X");
        }
    }

    private static boolean isOpponentPiece(String target, String piece) {
        return (target.startsWith("O") && !piece.startsWith("O")) || (!target.startsWith("O") && piece.startsWith("O"));
    }
}
/*
 * The main purpose of this class is representing the logic of checker, which utilize the class of Piece, Square and Board.
 * 
 * The board of the game is expressed as a linear array of Squares shown as following:
 * [ 0] [ 1] [ 2] [ 3] [ 4] [ 5] 
 * [ 6] [ 7] [ 8] [ 9] [10] [11] 
 * [12] [13] [14] [15] [16] [17] 
 * [18] [19] [20] [21] [22] [23]
 * [24] [25] [26] [27] [28] [29] 
 * [30] [31] [32] [33] [34] [35] 
 * 
 * There are several magic numbers being used in this class, I would like to explain them first.
 * A valid move could be +5, -5, +7, -7
 * A valid capture could be +10, -10, +14, -14
 * 
 */

public class Game {

    public static final int BOARD_SIZE = 36;
    public static final int EASY_AI = 6;
    public static final int MEDIUM_AI = 7;
    public static final int HARD_AI = 8;
    public static int maxDepth = 0;
    public static int noNodes = 0;
    public static int noPurningMax = 0;
    public static int noPurningMin = 0;
    
   public static final boolean[] VALID_SQUARE = {
        false, true, false, true, false, true,
        true, false, true, false, true, false,
        false, true, false, true, false, true,
        true, false, true, false, true, false,
        false, true, false, true, false, true,
        true, false, true, false, true, false,
    };
    
    public static final double[] POSITION_MULTIPLIER = {
            0, 1.2, 0, 1.2, 0, 1.2,
            1.2, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1.2,
            1.2, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1.2,
            1.2, 0, 1.2, 0, 1.2, 0
    };
    
    public Board checkersBoard;
    public int noHumanPieces, noCompPieces, AIType;

    // Set up the game board
    Game() {
        checkersBoard = new Board(BOARD_SIZE);
        noHumanPieces = 0;
        noCompPieces = 0;
        AIType = EASY_AI;

        Piece tempChecker;

        for (int i = 0; i < checkersBoard.size(); i++) {
            if (i < 12 && VALID_SQUARE[i] == true) {
                tempChecker = new Piece(Piece.COMPUTER_PLAYER);
                checkersBoard.addPiece(i, tempChecker);
                noCompPieces++;
                continue;
            }

            if (i > 23 && VALID_SQUARE[i] == true) {
                tempChecker = new Piece(Piece.HUMAN_PLAYER);
                checkersBoard.addPiece(i, tempChecker);
                noHumanPieces++;
                continue;
            }
        }
    }

    // Copy constructor
    public Game(Game another) {
        this.checkersBoard = new Board(BOARD_SIZE);
        for (int i = 0; i < this.checkersBoard.size(); i++) {
            if (another.getOwnerAt(i) != -1) {
                this.checkersBoard.addPiece(i, new Piece(another.checkersBoard.pieceOwnerAt(i)));
            }
        }
        this.noHumanPieces = another.noHumanPieces;
        this.noCompPieces = another.noCompPieces;
        this.AIType = another.AIType;
    }

    // Returns -1 if the game is still in progress, otherwise return 
    public int isGameEnded(int currentPlayer) {
        String attacksAndMoves = listAllMoves(currentPlayer);
        
        if (noHumanPieces == 0 || noCompPieces == 0 || attacksAndMoves.length() == 0) {
            return 999;
        }
        return -1;
    }

    // Returns the number of player pieces
    public int noHumanPieces() {
        return noHumanPieces;
    }

    // Returns the number of computer pieces
    public int noCompPieces() {
        return noCompPieces;
    }

    // Moves a piece on the board owned by the given player, return false if move is invalid  
    public boolean movePiece(int sourceSquare, int destSquare, int player) {

        if (!isValid(sourceSquare, destSquare, player)) {
            return false;
        }

        Piece movedPiece = (Piece) checkersBoard.removePiece(sourceSquare);

        checkersBoard.addPiece(destSquare, movedPiece);

        // Check if move was a capturing move
        int difference = Math.abs(sourceSquare - destSquare);
        
        // For non-capturing moves, difference will be less than 10 
        if (difference < 8) {
            return true;
        }

        // Capturing move action, remove captured piece
        Piece middlePiece = checkersBoard.removePiece((sourceSquare + destSquare) / 2);
        
        if (middlePiece.getOwner() == Piece.HUMAN_PLAYER) {
            noHumanPieces--;
        } else {
            noCompPieces--;
        }

        return true;
    }

    // Lists all valid moves available to the player
    public String listAllMoves(int player) {
        String legalAttacks = listCaptures(player);
        if (legalAttacks.length() != 0) {
            return legalAttacks;
        }
        String legalMoves = listMoves(player);
        if (legalMoves.length() != 0) {
            return legalMoves;
        }
        return "";
    }
    
    // Check if a move is valid
    public boolean isValid(int source, int dest, int player) {
        
        String move = " " + source + " " + dest + " ";
        String legalMoves = listAllMoves(player);
        
        if (legalMoves.length() != 0) {
            return (" " + legalMoves).contains(move);
        }
        
        return false;
    }

    // lists all captures available to the given player
    // Attacks are listed in the form: <source> + " " + <destination> + " "
    public String listCaptures(int player) {
        String attackList = "";

        for (int i = 0; i < checkersBoard.size(); i++) {
            attackList += listCaptures(i, player);
        }

        return attackList;
    }

    // List all captures available to the given player from the given position on the board. 
    // Captures are listed in the form: <source> + " " + <destination> + " "
    public String listCaptures(int square, int player) {
        String attackList = "";

        // Try 4 possible moves each time, add to list if valid
        int posAttack[] = {square + 14, square - 14, square + 10, square - 10};

        for (int j = 0; j < posAttack.length; j++) {
            if (validCapture(square, posAttack[j], player)) {
                attackList += square + " " + posAttack[j] + " ";
            }
        }
        return attackList;
    }

    // List all non-capturing moves available for the given player
    public String listMoves(int player) {
        String moveList = "";

        //Try 4 possible moves each time, add to list if valid
        for (int i = 0; i < checkersBoard.size(); i++) {
            int posMove[] = {i + 7, i - 7, i + 5, i - 5};

            for (int j = 0; j < posMove.length; j++) {
                if (validMove(i, posMove[j], player)) {
                    moveList += i + " " + posMove[j] + " ";//",";
                }
            }
        }
        return moveList;
    }

    // Check if the capture is valid
    private boolean validCapture(int source, int dest, int player) {
        int tempOwner = checkersBoard.pieceOwnerAt(source);

        // Check owner of the piece and destination square
        if (tempOwner != player
                || checkersBoard.pieceOwnerAt(dest) != Board.EMPTY_SQUARE) {
            return false;
        }

        // Player - starts bottom, moves upwards
        if (player == Piece.HUMAN_PLAYER) {
            if (source - dest > 8) {
                return validAttackUp(source, dest, player);
            } else {
                return false;
            }
        }

        // Computer - starts top, moves downwards
        if (player == Piece.COMPUTER_PLAYER) {
            if (dest - source > 8) {
                return validAttackDown(source, dest, player);
            } else {
                return false;
            }
        }

        return false;
    }

    // Check if the given non-capturing move is valid
    private boolean validMove(int source, int dest, int player) {
        int tempOwner = checkersBoard.pieceOwnerAt(source);

        // Check owner of the piece and destination square
        if (tempOwner != player
                || checkersBoard.pieceOwnerAt(dest) != Board.EMPTY_SQUARE) {
            return false;
        }

        // player - starts bottom, moves upwards
        if (player == Piece.HUMAN_PLAYER) {
            if (source - dest < 8) {
                return validMoveUp(source, dest);
            } else {
                return false;
            }
        }

        // Computer - starts top, moves downwards
        if (player == Piece.COMPUTER_PLAYER) {
            if (dest - source < 8) {
                return validMoveDown(source, dest);
            } else {
                return false;
            }
        }

        return false;
    }

    // Check if the given move upwards on the board is valid
    private boolean validMoveUp(int sourceSquare, int destSquare) {
        int difference = sourceSquare - destSquare;

        // Left-border case
        if (sourceSquare % 6 == 0) {
            return difference == 5;
        }

        // Right-border case
        if (sourceSquare % 6 == 5) {
            return difference == 7;
        }

        return difference == 5 || difference == 7;
    }

    // Check if the given move downwards on the baord is valid
    private boolean validMoveDown(int sourceSquare, int destSquare) {
        int difference = destSquare - sourceSquare;

        // Left-border case
        if (sourceSquare % 6 == 0) {
            return difference == 7;
        }

        // Right-border case
        if (sourceSquare % 6 == 5) {
            return difference == 5;
        }

        return difference == 5 || difference == 7;
    }

    // Check if the capturing move upwards is valid 
    private boolean validAttackUp(int sourceSquare, int destSquare, int player) {
        int difference = sourceSquare - destSquare;
        int midSquare = (sourceSquare + destSquare) / 2;

        if (checkersBoard.pieceOwnerAt(midSquare) == player
                || checkersBoard.pieceOwnerAt(midSquare) == Board.EMPTY_SQUARE) {
            return false;
        }

        // Left-border case
        if (sourceSquare % 6 == 0 || sourceSquare % 6 == 1) {
            return difference == 10;
        }

        // Right-border case
        if (sourceSquare % 6 == 5 || sourceSquare % 6 == 4) {
            return difference == 14;
        }

        return difference == 10 || difference == 14;
    }
    
    // Check if the capturing move downwards is valid 
    private boolean validAttackDown(int sourceSquare, int destSquare, int player) {
        int difference = destSquare - sourceSquare;
        int midSquare = (sourceSquare + destSquare) / 2;

        if (checkersBoard.pieceOwnerAt(midSquare) == player
                || checkersBoard.pieceOwnerAt(midSquare) == Board.EMPTY_SQUARE) {
            return false;
        }

        // Left-border case
        if (sourceSquare % 6 == 0 || sourceSquare % 6 == 1) {
            return difference == 14;
        }

        // Right-border case
        if (sourceSquare % 6 == 5 || sourceSquare % 6 == 4) {
            return difference == 10;
        }

        return difference == 14 || difference == 10;
    }
    
    // Return a move for the selected AI difficulty
    public String AIMove(){
        switch(AIType()){
            case EASY_AI:
                return miniMax(this, EASY_AI);
            case MEDIUM_AI:
                return miniMax(this, MEDIUM_AI);
            case HARD_AI:
                return miniMax(this, HARD_AI);
            default:
                return "";
        }
    }
    
    /*
     * A heuristic evaluation function that takes into account：
     * 1. the number of pieces left for each player
     * 2. the pieces' positions on the board (pieces on the edge are rated higher because they can't be captured)
     * The higher the returned value, the better for the AI and worse for the human player. 
     */
    static double evalGameState(Game game){
        double humanPieceValue = 0;
        double compPieceValue = 0;
        for(int i=0; i< BOARD_SIZE; i++){
            if(!VALID_SQUARE[i]) continue;
            
            double pieceValue = 1;
            pieceValue *= POSITION_MULTIPLIER[i];
            
            if(game.getOwnerAt(i) == Piece.COMPUTER_PLAYER)
                compPieceValue += pieceValue;
            else if(game.getOwnerAt(i) == Piece.HUMAN_PLAYER)
                humanPieceValue += pieceValue;
        }
        return compPieceValue - humanPieceValue;
    }
    
    /*
     * A MiniMax algorithm that finds the best move for the AI. It uses alpha-beta pruning for optimization. 
     * Unrealistic values are provided when calling maxMove(-1000 and 1000)
     */
    private String miniMax(Game game, int depth) {
    	    maxDepth = 0;
    	    noNodes = 0;
    	    noPurningMax = 0;
    	    noPurningMin = 0;
    	    System.out.println("**************************************************************");
        Move temp = maxMove(game, -1000, 1000, 0, depth, "");
        System.out.println("Maximum depth of tree: " + maxDepth);
        System.out.println("Total number of nodes generated: " + noNodes);
        System.out.println("Number of times pruning occurred in the MAX-VALUE function: " + noPurningMax);
        System.out.println("Number of times pruning occurred in the MIN-VALUE function: " + noPurningMin);
        return temp.move;
    }
    
    /*
     * Search and pick the best move for the AI by trying to maximize the result based on evaluation function.
     */
    private Move maxMove(Game game, double alpha, double beta, int currentDepth, int depthLimit, String firstMove) {    
        
        // Return and evaluate if game ended or limit has been reached
        if(game.isGameEnded(Piece.COMPUTER_PLAYER) != -1 || currentDepth >= depthLimit)
            return new Move(firstMove, evalGameState(game));   
        
        // Theoretical best move, -1000 to be replaced
        Move bestMove = new Move("", -1000); 
        
        // Process all possible computer moves
        String moves = game.listAllMoves(Piece.COMPUTER_PLAYER);
        String [] splitMoves = moves.split(" ");
        noNodes = noNodes + splitMoves.length / 2;
        
        for(int i=0; i < splitMoves.length; i+=2){
            Game temp = new Game(game);
            int source = Integer.parseInt(splitMoves[i]);
            int dest = Integer.parseInt(splitMoves[i+1]);
            
            String tempFirstMove = firstMove;
            
            // Remember first move in the sequence
            if(firstMove.equals(""))                
                tempFirstMove = (source + " " + dest + " ");
            
            // Try the move on a theoretical board
            temp.movePiece(source, dest, Piece.COMPUTER_PLAYER);
            
            // Get opponent's best move (worst for AI, best for player)
            Move move = minMove(temp, alpha, beta, (currentDepth+1), depthLimit, tempFirstMove);
            maxDepth = Math.max(maxDepth, currentDepth+1);
            
            // Remember the best move, save best value into alpha
            if(move.value > bestMove.value){
                bestMove = move;
                alpha = move.value;
            }
                
            // Carry out alpha-beta pruning
            if(beta != 1000.0 && move.value > beta){
            		noPurningMax++;
                return bestMove;
            }
        }
        return bestMove;
    }
    
    /*
     * Search and pick the best move for the AI by trying to minimize the result based on evaluation function.
     */  
    private Move minMove(Game game, double alpha, double beta, int currentDepth, int depthLimit, String firstMove) {
        
    		// Return and evaluate if game ended or limit has been reached
        if(game.isGameEnded(Piece.HUMAN_PLAYER) != -1 || currentDepth >= depthLimit)
            return new Move(firstMove, evalGameState(game));
        
        // Theoretical best move, 1000 to be replaced
        Move worstMove = new Move("", 1000); 
        
        // Process all possible human moves
        String moves = game.listAllMoves(Piece.HUMAN_PLAYER);
        String [] splitMoves = moves.split(" ");
        noNodes = noNodes + splitMoves.length / 2;
        
        for(int i=0; i < splitMoves.length; i+=2){
            Game temp = new Game(game);
            int source = Integer.parseInt(splitMoves[i]);
            int dest = Integer.parseInt(splitMoves[i+1]);
            
            // Try the move on a theoretical board
            temp.movePiece(source, dest, Piece.HUMAN_PLAYER);
            
            // Get opponent's best move (best for AI, worst for player)
            Move move = maxMove(temp, alpha, beta, (currentDepth+1), depthLimit, firstMove);
            maxDepth = Math.max(maxDepth, currentDepth + 1);

            // Remember the worst move, save worst value into beta
            if(move.value < worstMove.value){
                worstMove = move;
                beta = move.value;
            }
                
            // Carry out alpha-beta pruning
            if(alpha != -1000.0 && move.value < alpha){
            		noPurningMin++;
                return worstMove;
            }
            
        }        
        return worstMove;
    }
    
     // A Class used by the minimax algorithm to store the move's evaluated value as well as the move itself.
    public static class Move{
        public String move;
        public double value;
        
        Move(String argMoves, double argValue){
            move = argMoves;
            value = argValue;
        }
    }

    // Returns owner of piece at given location
    public int getOwnerAt(int square) {
        return checkersBoard.pieceOwnerAt(square);
    }

    // Changes the AI settings
    public void setAI(int type) {
        AIType = type;
    }

    //Returns the AI settings
    public int AIType() {
        return AIType;
    }

    // Add a piece to the board at given location
    public void addPieceAt(int square, Piece piece) {
        checkersBoard.addPiece(square, piece);
    }
}

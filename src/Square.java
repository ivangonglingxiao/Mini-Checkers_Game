/*
 * A class that represents a square of a game board. The square can contain an object of type Piece.
 */

public class Square {
    
    private Piece piece;
    
    Square() {
    }   
    
    Square(Piece piece) {
        setPiece(piece);
    }
    
    // Put a Piece in the square. return success/failure indicator. 
    public boolean setPiece(Piece piece) {
        if (!this.isEmpty()){
            return false;
        }
        
        this.piece = piece;
        
        return true;        
    }
     
    public Piece getPiece() {
        return piece;
    }
    
    // Check if Square contains a Piece
    public boolean isEmpty() {
        return piece == null;
    }
    
    // Remove any Piece from the Square.
    public void empty() {
        piece = null;
    }
    
    // Return the owner of the Piece that is contained in the square. 
    public int getPieceOwner() {
        if (this.isEmpty()){
            throw new NullPointerException("Square does not contain a Piece.");
        }
        
        return piece.getOwner();
    }
}
/*
 * A checkers piece class, an implementation of class Piece.
*/

public class Piece {

    public static final int HUMAN_PLAYER = 0;
    public static final int COMPUTER_PLAYER = 1;
    public static final int TIE = 2;
    
    private int owner;
    
    Piece() {
        owner = 0;
    }
    
    // constructor that sets owner and type to non-default supplied values.
    Piece(int owner) {
        setOwner(owner);
    }
    
    private void setOwner(int owner){
        this.owner = owner;
    }
    
    public int getOwner(){
        return owner;
    }
    
    
}
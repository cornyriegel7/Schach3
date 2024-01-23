package pack;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    Board board;
    private static int selectedPieceValue;
    public int startSquare;  //Index Startposition vom Move
    public static int xE, yE; //Koordinaten der Maus zum Abrufen fürs board

    public Input(Board pBoard){
        this.board = pBoard;
        selectedPieceValue = 0;
        int xE = 0, yE = 0;
        startSquare = -1; // -1 weil 0 ein Index vom Array ist und startSquare ja erstmal nichts sein soll
    }


    //Wenn die Maus gedrückt wird
    @Override
    public void mousePressed(MouseEvent e) {
        xE = e.getX();  //Zum Abrufen fürs Board
        yE = e.getY();
        startSquare = board.xyToSquare(xE, yE);
        if(board.getPieceFromSquare(startSquare) != 0){
            selectedPieceValue = board.getPieceFromSquare(startSquare);
            board.setSquare(startSquare, 0);
        }
        else
        {
            startSquare = 0;
        }

    }

    //Wenn die Maus gezogen wird
    @Override
    public void mouseDragged(MouseEvent e) {
        xE = e.getX();  //Zum Abrufen fürs Board
        yE = e.getY();
        if(startSquare != 0){
           board.repaint();     //swing sagen, dass es repainten soll
        }
        System.out.println("Dragged"); //Wie oft wird draggen methode ausgeführt

    }

    //Wenn die Maus released wird und ein Piece gehalten wird, setze es bei dem Square wo die Maus ist ab.
    //todo: (solange es valid ist, aber das ist noch nicht einprogrammiert)
    @Override
    public void mouseReleased(MouseEvent e) {

        xE = e.getX();  //Zum Abrufen fürs Board
        yE = e.getY();
        if (board.isEmpty(board.xyToSquare(xE, yE))) { //wenn das feld frei ist
            //if(board.generateMoves(startSquare, board.getSquare(selectedPiece, ))){
            board.setSquare(board.xyToSquare(xE, yE), selectedPieceValue);// der Square von dem du Weg gehst soll 0 gesetzt werden
            startSquare = 0;
            //}
        }
        else{
            board.setSquare(startSquare, selectedPieceValue);
        }
        selectedPieceValue = 0;
        board.repaint();
    }

    public int getstartSquare(){
        return startSquare;
    }

    public static int getSelectedPieceValue(){
        return  selectedPieceValue;
    }

    public static int getxE(){
        return xE;
    }

    public static int getyE(){
        return yE;
    }
}

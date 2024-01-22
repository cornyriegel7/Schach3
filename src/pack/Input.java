package pack;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    Board board;
    int selectedSquare; //Index des SquareArrays, das grade selected ist
    int pStart;  //Startposition vom Move
    int xE; //Koordinaten der Maus zum Abrufen fürs board
    int yE; //Koordinaten der Maus zum Abrufen fürs board

    public Input(Board pBoard){
        this.board = pBoard;
        selectedSquare = 0;
        int xE = 0;
        int yE = 0;
        int pStart = -1; // -1 weil 0 ein Index vom Array ist und pStart ja erstmal nichts sein soll
    }


    //Wenn die Maus gedrückt wird
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("MousePressed");//Test
        pStart = board.xyToSquare(e.getX(), e.getY());
        if(board.getSquare(pStart) != 0){
            selectedSquare = pStart;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        xE = e.getX() - board.titleSize/2;  //Zum Abrufen fürs Board
        yE = e.getY() - board.titleSize/2;
        if(selectedSquare != 0){
           board.repaint();     //swing sagen, dass es repainten soll
        }
    }

    //Wenn die Maus released wird und ein Piece gehalten wird, setze es bei dem Square wo die Maus ist ab. (solange es valid ist, aber das ist noch nicht einprogrammiert)
    @Override
    public void mouseReleased(MouseEvent e) {

        System.out.println("MouseReleased");//Test
        if (board.getSquare(selectedSquare) != 0) {
            //if(board.generateMoves(pStart, board.getSquare(selectedPiece, ))){
            board.setSquare(board.xyToSquare(xE, yE), board.getSquare(selectedSquare)); // der Square zu dem du hingesht soll den Wert von der FIgur bekommen die da hingeht
            board.setSquare(pStart, 0);// der Square von dem du Weg gehst solll 0 gesetzt werden
            //pStart = -1;
            //selectedSquare = 0;
            board.repaint();
            //}
        }

    }

    public int getSelectedSquare(){
        return selectedSquare;
    }

    public int getpStart(){
        return pStart;
    }

    public int getxE(){
        return xE;
    }

    public int getyE(){
        return yE;
    }
}

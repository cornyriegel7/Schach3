package pack;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

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
            board.setSquare(startSquare,0);
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
    @Override
    public void mouseReleased(MouseEvent e) {

        xE = e.getX();  //Zum Abrufen fürs Board
        yE = e.getY();
        int endsquare = board.xyToSquare(xE, yE);
        int farbe = selectedPieceValue / Math.abs(selectedPieceValue);
        LinkedList<Integer> eigenePositionen = farbe == Piece.white ? board.whitePositions : board.blackPositions;
        LinkedList<int[]> vonAnderenAngegriffen = farbe == Piece.white ? board.attackedByBlackPositions : board.attackedByWhitePositions;
        LinkedList<int[]> vonEigenenAngegriffen = farbe == Piece.black ? board.attackedByBlackPositions : board.attackedByWhitePositions;
        int[][] legalmoves = board.getLegalMoves(selectedPieceValue,startSquare,board.giveBoard(),vonAnderenAngegriffen,vonEigenenAngegriffen,eigenePositionen);
        for (int i = 0; i < legalmoves.length; i++) {
            if(legalmoves[i][1] == endsquare)
            {
                board.execMove(selectedPieceValue,startSquare,endsquare);
                selectedPieceValue = 0;
                board.repaint();
                return;
            }
        }
        //Das hier ist die Aktion die geändert werden muss, zurzeit wird einfach nen neues Piece gespawnt wenn der MOve nd ok is
        board.setSquare(startSquare, selectedPieceValue);
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

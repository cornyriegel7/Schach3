package pack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class Input extends MouseAdapter {

    Controller c;
    private static int selectedPieceValue;
    public int startSquare;  //Index Startposition vom Move
    public static int xE, yE;//Koordinaten der Maus zum Abrufen fürs board

    public static int[][] legalMoves;
    private int endSquare;

    boolean active;

    public Input(Controller pController){
        c = pController;
        selectedPieceValue = 0;
        int xE = 0, yE = 0;
        startSquare = -1;// -1 weil 0 ein Index vom Array ist und startSquare ja erstmal nichts sein soll
        endSquare = -1;
        legalMoves = new int[0][0];
        active = true;
    }


    //Wenn die Maus gedrückt wird
    @Override
    public void mousePressed(MouseEvent e) {
        if(active) {
            xE = e.getX();  //Zum Abrufen fürs Board
            yE = e.getY();
            startSquare = c.board.xyToSquare(xE, yE);
            if (c.board.getPieceFromSquare(startSquare) != 0) {
                selectedPieceValue = c.board.getPieceFromSquare(startSquare);
                int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                if (pieceColor == c.dran) {
                    LinkedList<Integer> eigenePositionen = pieceColor == Piece.white ? c.board.whitePositions : c.board.blackPositions;
                    LinkedList<int[]> vonAnderenAngegriffen = pieceColor == Piece.white ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                    LinkedList<int[]> vonEigenenAngegriffen = pieceColor == Piece.black ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                    legalMoves = c.board.generateLegalMoves(startSquare, selectedPieceValue, c.board.giveBoard(), vonEigenenAngegriffen, vonAnderenAngegriffen, eigenePositionen,c.board.specialMovePositions);
                    if (legalMoves.length == 0 && c.board.isCheckMate(c.dran)) {
                        boolean hasWhiteLost = c.dran == Piece.white;
                        c.view.checkMateMessage(hasWhiteLost);
                    }
                    c.board.setSquare(startSquare, 0);
                } else {
                    selectedPieceValue = 0;
                    startSquare = -1;
                }
            } else {
                startSquare = -1;
            }
        }
    }

    //Wenn die Maus gezogen wird
    public void mouseDragged(MouseEvent e) {
        if(active) {
            if (selectedPieceValue != 0) {
                xE = e.getX();
                yE = e.getY();

                if (startSquare != 0) {
                    c.boardGUI.repaint();     // Swing sagen, dass es repainten soll
                    // aber das hauptproblem ist, dass mouseDragged zu wenig oft aufgerufen wird
                    //HAAALLOO
                }
            }
        }
    }

    //Wenn die Maus released wird und ein Piece gehalten wird, setze es bei dem Square wo die Maus ist ab.
    @Override
    public void mouseReleased(MouseEvent e) {
        if(active) {
            if (selectedPieceValue != 0) {
                xE = e.getX();  //Zum Abrufen fürs Board
                yE = e.getY();
                endSquare = c.board.xyToSquare(xE, yE);
                for (int i = 0; i < legalMoves.length; i++) {
                    if (legalMoves[i][1] == endSquare) {
                        c.board.execMove(legalMoves[i][0], legalMoves[i][1], legalMoves[i][2]);
                        //legalMoves = null;
                        int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                        c.dran = pieceColor == Piece.white ? Piece.black : Piece.white;
                        selectedPieceValue = 0;
                        c.boardGUI.repaint();
                        System.out.println(c.bot.evaluation(c.board.Square,c.board.whitePositions,c.board.blackPositions));
                        return;
                        //board.view.c.chatClient.setIntArray(board.giveBoard()); //intarray wird verschickt

                    }
                }
                if (startSquare != -1) {
                    // figur wird an die stelle zurückgesetzt und repaint
                    c.board.setSquare(startSquare, selectedPieceValue);
                    selectedPieceValue = 0;
                    c.boardGUI.repaint(0, 0, 3000, 3000);
                }

            }
        }
    }

    public void setActive(boolean active) {
        this.active = active;
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

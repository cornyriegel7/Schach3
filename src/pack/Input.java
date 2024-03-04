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
    boolean dragpiece;

    public Input(Controller pController){
        c = pController;
        selectedPieceValue = 0;
        int xE = 0, yE = 0;
        startSquare = -1;// -1 weil 0 ein Index vom Array ist und startSquare ja erstmal nichts sein soll
        endSquare = -1;
        legalMoves = new int[0][0];
        active = true;
        dragpiece = false;
    }


    //Wenn die Maus gedrückt wird
    @Override
    public void mousePressed(MouseEvent e) {
        if(active) {
            //Koords updaten
            xE = e.getX();
            yE = e.getY();
            //Startsquare = koords
            int pKoords = c.board.xyToSquare(xE, yE);

            //Ist ein Piece selected?
            if(selectedPieceValue == 0){
                //KEIN PIECE SELECTED

                //IST DAS FELD ÜBERHAUPT BELEGT????
                if (c.board.getPieceFromSquare(pKoords) != 0) {
                    //DANN AKTUALISIER    !!!VORERST!!! MAL DAS SELECTED PIECE
                    //ALSO STARTSQUARE VOM PIECE UND DEN VALUE
                    selectedPieceValue = c.board.getPieceFromSquare(pKoords);
                    startSquare = pKoords;

                    //SOOOO MAL GUCKEN OB DAS PIECE AUCH DIE RICHTIGE FARBE HAT BZW. DRAN IST
                    int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                    //IST ES DRAN??
                    if (pieceColor == c.dran) {

                        //JAAAAAAAA!!! DANN HABEN WIR RICHTIG AUSGEWÄHLT

                        //BERECHNE DIE LEGAL MOVESSSS

                        LinkedList<Integer> eigenePositionen = pieceColor == Piece.white ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<int[]> vonAnderenAngegriffen = pieceColor == Piece.white ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        LinkedList<int[]> vonEigenenAngegriffen = pieceColor == Piece.black ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        legalMoves = c.board.generateLegalMoves(startSquare, selectedPieceValue, c.board.giveBoard(), vonEigenenAngegriffen, vonAnderenAngegriffen, eigenePositionen,c.board.specialMovePositions);
                        //CHECKMATE??!??!?!?!
                        if (legalMoves.length == 0 && c.board.isCheckMate(c.dran)) {
                            boolean hasWhiteLost = c.dran == Piece.white;
                            c.view.checkMateMessage(hasWhiteLost);
                        }

                        //SETZE SCHONMAL DAS STARTSQUARE AUF 0 DAMIT KEINE FIGUR MEHR DRAUF IST (muss aber in dragged eig)
                        c.board.setSquare(startSquare, 0);
                    } else {
                        //FUCK ES HAT DIE FALSCHE FARBE DANN HABEN WIR QUASI KEIN PIECE
                        selectedPieceValue = 0;
                        startSquare = -1;
                    }
                } else {
                    //ES IST KEINE FIGUR DA DU DUMBO
                    //vorher
                    //startSquare = -1;
                    pKoords = -1;
                }

            }
            else if(selectedPieceValue != 0)
            {
                //FUCK EIN PIECE IST SELECTED

                int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);

                //IST AUF DEM GEKLICKTEN FELD EIN PIECE UNSERES TEAMS??????
                if (c.board.getPieceFromSquare(pKoords) != 0 && pieceColor == c.dran) {
                    //JAAA???? DANN WÄHLE NUN DAS PIECE AUS
                    //PIECE VALUE AKTuALISIEREN
                    selectedPieceValue = c.board.getPieceFromSquare(pKoords);
                    //STARTSQUARE AKTUALISIEREN
                    startSquare = pKoords;
                }
                //NEIN? dann juckt nicht.. (glaub ich)
            }
        }
    }

    //Wenn die Maus gezogen wird
    public void mouseDragged(MouseEvent e) {
        if(active) {
            //IST EIN PIECE AUSGEWÄHLT????
            if (selectedPieceValue != 0) {
                //JA?? DANN AKTUALISIER DIE KOORDS
                xE = e.getX();
                yE = e.getY();

                //ES WIRD !!!!EIN PIECE!!! GEDRAGGTTT
                dragpiece = true;

//                if (startSquare != 0) {
                    c.boardGUI.repaint();     // Swing sagen, dass es repainten soll

//                }
            }
        }
    }

    //Wenn die Maus released wird und ein Piece gehalten wird, setze es bei dem Square wo die Maus ist ab.
    @Override
    public void mouseReleased(MouseEvent e) {
        if(active) {
            //IST EIN PIECE AUSGEWÄHLT????
            if(selectedPieceValue != 0) {

                //FÜRS BOARD MHMHM
                xE = e.getX();
                yE = e.getY();

                //ERSTELLE EIN ENDSQUARE AN DEN KOORDS
                endSquare = c.board.xyToSquare(xE, yE);

                //JA????? DURCH KLICKEN ODER DRAGGEN??
                if (dragpiece == true) {
                    //DURCH DRAGGEN ALSO...
                    //JA DRAGGEN KANN SCHONMAL WIEDER FALSE SEIN
                    dragpiece = false;

                    for (int i = 0; i < legalMoves.length; i++) {
                        //WENN DAS ENDSQUARE EIN LEGALMOVE IST DANN MACHE DEN MOVE
                        if (legalMoves[i][1] == endSquare) {
                            c.board.execMove(legalMoves[i][0], legalMoves[i][1], legalMoves[i][2]);
                            //legalMoves = null;
                            int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                            c.dran = pieceColor == Piece.white ? Piece.black : Piece.white;

                            //Resette alles, weil der Move ausgeführt wurde
                            selectedPieceValue = 0;
                            System.out.println("selectedPieceValue = 0 DRAGGEN SCHLEIFE");
                            startSquare = -1; //???????????????????????????????????????????????????????????????? EIG MÜSSTE


                            //REPAINTE DU ...
                            c.boardGUI.repaint();
                            return;
                            //board.view.c.chatClient.setIntArray(board.giveBoard()); //intarray wird verschickt

                        }
                    }

                    //ES GAB KEINEN LEGALMOVE WEIL DIE FOR SCHLEIFE DURCH IST UND NICHT RETURNT WURDE

                    if (startSquare != -1) {
                        //DANN SETZE JETZT DIE FIGUR ZURÜCL
                        // figur wird an die stelle zurückgesetzt und repaint
                        c.board.setSquare(startSquare, selectedPieceValue);
                        selectedPieceValue = 0;
                        System.out.println("selectedPieceValue = 0 DRAGGEN SCHLEIFE DURCH");
                        c.boardGUI.repaint();
                    }
                }
                else
                {
                    //DURCH KLICKEN ALSOOO!!!
                    //DIE FRAGE IST....HAST DU ES GERADE ERST AUSGEWÄHLT ODER WILLST DU HIERHIN MOVEN??
                    //DAS PRÜFEN WIR MAL...
                    //SO WENN DU ES GERADE AUSGEWÄHLT HAST HAT SICH DEINE KOORDINATE NICHT VERÄNDERT
                    // --> START UND ENDSQUARE SIND GLEICH RICHTIG?

                    if(startSquare != endSquare){
                        //SIE SIND NICHT GLEICH --> DU WILLST ZU ENDSQUARE MOVEN
                        for (int i = 0; i < legalMoves.length; i++) {
                            //WENN DAS ENDSQUARE EIN LEGALMOVE IST DANN MACHE DEN MOVE
                            if (legalMoves[i][1] == endSquare) {
                                c.board.execMove(legalMoves[i][0], legalMoves[i][1], legalMoves[i][2]);
                                //legalMoves = null;
                                int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                                c.dran = pieceColor == Piece.white ? Piece.black : Piece.white;

                                //Resette alles, weil der Move ausgeführt wurde
                                selectedPieceValue = 0;
                                System.out.println("selectedPieceValue = 0 KLICKEN SCHLEIFE");


                                //REPAINTE DU ...
                                c.boardGUI.repaint();
                                return;
                                //board.view.c.chatClient.setIntArray(board.giveBoard()); //intarray wird verschickt

                            }
                        }

                        //ES GAB KEINEN LEGALMOVE WEIL DIE FOR SCHLEIFE DURCH IST UND NICHT RETURNT WURDE

                        if (startSquare != -1) {
                            //DANN SETZE JETZT DIE FIGUR ZURÜCL
                            // figur wird an die stelle zurückgesetzt und repaint
                            c.board.setSquare(startSquare, selectedPieceValue);
                            selectedPieceValue = 0;
                            System.out.println("selectedPieceValue = 0 KLICKEN SCHLEIFE DURCH");
                            startSquare = -1; //??????????????????????????????????????????????????????????? EIG MÜSSTE
                            c.boardGUI.repaint();
                        }
                    }
                    else{
                        // DU HAST ES GRADE ERST AUSGEWÄHLT mhm.
                    }

                }



            }
            else{}
        }
    }

    public void mouseClicked(MouseEvent e){
        dragpiece = false;
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

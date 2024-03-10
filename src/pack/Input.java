package pack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class Input extends MouseAdapter {

   //Hier Richard deine Methode für den Bot:  System.out.println(c.bot.evaluation(c.board.Square,c.board.whitePositions,c.board.blackPositions));

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

            //Ist ein Piece selected?
            if(selectedPieceValue == 0){
                //KEIN PIECE SELECTED

                startSquare = c.board.xyToSquare(xE, yE);

                //IST DAS FELD ÜBERHAUPT BELEGT????
                if (c.board.getPieceFromSquare(startSquare) != 0) {
                    //DANN AKTUALISIER    !!!VORERST!!! MAL DAS SELECTED PIECE
                    //ALSO STARTSQUARE VOM PIECE UND DEN VALUE
                    selectedPieceValue = c.board.getPieceFromSquare(startSquare);

                    //SOOOO MAL GUCKEN OB DAS PIECE AUCH DIE RICHTIGE FARBE HAT BZW. DRAN IST
                    int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                    //IST ES DRAN??
                    if (pieceColor == c.dran) {

                        //JAAAAAAAA!!! DANN HABEN WIR RICHTIG AUSGEWÄHLT

                        //BERECHNE DIE LEGAL MOVESSSS

                        LinkedList<Integer> eigenePositionen = pieceColor == Piece.white ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<Integer> gegnerPositionen = pieceColor == Piece.black ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<int[]> vonAnderenAngegriffen = pieceColor == Piece.white ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        LinkedList<int[]> vonEigenenAngegriffen = pieceColor == Piece.black ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        legalMoves = c.board.generateLegalMoves(startSquare, selectedPieceValue, c.board.giveBoard(), vonEigenenAngegriffen, vonAnderenAngegriffen, eigenePositionen,c.board.specialMovePositions);
                        //CHECKMATE??!??!?!?!
                        if (legalMoves.length == 0) {
                            if(c.board.isCheckMate(c.dran)) {
                                boolean hasWhiteLost = c.dran == Piece.white;
                                c.view.checkMateMessage(hasWhiteLost);
                            }
                            else if(c.board.getAttacksOnKing(c.board.getKingPos(c.board.Square,eigenePositionen),c.board.Square,eigenePositionen,vonAnderenAngegriffen).length == 0)
                            {
                                //HIER FENSTER FÜR UNENTSCHIEDEN HIN?
                                System.out.println("PATTPATTPATTPATTPATT");
                            }
                        }
                        else if(c.board.isTie(eigenePositionen,gegnerPositionen,c.board.Square))
                        {
                            //HIER FENSTER FÜR UNENTSCHIEDEN HIN?
                            System.out.println("PATTPATTPATTPATTPATT");
                        }




                        //SETZE SCHONMAL DAS STARTSQUARE AUF 0 DAMIT KEINE FIGUR MEHR DRAUF IST (muss aber in dragged eig)
                        //c.board.setSquare(startSquare, 0);
                    } else {
                        //FUCK ES HAT DIE FALSCHE FARBE DANN HABEN WIR QUASI KEIN PIECE
                        selectedPieceValue = 0;
                        startSquare = -1;
                    }
                } else {
                    //ES IST KEINE FIGUR DA DU DUMBO
                    //vorher
                    startSquare = -1;
                }

            }
            else if(selectedPieceValue != 0)
            {
                //FUCK EIN PIECE IST SCHON SELECTED!!! IST EINS DRAUF? MÜSSEN WIR EIN ANDERES AUSWÄHLEN ODER IST ES EIN GEGNERISCHES DRAUF?
                //(GEGNERISCHE JUCKEN NICHT, WEIL DI SPÄTER BEI MOUSE RELEASED EH GESCHLAGEN WERDEN)
                //HOL DIR DAS PIECE AUF DEM FElD
                int tempSelectedPieceValue = c.board.getPieceFromSquare(c.board.xyToSquare(xE, yE));

                //IST AUF DEM GEKLICKTEN FELD EIN PIECE
                if(tempSelectedPieceValue != 0){
                    //JA DA IST EINS
                    int selectedPieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                    int tempPieceColour = tempSelectedPieceValue / Math.abs(tempSelectedPieceValue);
                    //IST ES IN UNSEREM TEAM?
                    if(selectedPieceColor == tempPieceColour)
                    {
                        //JAAA IST ES...
                        //DANN SETZE DAS ALTE PIECE ZURÜCk (das "fliegt" nämlich grad nur überm Feld bzw. ist nur in Input gespeichert. Muss also wieder in board gespeichert werden)
                        c.board.setSquare(startSquare, selectedPieceValue);

                        //DANN WÄHLE NUN DAS PIECE AUS

                        //WÄHLE DAS NEUE PIECE AUSUUUSSUSUUSSSS
                        //STARTSQUARE AKTUALISIEREN
                        startSquare = c.board.xyToSquare(xE, yE);
                        //PIECE VALUE AKTuALISIEREN (mit Startsquare)
                        selectedPieceValue = c.board.getPieceFromSquare(startSquare);
                        //STARTSQUARE AKTUALISIEREN


                        //BERECHNE DIE LEGAL MOVESSSS DES NEU AUSGEWÄHLTEN PIECESS
                        LinkedList<Integer> eigenePositionen = tempPieceColour == Piece.white ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<Integer> gegnerPositionen = tempPieceColour == Piece.black ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<int[]> vonAnderenAngegriffen = tempPieceColour == Piece.white ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        LinkedList<int[]> vonEigenenAngegriffen = tempPieceColour == Piece.black ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        legalMoves = c.board.generateLegalMoves(startSquare, selectedPieceValue, c.board.giveBoard(), vonEigenenAngegriffen, vonAnderenAngegriffen, eigenePositionen,c.board.specialMovePositions);

                        if (legalMoves.length == 0) {
                            if(c.board.isCheckMate(c.dran)) {
                                boolean hasWhiteLost = c.dran == Piece.white;
                                c.view.checkMateMessage(hasWhiteLost);
                            }
                            else if(c.board.getAttacksOnKing(c.board.getKingPos(c.board.Square,eigenePositionen),c.board.Square,eigenePositionen,vonAnderenAngegriffen).length == 0)
                            {
                                //HIER FENSTER FÜR UNENTSCHIEDEN HIN?
                                System.out.println("PATTPATTPATTPATTPATT");
                            }
                        }
                        else if(c.board.isTie(eigenePositionen,gegnerPositionen,c.board.Square))
                        {
                            //HIER FENSTER FÜR UNENTSCHIEDEN HIN?
                            System.out.println("PATTPATTPATTPATTPATT");
                        }

                        c.boardGUI.repaint();
                    }
                }
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
                //SETZE SCHONMAL STARTSQUARE AUF 0 (sonst wird beim draggen ein altes piece angezeigt :( )
                c.board.setSquare(startSquare, 0);

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
                    //System.out.println("DRAGRELEASED UND MOVED");
                    //DURCH DRAGGEN ALSO...
                    //JA DRAGGEN KANN SCHONMAL WIEDER FALSE SEIN
                    dragpiece = false;

                    for (int i = 0; i < legalMoves.length; i++) {
                        //WENN DAS ENDSQUARE EIN LEGALMOVE IST DANN MACHE DEN MOVE
                        if (legalMoves[i][1] == endSquare) {




                            //move ausführung
                            c.board.execMove(legalMoves[i][0], legalMoves[i][1], legalMoves[i][2]);


                            if(c.dran == 1) {
                                //c.board.printMove(c.bot.getMove(c.board.Square, c.dran * -1, enemyPos, ownPos, enemyAttacked, ownAttacked, c.board.specialMovePositions));
                            }


                            //legalMoves = null;
                            int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                            c.dran = pieceColor == Piece.white ? Piece.black : Piece.white;



                            //Resette alles, weil der Move ausgeführt wurde
                            selectedPieceValue = 0;
                            //System.out.println("selectedPieceValue = 0 DRAGGEN SCHLEIFE");
                            startSquare = -1; //???????????????????????????????????????????????????????????????? EIG MÜSSTE



                            System.out.println("MOVEEEE!!");

                            //REPAINTE DU ...
                            c.boardGUI.repaint();
                            if(c.view.getPickedMode()==2)
                            c.chatClient.nachrichtVersenden(c.view.intArrayToString(c.board.Square)); //intarray wird verschickt
                            //Bei Online oder gegen Bot setze active auf false nach einem Move
                            if(c.view.getPickedMode() == 2) {
                                active = false;
                            }
                            else if(c.view.getPickedMode() == 3) {
                                c.vsBot();
                            }
                            return;
                        }
                    }

                    //ES GAB KEINEN LEGALMOVE WEIL DIE FOR SCHLEIFE DURCH IST UND NICHT RETURNT WURDE

                    if (startSquare != -1) {
                        //DANN SETZE JETZT DIE FIGUR ZURÜCL
                        // figur wird an die stelle zurückgesetzt und repaint
                        c.board.setSquare(startSquare, selectedPieceValue);
                        selectedPieceValue = 0;
                        //System.out.println("selectedPieceValue = 0 DRAGGEN SCHLEIFE DURCH");
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

                        //METHODEN FÜR MOVEN DURCH KLICKEN
                        //SIE SIND NICHT GLEICH --> DU WILLST ZU ENDSQUARE MOVEN
                        //System.out.println("KLICKRELEASED UND MOVED");
                        for (int i = 0; i < legalMoves.length; i++) {
                            //WENN DAS ENDSQUARE EIN LEGALMOVE IST DANN MACHE DEN MOVE
                            if (legalMoves[i][1] == endSquare) {
                                c.board.execMove(legalMoves[i][0], legalMoves[i][1], legalMoves[i][2]);
                                legalMoves = null;
                                int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                                c.dran = pieceColor == Piece.white ? Piece.black : Piece.white;

                                //Resette alles, weil der Move ausgeführt wurde
                                selectedPieceValue = 0;
                                startSquare = -1;
                                //System.out.println("selectedPieceValue = 0 KLICKEN SCHLEIFE");

                                System.out.println("MOVEEEE!!");
                                System.out.println(c.view.getPickedMode());
                                //Bei Online oder gegen Bot setze active auf false nach einem Move (Bot 3, Online 2)


                                //REPAINTE DU ...
                                System.out.println("Paint Component aufgerufen");
                                c.boardGUI.repaint();


                                //Bei Online oder gegen Bot setze active auf false nach einem Move
                                if(c.view.getPickedMode() == 2) {
                                    active = false;
                                    c.chatClient.nachrichtVersenden(c.view.intArrayToString(c.board.Square)); //intarray wird verschickt
                                }
                                else if(c.view.getPickedMode() == 3) {
                                    c.vsBot();
                                }

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
                            startSquare = -1; //??????????????????????????????????????????????????????????? EIG MÜSSTE
                            c.boardGUI.repaint();
                        }
                    }


                    //METHODEN FÜR NUR AUSWÄHLEN DURCH KLICKEN
                    else{
                        //SETZE DAS STARTSQUARE SCHONMAL 0 damit kein PIECE MEHR DRAUF IST
                        c.board.setSquare(startSquare, 0);
                        //System.out.println("KLICKRELEASED UND AUSGEWÄHLT");
                        //UM DIE PIECES ZU IN DIE MITTE ZU PAINTEN
                        //System.out.println("STARTSQUARE: " + startSquare + "    X: " + xE + "    Y: " + yE);
                        xE = c.boardGUI.squareToX(startSquare) + 40;
                        yE = c.boardGUI.squareToY(startSquare) + 40;
                        //System.out.println("STARTSQUARE: " + startSquare + "    X: " + xE + "    Y: " + yE);
                        c.boardGUI.repaint();
                    }
                }
            }
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

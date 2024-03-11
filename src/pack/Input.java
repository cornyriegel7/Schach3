package pack;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class Input extends MouseAdapter {
    Controller c;
    private static int selectedPieceValue;
    public int startSquare;  //Index Startposition vom Move
    public static int xE, yE; //Koordinaten der Maus zum Abrufen fürs board
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
            //Koordinaten updaten
            xE = e.getX();
            yE = e.getY();

            //Ist ein Piece selected?
            if(selectedPieceValue == 0){
                //kein piece selected

                startSquare = c.board.xyToSquare(xE, yE);

                //ist das feld überhaupt belegt?
                if (c.board.getPieceFromSquare(startSquare) != 0) {
                    //aktualisierung des selectedpieces (vorerst)
                    selectedPieceValue = c.board.getPieceFromSquare(startSquare);

                    //hat das piece die richtige farbe
                    int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                    //ist es dran?
                    if (pieceColor == c.dran) {
                        //berechne die legal moves

                        LinkedList<Integer> eigenePositionen = pieceColor == Piece.white ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<Integer> gegnerPositionen = pieceColor == Piece.black ? c.board.whitePositions : c.board.blackPositions;
                        LinkedList<int[]> vonAnderenAngegriffen = pieceColor == Piece.white ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        LinkedList<int[]> vonEigenenAngegriffen = pieceColor == Piece.black ? c.board.attackedByBlackPositions : c.board.attackedByWhitePositions;
                        legalMoves = c.board.generateLegalMoves(startSquare, selectedPieceValue, c.board.giveBoard(), vonEigenenAngegriffen, vonAnderenAngegriffen, eigenePositionen,c.board.specialMovePositions);
                        //CHECKMATE
                        if (legalMoves.length == 0) {
                            if(c.board.isCheckMate(c.dran)) {
                                boolean hasWhiteLost = c.dran == Piece.white;
                                c.view.checkMateMessage(hasWhiteLost);
                            }
                            else if(c.board.getAttacksOnKing(c.board.getKingPos(c.board.Square,eigenePositionen),vonAnderenAngegriffen).length == 0)
                            {
                                c.view.staleMateMessage();
                            }
                        }
                        else if(c.board.isTie(eigenePositionen,gegnerPositionen,c.board.Square))
                        {
                            c.view.staleMateMessage();
                        }
                    } else {
                        //falsche farbe
                        selectedPieceValue = 0;
                        startSquare = -1;
                    }
                } else {
                   //keine figur vorhanden
                    startSquare = -1;
                }

            }
            else if(selectedPieceValue != 0)
            {
                //ein piece ist schon selected! müssen wir ein anderes auswählen oder ist es ein gegnerisches drauf?
                int tempSelectedPieceValue = c.board.getPieceFromSquare(c.board.xyToSquare(xE, yE));

                if(tempSelectedPieceValue != 0){
                    int selectedPieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                    int tempPieceColour = tempSelectedPieceValue / Math.abs(tempSelectedPieceValue);
                    if(selectedPieceColor == tempPieceColour)
                    {
                        c.board.setSquare(startSquare, selectedPieceValue);
                        //startsquare aktualisierung
                        startSquare = c.board.xyToSquare(xE, yE);
                        selectedPieceValue = c.board.getPieceFromSquare(startSquare);

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
                            else if(c.board.getAttacksOnKing(c.board.getKingPos(c.board.Square,eigenePositionen),vonAnderenAngegriffen).length == 0)
                            {

                            }
                        }
                        else if(c.board.isTie(eigenePositionen,gegnerPositionen,c.board.Square))
                        {

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
            if (selectedPieceValue != 0) {
                xE = e.getX();
                yE = e.getY();

                dragpiece = true;
                c.board.setSquare(startSquare, 0);
                c.boardGUI.repaint();
            }
        }
    }

    //Wenn die Maus released wird und ein Piece gehalten wird, setze es bei dem Square wo die Maus ist ab.
    @Override
    public void mouseReleased(MouseEvent e) {
        if(active) {
            //ist ein piece ausgewählt?
            if(selectedPieceValue != 0) {

                //fürs board
                xE = e.getX();
                yE = e.getY();

                endSquare = c.board.xyToSquare(xE, yE);

                if (dragpiece == true) {
                    dragpiece = false;

                    for (int i = 0; i < legalMoves.length; i++) {
                        //wenn das endsquare ein legalmove ist dann mache den move
                        if (legalMoves[i][1] == endSquare) {

                            //move ausführung
                            c.board.execMove(legalMoves[i][0], legalMoves[i][1], legalMoves[i][2]);

                            int pieceColor = selectedPieceValue / Math.abs(selectedPieceValue);
                            c.dran = pieceColor == Piece.white ? Piece.black : Piece.white;

                            //Resette alles, weil der Move ausgeführt wurde
                            selectedPieceValue = 0;
                            startSquare = -1;

                            c.boardGUI.repaint();
                            if(c.view.getPickedMode() == 2) {
                                active = false;
                                c.chatClient.nachrichtVersenden(c.view.intArrayToString(c.board.Square)); //intarray wird verschickt
                            }
                            else if(c.view.getPickedMode() == 3) {
                                c.vsBot();
                            }
                            return;
                        }
                    }


                    if (startSquare != -1) {
                        // figur wird an die stelle zurückgesetzt und repaint
                        c.board.setSquare(startSquare, selectedPieceValue);
                        selectedPieceValue = 0;
                        c.boardGUI.repaint();
                    }
                }
                else
                {

                    if(startSquare != endSquare){

                        //METHODEN FÜR MOVEN DURCH KLICKEN
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

                                c.boardGUI.repaint();

                                //Bei Online oder gegen Bot setze active auf false nach einem Move (Bot 3, Online 2)
                                if(c.view.getPickedMode() == 2) {
                                    active = false;
                                    c.chatClient.nachrichtVersenden(c.view.intArrayToString(c.board.Square)); //intarray wird verschickt
                                }
                                else if(c.view.getPickedMode() == 3) {
                                    c.vsBot();
                                }
                                c.boardGUI.repaint();
                                return;
                            }
                        }

                        if (startSquare != -1) {
                            // figur wird an die stelle zurückgesetzt und repaint
                            c.board.setSquare(startSquare, selectedPieceValue);
                            selectedPieceValue = 0;
                            startSquare = -1;
                            c.boardGUI.repaint();
                        }
                    }
                    //METHODEN FÜR NUR AUSWÄHLEN DURCH KLICKEN
                    else{
                        c.board.setSquare(startSquare, 0);
                        xE = c.boardGUI.squareToX(startSquare) + 40;
                        yE = c.boardGUI.squareToY(startSquare) + 40;
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

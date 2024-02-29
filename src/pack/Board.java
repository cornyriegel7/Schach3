package pack;
import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

import static pack.Piece.pawn;

public class Board {

    Controller c;
    //Gameplay Zeug
    public final int[] Square;
    private static final int leeresFeld = 0;
    static int[][] distancesToEdge;

    static final int enPassantInt = 11;
    static int oben = -8, unten = 8,  rechts = 1, links = -1, obenrechts = -7, untenlinks = 7, obenlinks = -9, untenrechts = 9;
    
    static final int[] directions = {oben, unten,
            rechts, links,
            untenlinks, obenrechts,
            untenrechts, obenlinks};
    LinkedList<int[]> attackedByWhitePositions, attackedByBlackPositions;
    LinkedList<Integer>  whitePositions, blackPositions;
    ArrayList<Integer> specialMovePositions;

    public Board(Controller pController){

        c = pController;

        Square = new int[64]; //brett als eindimensionales array (von oben links nach unten rechts) ist später praktisch
        initalizeSquare();

        distancesToEdge = getDistanceToEdges();
        whitePositions= getPositions(Piece.white, Square);
        blackPositions = getPositions(Piece.black, Square);
        attackedByWhitePositions = new LinkedList<>();
        attackedByBlackPositions = new LinkedList<>();
        //Positionen an denen Specialmoves theorethisch moeglich waeren -> Rochade und spaeter En passant
        specialMovePositions = new ArrayList<>();
        specialMovePositions.add(2);
        specialMovePositions.add(6);
        specialMovePositions.add(62);
        specialMovePositions.add(58);

            for (int i = 0; i < whitePositions.size(); i++) {
                 this.generateMoves(whitePositions.get(i),Square[whitePositions.get(i)],Square,attackedByWhitePositions,attackedByBlackPositions,specialMovePositions);
            }
             for (int i = 0; i < blackPositions.size(); i++) {
                 this.generateMoves(blackPositions.get(i),Square[blackPositions.get(i)],Square,attackedByBlackPositions,attackedByWhitePositions,specialMovePositions);
            }
        }



    //Wandelt Koordinaten in ein Index fürs Square Array um
    public int xyToSquare(int xValue, int yValue) {
        int minRangeX = 0;      //Die Werte in dem der Mausklick stattfinden muss.
        int maxRangeX = 800;    //(Weil in dem Bereich eben das Schachbrett ist)
        int minRangeY = 0;
        int maxRangeY = 800;

        if (xValue >= minRangeX && xValue <= maxRangeX && yValue >= minRangeY && yValue <= maxRangeY) {
            int row = yValue / 100; //funktioniert, weil int ja nur ganzzahlige Zahlen akzeptiert und IMMER abrundet
            int column = xValue / 100;
            return row * 8 + column;
        } else {
            // Umgang mit ungültigen x- oder y-Werten
            return -1;
        }
    }
    /**
     * @param pPieceValue den wert einer Figur aus Farbe * FigurTyp
     * @return den Anfangsbuchstaben des Namen der Figur (klein wenn Figur == schwarz, gross wenn weiß)
     */
    private  char pieceIntToChar(int pPieceValue)
    {
        int absolutFigurInt = Math.abs(pPieceValue);
        int farbe = pPieceValue / absolutFigurInt;

        return switch (absolutFigurInt) {
            case (Piece.king) -> farbe == Piece.black ? 'k' : 'K';
            case (pawn) -> farbe == Piece.black ? 'p' : 'P';
            case (Piece.knight) -> farbe == Piece.black ? 'n' : 'N';
            case (Piece.bishop) -> farbe == Piece.black ? 'b' : 'B';
            case (Piece.rook) -> farbe == Piece.black ? 'r' : 'R';
            case (Piece.queen) -> farbe == Piece.black ? 'q' : 'Q';
            default -> 0;
        };
    }

    /**
     * Dreckige HIlfsmethode (braucht keiner)
     */
    public  void printBoard()
    {
        String lineString = "";
        for(int i = 0; i< Square.length; i++)
        {

            if(Square[i] == Board.leeresFeld)
            {
                lineString += " ";
            }
            else
            {
                lineString += pieceIntToChar(Square[i]);
            }
            lineString += " | ";
            if(( (i + 1) % 8 == 0 && i != 0 ) || i == 63) { //Zeilenumbruch wenn i durch 8 teilbar ist -> alle 8 Felder
                System.out.println(lineString);
                System.out.println("------------------------------");
                lineString = "";
            }
        }
    }

    public int[] giveBoard()
    {
        return this.Square;
    }

    /**
     * gibt den Abstand zum Rand von jedem Feld zum Rand in jeder Richtung in einem Array an,
     * diese Information kann so vorgespeichert werden und muss nicht für jede Figur einzeln berechnet werden
     * @return Array mit Abstand zum Rand von jedem Feld zum Rand in jeder Richtung in einem Array
     */
    private int[][] getDistanceToEdges()
    {
        int[][] rdistancesToEdge = new int[64][8];
        for(int zeile = 0;zeile<8;zeile++)
        {
            for (int spalte = 0; spalte < 8; spalte++) {

                int squareIndex = zeile * 8 + spalte;
                int oben, unten, rechts, links, untenlinks, obenrechts, untenrechts, obenlinks;

                oben = zeile;
                unten = 7 - zeile;
                rechts = 7 - spalte;
                links = spalte;

                untenlinks = Math.min(unten, links);
                obenrechts = Math.min(oben,rechts);
                untenrechts = Math.min(unten, rechts);
                obenlinks = Math.min(oben, links);

                int[] distances =  new int[] {oben, unten, rechts, links, untenlinks, obenrechts, untenrechts, obenlinks};
                rdistancesToEdge[squareIndex] = distances;
            }
        }
        return rdistancesToEdge;
    }

    public int[][] generateLegalMoves(int startPosition, int pPieceValue, int[] pSquares, LinkedList<int[]> attackedByOwn,LinkedList<int[]> attackedByEnemy,LinkedList<Integer> pPositions,ArrayList<Integer> pSpecialMovePositions)
    {

        int color = pPieceValue / Math.abs(pPieceValue);


        //TODO: das koennte man Laufzeit technisch besser machen, wenn man die Positionslisten einfach nach groesse sortieren wuerde, dann koennte man sich eif ein bestimmten index holen
        int kingPosition = 0;
        for (int i = 0; i < pPositions.size(); i++) {
            if(Math.abs(pSquares[pPositions.get(i)]) == Piece.king)
            {
                kingPosition = pPositions.get(i);
                break;
            }
        }

        //wenn der Koenig nicht im Schach ist

        LinkedList<int[]> attacksOnKing = new LinkedList<>();
        for (int i = 0; i < attackedByEnemy.size(); i++) {
            if(kingPosition == attackedByEnemy.get(i)[1])
            {
                attacksOnKing.add(attackedByEnemy.get(i));
            }
        }
        int[][] moves = new int[0][0];
        if (Math.abs(pPieceValue) == Piece.king) {
            return generateLegalKingMoves(startPosition, color, pSquares, attackedByOwn, attackedByEnemy,attacksOnKing.toArray(new int[0][0]));

        }
        else if(attacksOnKing.size() == 0)
        {
             moves  =  generateMoves(startPosition,pPieceValue,pSquares,attackedByOwn,attackedByEnemy,pSpecialMovePositions);

        }
        else if(attacksOnKing.size() == 1)
        {
            moves  =  generateMoves(startPosition,pPieceValue,pSquares,attackedByOwn,attackedByEnemy,pSpecialMovePositions);
            LinkedList<int[]> legalMoves = new LinkedList<>();
            int[] allowedSquares = generateAllowedSquares(attacksOnKing.get(0));

            for (int i = 0; i < moves.length; i++) {
                for (int j = 0; j < allowedSquares.length; j++) {
                    if(moves[i][1] == allowedSquares[j])
                    {
                        legalMoves.add(moves[i]);
                        break;
                    }
                }
            }
            moves = legalMoves.toArray(new int[0][0]);
        }
        int[] allowedAfterPin = isPinned(startPosition,kingPosition,Square,attackedByEnemy);
        if(allowedAfterPin!= null) {
            LinkedList<int[]> allowedMoves = new LinkedList<>();
            for (int i = 0; i < moves.length; i++) {
                for (int j = 0; j < allowedAfterPin.length; j++) {
                    if (moves[i][1] == allowedAfterPin[j]) {
                        allowedMoves.add(moves[i]);
                    }
                }
            }
            return allowedMoves.toArray(new int[0][0]);
        }
        return moves;
    }

    public boolean isCheckMate(int color)
    {
        LinkedList<Integer> positions = color == Piece.white ? whitePositions : blackPositions;
        LinkedList<int[]> attackedByOwn = color == Piece.white ? attackedByWhitePositions : attackedByBlackPositions;
        LinkedList<int[]> attackedByEnemy = color == Piece.black ? attackedByWhitePositions : attackedByBlackPositions;
        for (int i = 0; i < positions.size(); i++) {
            if(generateLegalMoves(positions.get(i),Square[positions.get(i)],Square,attackedByOwn,attackedByEnemy,positions,specialMovePositions).length != 0) {
            return false;
            }
            }
        return true;
    }
    public int[][] generateLegalKingMoves(int startPosition, int color, int[] pSquares,LinkedList<int[]> attackedByOwn, LinkedList<int[]> attackedByEnemy,int[][] attacksOnKing)
    {
        LinkedList<int[]> moves = new LinkedList<>();

        outerloop: for (int i = 0; i < directions.length; i++) {
            int newSquare = startPosition + directions[i];
            if((newSquare >= 0 && newSquare < pSquares.length) && (pSquares[newSquare] == leeresFeld || pSquares[newSquare] / Math.abs(pSquares[newSquare]) != color))
            {
                for (int j = 0; j < attackedByEnemy.size(); j++) {
                    if(newSquare == attackedByEnemy.get(j)[1])
                    {
                        continue outerloop;
                    }
                    if(attacksOnKing.length != 0)
                    {
                        //wenn der Koenig angegriffen ist, darf er sich nicht in die Richtung des Angriffs zurückziehen
                        int moveDirection = newSquare - startPosition;
                        for (int k = 0; k < attacksOnKing.length; k++) {
                            int attackDirection = attacksOnKing[k][0] - startPosition;
                            int absAttackingPieceValue = Math.abs(attacksOnKing[k][2]);
                            if(absAttackingPieceValue == Piece.queen || absAttackingPieceValue == Piece.rook|| absAttackingPieceValue == Piece.bishop) {
                                if (attackDirection % moveDirection == 0 && (Math.abs(moveDirection) != 1 || (Math.abs(moveDirection) == 1 && attacksOnKing[k][0] / 8 == attacksOnKing[k][1] / 8)) && newSquare != attacksOnKing[k][0]) {
                                    continue outerloop;
                                }
                            }
                        }
                    }
                }

                moves.add(new int[]{startPosition,newSquare,Piece.king * color});
                addToAttackedPositions(startPosition,newSquare,Piece.king,attackedByOwn);
            }
        }
        return moves.toArray(new int[0][0]);
    }

    private int[] generateAllowedSquares(int[] attackOnKing)
    {
        LinkedList<Integer> squares = new LinkedList<>();
        squares.add(attackOnKing[0]); // Das Square von dem der Keonig angegriffen wird is immer dabei
        if((attackOnKing[2] == Piece.queen || attackOnKing[2] == Piece.rook|| attackOnKing[2] == Piece.bishop))
        {
            int attackDirection = attackOnKing[1] - attackOnKing[0];
            int vorzeichen = attackDirection / Math.abs(attackDirection);
            if(attackDirection % 9 == 0)
            {
                attackDirection /= (attackDirection /= 9);
                attackDirection *= vorzeichen;
            }
            else if(attackDirection % 8 == 0)
            {
                attackDirection /= (attackDirection /= 8);
                attackDirection *= vorzeichen;
            }
            else if(attackDirection % 7 == 0)
            {
                attackDirection /= (attackDirection /= 7);
                attackDirection *= vorzeichen;
            }
            else
            {
                attackDirection /= (attackDirection /= 1);
                attackDirection *= vorzeichen;
            }
            for(int i = attackOnKing[0] + attackDirection;i != attackOnKing[1]; i += attackDirection)
            {
                squares.add(i);
            }
        }
        return squares.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     *
     * @param ownPosition die Position d. Figur die ueberprueft werden soll ob sie gepinnt ist
     * @param kingPosition die Position d. Koenigs d. Figur
     * @param pSquare das Brett auf dem gespielt wird
     * @param attackedByEnemy die Felder die von der Gegnerischen Farbe angegriffen werden
     * @return null -> es liegt kein Pinn vor; Wenn das Arrays Zahlen enthält, sind das die Felder die trotz Pinn legal begehbar waeren(length == 0-> keine legalen Felder wegen Pinn)
     */
    private int[] isPinned(int ownPosition,int kingPosition, int[] pSquare,LinkedList<int[]> attackedByEnemy)
    {
        if(ownPosition==kingPosition)
        {
            return null;
        }
        int directionToKing = ownPosition - kingPosition;
        if(directionToKing % 9 == 0)
        {
            directionToKing/=(Math.abs(directionToKing/=9));
        }
        else if(directionToKing % 8 == 0)
        {
            directionToKing/=(Math.abs(directionToKing/=8));
        }
        else if(directionToKing % 7 == 0)
        {
            directionToKing/=(Math.abs(directionToKing/=7));
        }
        else if(ownPosition/8 == kingPosition/8) // wenn in der selben Reihe
        {
            directionToKing /= Math.abs(directionToKing);
        }
        else
        {
            return null;
        }
        for (int i = kingPosition + directionToKing; i != ownPosition ; i+= directionToKing) {
            if(pSquare[i] != leeresFeld)
            {
                return null;
            }
        }
        int[] allowedSquares = null;
        for (int i = 0; i < attackedByEnemy.size(); i++) {
            int pieceType = attackedByEnemy.get(i)[2];
            int attackBeginSquare = attackedByEnemy.get(i)[0];
            int attackedSquare = attackedByEnemy.get(i)[1];
            if((pieceType == Piece.queen || pieceType == Piece.rook|| pieceType == Piece.bishop) && attackedSquare == ownPosition)
            {
                int attackDirection = attackedByEnemy.get(i)[0] - ownPosition;
                if(attackDirection % 9 == 0)
                {
                    attackDirection/=(Math.abs(attackDirection/=9));
                }
                else if(attackDirection % 8 == 0)
                {
                    attackDirection/=(Math.abs(attackDirection/=8));
                }
                else if(attackDirection % 7 == 0)
                {
                    attackDirection/=(Math.abs(attackDirection/=7));
                }
                else if(ownPosition/8 == kingPosition/8) // wenn in der selben Reihe
                {
                    attackDirection /= Math.abs(attackDirection);
                }
                if(attackDirection == directionToKing)
                {
                    if(!(Square[ownPosition] == Piece.knight || (Square[ownPosition] == Piece.pawn && Math.abs(attackedSquare) == 1)))
                    {
                    allowedSquares = new int[Math.abs(((ownPosition + attackDirection) - attackBeginSquare) / attackDirection) + 1];
                    for (int j = ownPosition + attackDirection; j != attackBeginSquare; j+= attackDirection) {
                        int index = (j -(ownPosition + attackDirection)) / attackDirection;
                        allowedSquares[index] = j;
                    }
                    allowedSquares[allowedSquares.length - 1] = attackBeginSquare;
                    }
                    break;
                }
            }

        }
        return allowedSquares;
    }


    /**
     *  unnoetige Hilfsmethode TODO: irgendwann entfernen
     * @param move der move(StartPosition, Endposition
     */
    public void printMove(int[] move)
    {
        System.out.println(pieceIntToChar(move[2])+" von "+move[0]+" nach "+move[1]);
    }

    /**
     *
     * @param startPosition startPosition auf dem brett
     * @return die Moves die gehen würden, wenn keine anderen Figuren auf dem Feld wären
     */
   public  int[][] generateMoves(int startPosition, int pPieceValue, int[] pSquares, LinkedList<int[]> attackedByOwn,LinkedList<int[]> attackedByEnemy,ArrayList<Integer> pSpecialMovePositions)
    {

        //int figurInt = pSquares[startPosition]; -> waere theorethisch auch moeglich


        int absPieceValue = Math.abs(pPieceValue);
        if(absPieceValue == pawn)
        {
            return generatePawnMoves(startPosition,pPieceValue,pSquares,attackedByOwn,pSpecialMovePositions);
        }
        else if(absPieceValue == Piece.knight)
        {
            return generateKnightMoves(startPosition,pPieceValue,pSquares,attackedByOwn);
        }
        return generateSlidingPieceMoves(startPosition,pPieceValue,pSquares,attackedByOwn);
    }

    /**
     * generiert die möglichen Moves für Laeufer, Turm, Dame wenn
     * @param startPos aktuelle Position der Figur un
     * @return Array an allen möglichen Moves
     */
    public int[][] generateSlidingPieceMoves(int startPos, int pPieceValue, int[] pSquares, LinkedList<int[]> attackedByColorPositions)
    {
        LinkedList<int[]> moves = new LinkedList<>();
        int absPieceValue = Math.abs(pPieceValue);


        int anfang = 0;
        int ende = 8;
        if(absPieceValue == Piece.rook) //nur oben,unten,rechts,links
        {
            ende = 4;
        }
        else if(absPieceValue == Piece.bishop) //nur schraeg
        {
            anfang = 4;
        }


        for(;anfang < ende;anfang++)
        {


            for(int j = 1;j <= distancesToEdge[startPos][anfang];j++) // j=1 damit das Startfeld nicht mitreingenommen wird
            {
                int square = startPos + j * directions[anfang];//aktuellesFeld = Startfeld + Die Anzahl von Schritten in eine Richtung
                int eigeneFarbe = pPieceValue / Math.abs(pPieceValue);
                addToAttackedPositions(startPos,square,absPieceValue,attackedByColorPositions);
                if(pSquares[square] == leeresFeld)
                {
                    moves.add(new int[]{startPos,square,pPieceValue});
                }
                else
                {
                    int farbeAndereFigur = pSquares[square] / Math.abs(pSquares[square]);
                    if (eigeneFarbe != farbeAndereFigur) {
                        moves.add(new int[]{startPos,square,pPieceValue});
                    }
                    break; //in jedem Fall kann die Figur nachdem sie auf eine andere Figur getroffen ist nicht weiterlaufen
                }
            }
        }
        return moves.toArray(new int[0][0]);
    }

    public int[][] generateKnightMoves(int startPos, int pPieceValue,  int[] pSquares, LinkedList<int[]> attackedByColorPositions)
    {
        int absPieceValue = Math.abs(pPieceValue);
        int eigeneFarbe = pPieceValue /  absPieceValue;

        ArrayList<int[]> moves = new ArrayList<>();

        int[] knightDirections = new int[]{-16 + 1, -16 - 1, // 3 nach oben, 1 nach links/rechts
                2 - 8, 2 + 8,    // 3 nach rechts, 1 nach oben/unten
                16 + 1, 16 - 1,  // 3 nach unten, 1 nach links/rechts
                -2 - 8, -2 + 8   // 3 nach links, 1 nach oben/unten
        };
        int[][] necessaryDistancestoEdge =  new int[][]{{2,0,1,0}, {2,0,0,1}, // kann man bestimmt auch berechnen, hab aber keine Lust das zu machen und so geht das aufjedenfall schneller
                {1,0,2,0}, {0,1,2,0},
                {0,2,1,0}, {0,2,0,1},
                {1,0,0,2}, {0,1,0,2}};

        outerloop: for (int i = 0; i < knightDirections.length ; i++) {
            for (int j = 0; j < 4; j++) {
                if(necessaryDistancestoEdge[i][j] > distancesToEdge[startPos][j]) // wenn in irgendeine Richtung nicht genuegend Platz ist
                {
                    continue outerloop; // wird der Move geskippt
                }}

            int neuersquare = startPos + knightDirections[i];

            // wenn das neue Feld leer ist oder ein Feind drauf ist, ist der Move okay,
            // wenn eine eigene Figur auf dem Feld ist, geht der Move nicht und muss dementsprechend auch nicht hinzugefuegt werden
            addToAttackedPositions(startPos, neuersquare, absPieceValue, attackedByColorPositions);
            if(pSquares[neuersquare] == leeresFeld || (eigeneFarbe != pSquares[neuersquare] / Math.abs(pSquares[neuersquare]))) {
                moves.add(new int[]{startPos, neuersquare, Piece.knight * eigeneFarbe});
            }



        }
        return moves.toArray(new int[0][0]);
    }

    public int[][] generatePawnMoves(int startPos,int pPieceValue, int[] pSquares, LinkedList<int[]> ownAttackedPositions,ArrayList<Integer> pSpecialMovePositions)
    {
        LinkedList<int[]> moves = new LinkedList<>();

        int absPieceValue = Math.abs(pPieceValue);
        int eigeneFarbe = pPieceValue / absPieceValue;
        int bewegungsrichtung = -8 * eigeneFarbe; //Weiß geht von höheren Zahlen zu niedrigeren#
        int anfangsReiheAnfang = eigeneFarbe == Piece.white ? 48 : 8;
        int anfangsReiheEnde = anfangsReiheAnfang + 7;
        int endReiheAnfang = eigeneFarbe == Piece.white ? 8 : 48;
        int endReiheEnde = endReiheAnfang + 7;
        int enPassantReiheAnfang = eigeneFarbe == Piece.white ? 24:32;
        int enPassantReiheEnde = enPassantReiheAnfang + 7;


        /*Dadurch dass Zuerst nach moves gesucht wird, bei denen Bauern andere schlagen (ist immer mindestens ein gleichwertiger Trade),
        sind gute Moves am Anfang der Liste was z.B. bei Minimax mit Alpha-Beta-Pruning Sinn macht
        */

        //schraeg rechts
        int neuersquare = startPos+bewegungsrichtung+1;
        if(neuersquare % 8 != 0 && (0 <= neuersquare && neuersquare < 64)) {
            addToAttackedPositions(startPos, neuersquare,absPieceValue,ownAttackedPositions);
            if (pSquares[neuersquare] != leeresFeld && pSquares[neuersquare] / Math.abs(pSquares[neuersquare]) == eigeneFarbe * -1) // Ist vorne und 1 nach rechts ein Gegner
            {
                if(endReiheAnfang <= startPos && startPos <= endReiheEnde)
                {
                    moves.add(new int[]{startPos, neuersquare,c.view.getPromotionInt() * eigeneFarbe});
                }
                else
                {moves.add(new int[]{startPos, neuersquare, pawn * eigeneFarbe});}

            }
        }
        //schraeg links
        neuersquare = startPos+bewegungsrichtung-1;
        if(neuersquare / 8 == (startPos+bewegungsrichtung) / 8 && (0 <= neuersquare && neuersquare < 64)) {
            addToAttackedPositions(startPos, neuersquare,absPieceValue,ownAttackedPositions);
            if (pSquares[neuersquare] != leeresFeld && pSquares[neuersquare] / Math.abs(pSquares[neuersquare]) == eigeneFarbe * -1) // Ist vorne und 1 nach links ein Gegner
            {
                if(endReiheAnfang <= startPos && startPos <= endReiheEnde)
                {
                    moves.add(new int[]{startPos, neuersquare,c.view.getPromotionInt() * eigeneFarbe});
                }
                else
                {moves.add(new int[]{startPos, neuersquare, pawn * eigeneFarbe});}


            }
        }
        //En passant
        if(enPassantReiheAnfang <= startPos && startPos <= enPassantReiheEnde)
        {
            if((startPos+1) / 8 == startPos / 8 && pSquares[startPos+1] != leeresFeld && Math.abs(pSquares[startPos+1]) == pawn)
            {
                for (int i = 0; i < specialMovePositions.size(); i++) {
                    if(specialMovePositions.get(i) == startPos+1)
                    {
                        addToAttackedPositions(startPos,startPos+1, enPassantInt,ownAttackedPositions);
                        moves.add(new int[]{startPos,startPos+1+bewegungsrichtung,enPassantInt*eigeneFarbe});
                        break;
                    }
                }
            }
            if((startPos-1) / 8 == startPos / 8 && pSquares[startPos-1] != leeresFeld && Math.abs(pSquares[startPos-1]) == pawn)
            {
                for (int i = 0; i < specialMovePositions.size(); i++) {
                    if(specialMovePositions.get(i) == startPos-1)
                    {
                        addToAttackedPositions(startPos,startPos-1, enPassantInt,ownAttackedPositions);
                        moves.add(new int[]{startPos,startPos-1+bewegungsrichtung,enPassantInt*eigeneFarbe});
                        break;
                    }
                }
            }
        }

        //geradeaus 1
        neuersquare = startPos+bewegungsrichtung;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == leeresFeld)
        {
            if(endReiheAnfang <= startPos && startPos <= endReiheEnde)
            {
                moves.add(new int[]{startPos, neuersquare,c.view.getPromotionInt() * eigeneFarbe});
            }
            else
            {moves.add(new int[]{startPos, neuersquare, pawn * eigeneFarbe});}
        }

        // geradeaus 2
        neuersquare = startPos+bewegungsrichtung*2;
        if((anfangsReiheAnfang <= startPos && startPos <= anfangsReiheEnde) && pSquares[neuersquare] == leeresFeld)
        {
            moves.add(new int[]{startPos,neuersquare, pawn * eigeneFarbe});
        }

        return moves.toArray(new int[0][0]);
    }

    private LinkedList getPositions(int pColor, int[] pSquare)
    {
        LinkedList<Integer> positions = new LinkedList<Integer>();
        for (int i = 0; i < pSquare.length && positions.size() < 16; i++) {
            if(pSquare[i] != leeresFeld && pSquare[i]/Math.abs(pSquare[i]) == pColor)
                positions.add(i);
        }

        return positions;
    }

    /**
     * Diese Version führt einen Move aus, dafür darf die Startpos aber noch nd == 0 sein
     * @param pStartPos Startposition des MOves
     * @param pEndPos Endposition
     * @param pSquare das Schachbrett auf dem gemoved wird
     * @param pPositionlist Liste mit Positionen der selben Farbe
     */
    public void execMove(int pStartPos, int pEndPos, int[] pSquare, LinkedList<Integer> pPositionlist)
    {
        int color = pSquare[pStartPos] / Math.abs(pSquare[pStartPos]);



        pPositionlist.remove((Integer) pStartPos);
        pPositionlist.add(pEndPos);
        if(pSquare[pEndPos] != leeresFeld)
        {
            LinkedList<Integer> enemylist = color == Piece.black ? whitePositions : blackPositions;
            enemylist.remove((Integer) pEndPos); // dadurch das integer verwendet wird, wird nicht der Index entfernt, sondern das Objekt mit dem Wert
        }
        pSquare[pEndPos] = pSquare[pStartPos];
        pSquare[pStartPos] = leeresFeld;
    }

    /**
     * Diese Version führt einen Move aus, AUF DEM HAUPTBOARD NICHT FUER MINIMAX
     * @param pPieceValue der Figur Value der Figur die gezogen werden soll
     * @param
     */
    public void execMove(int pStartPosition, int pEndPosition,int pPieceValue)
    {
        int color = pPieceValue / Math.abs(pPieceValue);
        LinkedList<int[]> ownAttackedPositions = color == Piece.white ? attackedByWhitePositions : attackedByBlackPositions;
        LinkedList<int[]> enemyAttackedPositions = color == Piece.black ? attackedByWhitePositions : attackedByBlackPositions;
        LinkedList<Integer> ownPositions = color == Piece.white ? whitePositions : blackPositions;
        LinkedList<Integer> enemyPositions = color == Piece.black ? whitePositions : blackPositions;

        // Die Position, die Figur vorher angegriffen hat werden entfernt
        for (int i = 0; i < ownAttackedPositions.size(); i++) {
            if(ownAttackedPositions.get(i)[0] == pStartPosition || (ownAttackedPositions.get(i)[1] == pStartPosition))//bisherige AttackedPositions der bewegten Figur entfernen
            {
                //(ownAttackedPositions.get(i));
                ownAttackedPositions.remove(i);
                i-=1; //ein element weniger -> index muss ein weniger sein
            }

        }

        //falls eine gegnerische Figur geschlagen wird, werden ihre AttackedPositions entfernt
        if(Square[pEndPosition] != leeresFeld)
        {
            //Wenn es eine Figur nicht mehr gibt, kann sie auch keine Felder mehr angreifen

            for (int i = 0; i < enemyAttackedPositions.size(); i++) {
                if(enemyAttackedPositions.get(i)[0] == pEndPosition)
                {
                    enemyAttackedPositions.remove(i);
                    i-=1;
                }
            }

            //Figur wird aus den Positionen rausgenommen
            enemyPositions.remove((Integer) pEndPosition); // dadurch das integer verwendet wird, wird nicht der Index entfernt, sondern das Objekt mit dem Wert
        }

        //Zug im Square array eintragen
        if(Math.abs(pPieceValue)!=enPassantInt) {
            Square[pEndPosition] = pPieceValue;
        }
        else
        {
            Square[pEndPosition] = pawn * color;
            Square[pEndPosition + color * 8] = leeresFeld;
        }
        Square[pStartPosition] = leeresFeld;
        //Angriffe, die dadurch entstehen, dass das Feld frei wird generieren
        outerloop: for (int i = 0; i < ownPositions.size(); i++) {
            int ownPosition = ownPositions.get(i);
            int absPieceValue = Math.abs(Square[ownPosition]);
            if(ownPosition != pStartPosition && (absPieceValue == Piece.queen || absPieceValue == Piece.rook|| absPieceValue == Piece.bishop))
            {
                int attackDirection = pStartPosition - ownPositions.get(i);
                int[] attackDirections;
                switch (Math.abs(Square[ownPositions.get(i)]))
                {
                    case(Piece.rook):attackDirections = new int[]{8,1};break;
                    case(Piece.bishop):attackDirections = new int[]{9,7};break;
                    default:attackDirections = new int[]{9,8,7,1};break;
                }

                for (int j = 0; j < attackDirections.length; j++) {
                    if(attackDirection % attackDirections[j] == 0)
                    {
                        if(Math.abs(attackDirections[j])!=1 || (Math.abs(attackDirections[j]) == 1 && ownPosition/8 == pStartPosition /8)) {
                            if (Math.abs(attackDirection) != Math.abs(attackDirections[j])) {
                                attackDirection /= attackDirections[j];
                            }
                            for (int k = ownPosition + attackDirection; k != pStartPosition; k += attackDirection) {
                                //System.out.println(k);
                                if (Square[k] != leeresFeld) {
                                    continue outerloop;
                                }
                            }
                            int[][] neugeneriert = generateLegalMoves(ownPosition, Square[ownPosition], Square, ownAttackedPositions, enemyAttackedPositions, ownPositions,specialMovePositions);
                            /*for (int k = 0; k < neugeneriert.length; k++) {
                                System.out.print("neu");
                                printMove(neugeneriert[k]);
                            }*/
                        }
                    }
                }
            }
        }

        //Angriffe(des Gegners), die dadurch verhindert werden, dass das Feld besetzt wird loeschen
        LinkedList<int[]> blockedMoves = new LinkedList<>();
        for (int i = 0; i < enemyAttackedPositions.size(); i++) {
            int attackEndPosition = enemyAttackedPositions.get(i)[1];
            int absAttackPieceValue = Math.abs(enemyAttackedPositions.get(i)[2]);
            if(absAttackPieceValue == Piece.queen || absAttackPieceValue== Piece.rook|| absAttackPieceValue == Piece.bishop)
            {
                if(attackEndPosition == pEndPosition)
                {
                    blockedMoves.add(enemyAttackedPositions.get(i));
                }
            }
        }
        for (int i = 0; i < enemyAttackedPositions.size(); i++) {
            for (int j = 0; j < blockedMoves.size(); j++) {
                if(blockedMoves.get(j)[0] == enemyAttackedPositions.get(i)[0] )
                {
                    enemyAttackedPositions.remove(enemyAttackedPositions.get(i));
                    i -= 1;
                }
            }
        }
        for (int i = 0; i < blockedMoves.size(); i++) {
            generateLegalMoves(blockedMoves.get(i)[0],Square[blockedMoves.get(i)[0]],Square,enemyAttackedPositions,ownAttackedPositions,enemyPositions,specialMovePositions);
        }


        //Figur aus Positionen loeschen
        ownPositions.removeFirstOccurrence(/*(Integer)*/ pStartPosition);
        ownPositions.add(pEndPosition);

        //neue Legalmoves von der neuen Figur vom neuen Feld saus
        generateLegalMoves(pEndPosition,pPieceValue,Square,ownAttackedPositions,enemyAttackedPositions,ownPositions,specialMovePositions);

        //En-Passant ist immer nur einen Zug lang moeglich
        for (int i = 0; i < specialMovePositions.size(); i++) {
            int position = specialMovePositions.get(i);
            if(position!=2&&position!=6&&position!=62&&position!=58)
            {
                specialMovePositions.remove(i);
                i-=1;
            }
        }
        //Falls neue Specialmoves entstanden sind, diese eintragen
        if(Math.abs(pPieceValue) == Piece.pawn && Math.abs(pStartPosition - pEndPosition) == 16)
        {
            specialMovePositions.add(pEndPosition);
        }
    }

    private void addToAttackedPositions(int pEigenePosition, int pAttackedSquare, int pAbsPieceValue, LinkedList<int[]> pAttackedPositions)
    {
        pAttackedPositions.add(new int[]{pEigenePosition,pAttackedSquare, pAbsPieceValue});
    }

    /**
     * funktioniert nur, wenn bekannst ist welche Felder angegriffen werden-> für die Figur,
     * die den König angegriffen hat muss deshalb generateMoves(Position der Figur, aktuelles Brett, durchDieFarbeDerFigurAngegriffeneFelder)
     * gelaufen sein.
     * @param pPosition Position der Figur von dem geguckt werden soll ob er im Schach ist
     * @param pAttackedByColorPositions durch Die Farbe(die andere als die Figur) angegriffene Felder
     * @return true = angegriffen
     */
    public boolean isPositionAttacked(int pPosition, LinkedList<int[]> pAttackedByColorPositions)
    {

        for (int[] pAttackedByColorPosition : pAttackedByColorPositions) {
            if (pPosition == pAttackedByColorPosition[1]) {
                return true;
            }
        }
        return false;
    }


    //soll checken ob ein square besetzt ist
    public boolean isEmpty(int pFeld) {
        // Stimmt der Bereich
        if (pFeld < 0 || pFeld >= Square.length) {
            System.out.println("IRGENDWO WIRD GRAD VERSUCHT ZU GUCKEN, OB EIN NICHT EXISTENTES FELD LEER IST!!!");
        }
        // Wert ==0
        return Square[pFeld] == 0;
    }

    /**
     * setter-methode um das square-array zu verändern mit
     * @param pIndex an welchem square-index
     * @param //Value welchen wert (Piece)
     */
    public int getSquare(int pIndex){
        return Square[pIndex];
    }
    public void setSquare(int pIndex, int pValue){
        Square[pIndex] = pValue;
    }

    public int getSquareLength()
    {
        return Square.length;
    }


    /**
     * unter angabe der feldnummer wird die daraufstehende figur returnt
     * @param pIndex die Feldnummer
     * @return das Piece was auf dem Feld vom pIndex is
     */
    public int getPieceFromSquare(int pIndex){
        return Square[pIndex];
    }

   //Default Position vom Schach. (EInfach ganz nach unten immer packen die nervt sonst nur)
    public void initalizeSquare() {
        Square[0] = -4;
        Square[1] = -2;
        Square[2] = -3;
        Square[3] = -5;
        Square[4] = -6;
        Square[5] = -3;
        Square[6] = -2;
        Square[7] = -4;

        Square[8] = -1;
        Square[9] = -1;
        Square[10] = -1;
        Square[11] = -1;
        Square[12] = -1;
        Square[13] = -1;
        Square[14] = -1;
        Square[15] = -1;

        Square[48] = 1;
        Square[49] = 1;
        Square[50] = 1;
        Square[51] = 1;
        Square[52] = 1;
        Square[53] = 1;
        Square[54] = 1;
        Square[55] = 1;

        Square[56] = 4;
        Square[57] = 2;
        Square[58] = 3;
        Square[59] = 5;
        Square[60] = 6;
        Square[61] = 3;
        Square[62] = 2;
        Square[63] = 4;

        //Debugging for(int i = 0; i < Square.length; i++)
        //{System.out.println("Square[" + i + "]: " + Square[i]);}
    }
}
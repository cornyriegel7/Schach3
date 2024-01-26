package pack;
import java.awt.*;
import java.util.*;

public class Board {
    //Gameplay Zeug
    private final int[] Square;
    private static final int leeresFeld = 0;
    static int[][] distancesToEdge;
    BoardGUI boardgui;
    static int oben = -8, unten = 8,  rechts = 1, links = -1, obenrechts = -7, untenlinks = 7, obenlinks = -9, untenrechts = 9;
    
    static final int[] directions = {oben, unten,
            rechts, links,
            untenlinks, obenrechts,
            untenrechts, obenlinks};
    LinkedList<int[]> attackedByWhitePositions, attackedByBlackPositions;
    LinkedList<Integer>  whitePositions, blackPositions;
    View view;
    public Board(View pView){
        Square = new int[64]; //brett als eindimensionales array (von oben links nach unten rechts) ist später praktisch
        initalizeSquare();
        boardgui = new BoardGUI(this);

        distancesToEdge = getDistanceToEdges();
        whitePositions=getPositions(Piece.white, Square);
        blackPositions = getPositions(Piece.black, Square);
        attackedByWhitePositions = new LinkedList<>();
        attackedByBlackPositions = new LinkedList<>();

        view = pView;
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
     * @param pPieceInt den wert einer Figur aus Farbe * FigurTyp
     * @return den Anfangsbuchstaben des Namen der Figur (klein wenn Figur == schwarz, gross wenn weiß)
     */
    private  char pieceIntToChar(int pPieceInt)
    {
        int absolutFigurInt = Math.abs(pPieceInt);
        int farbe = pPieceInt / absolutFigurInt;

        return switch (absolutFigurInt) {
            case (Piece.king) -> farbe == Piece.black ? 'k' : 'K';
            case (Piece.pawn) -> farbe == Piece.black ? 'p' : 'P';
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

    /**
     *  soll irgendwann mal alle moeglichen Zuege fuer eine Farbe ausgeben
     * @param pPositions die Positionen d. Figuren d. Farbe
     * @param pSquares das gesamte Brett
     * @param durchAndereFarbeAngegriffeneFelder obv
     * @param durchEigeneFarbeAngegriffeneFelder obv
     * @return alle Zuege die nach allen Spielregeln okay sind
     */
   /* public int[][][] getLegalMoves(LinkedList<Integer> pPositions, int[] pSquares, LinkedList<int[]> durchAndereFarbeAngegriffeneFelder, LinkedList<int[]> durchEigeneFarbeAngegriffeneFelder)
    {
        LinkedList<int[][]> legalMoves = new LinkedList<>();
        for (Integer pPosition : pPositions) {
            legalMoves.add(generateMoves(pPosition, pSquares, durchEigeneFarbeAngegriffeneFelder));
        }
        for (int[][] legalMove : legalMoves) {
            for (int[] ints : legalMove) {
                printMove(ints);
            }
        }
        return legalMoves.toArray(new int[0][0][0]);
    }*/


    /**
     * Legalmoves fuer eine einzige Figur
     * @param pFigurInt
     * @param startPosition
     * @param pSquares
     * @param durchAndereFarbeAngegriffeneFelder
     * @param durchEigeneFarbeAngegriffeneFelder
     * @param pPositions
     * @return
     */
    public int[][] getLegalMoves(int pFigurInt, int startPosition, int[] pSquares,
                                 LinkedList<int[]> durchAndereFarbeAngegriffeneFelder,LinkedList<int[]> durchEigeneFarbeAngegriffeneFelder,
                                 LinkedList<Integer> pPositions)
    {

        int[][] pseudolegalMoves = generateMoves(startPosition,pFigurInt,pSquares,durchEigeneFarbeAngegriffeneFelder);


        //TODO: das koennte man Laufzeit technisch besser machen, wenn man die Positionslisten einfach nach groesse sortieren wuerde, dann koennte man sich eif ein bestimmten index holen
        int kingPosition = 0;
        for (int i = 0; i < pPositions.get(i); i++) {
            if(Math.abs(Square[pPositions.get(i)]) == Piece.king)
            {
                kingPosition = pPositions.get(i);
                break;
            }
        }
        //wenn der Koenig nicht im Schach ist

        if(!isPositionAttacked(kingPosition,durchAndereFarbeAngegriffeneFelder))
        {
            /*
        }
            // Und wenn es kein Abzugsscach gibt
            if(!isPositionAttacked(startPosition,durchAndereFarbeAngegriffeneFelder))
            {*/
            return pseudolegalMoves;
            /*}
            else
            {
                //TODO: gucken ob es Abzugsschach geben koennte
            }*/
        }
        else
        {
            //TODO: tun wenn der Koenig im Schach ist
        }




        return null;
    }


    /**
     *  unnoetige Hilfsmethode TODO: irgendwann entfernen
     * @param move der move(StartPosition, Endposition
     */
    public void printMove(int[] move)
    {
        System.out.println("von "+move[0]+" nach "+move[1]);
    }

    /**
     *
     * @param startPosition startPosition auf dem brett
     * @return die Moves die gehen würden, wenn keine anderen Figuren auf dem Feld wären
     */
   public  int[][] generateMoves(int startPosition, int pFigurInt, int[] pSquares, LinkedList<int[]> attackedByColorPositions)
    {

        //int figurInt = pSquares[startPosition]; -> waere theorethisch auch moeglich


        int absolutFigurInt = Math.abs(pFigurInt);
        if(absolutFigurInt == Piece.pawn)
        {
            return generatePawnMoves(startPosition,pFigurInt,pSquares,attackedByColorPositions);
        }
        else if(absolutFigurInt == Piece.knight)
        {
            return generateKnightMoves(startPosition,pFigurInt,pSquares,attackedByColorPositions);
        }
        return generateSlidingPieceMoves(startPosition,pFigurInt,pSquares,attackedByColorPositions);
    }

    /**
     * generiert die möglichen Moves für Laeufer, Turm, Dame wenn
     * @param startPos aktuelle Position der Figur un
     * @return Array an allen möglichen Moves
     */
    public int[][] generateSlidingPieceMoves(int startPos, int FigurInt, int[] pSquares, LinkedList<int[]> attackedByColorPositions)
    {
        ArrayList<int[]> moves = new ArrayList<>();
        int absolutFigurInt = Math.abs(FigurInt);


        int anfang = 0;
        int ende = 8;
        if(absolutFigurInt == Piece.rook) //nur oben,unten,rechts,links
        {
            ende = 4;
        }
        else if(absolutFigurInt == Piece.bishop) //nur schraeg
        {
            anfang = 4;
        }


        for(;anfang < ende;anfang++)
        {


            for(int j = 1;j <= distancesToEdge[startPos][anfang];j++) // j=1 damit das Startfeld nicht mitreingenommen wird
            {
                int square = startPos + j * directions[anfang];//aktuellesFeld = Startfeld + Die Anzahl von Schritten in eine Richtung
                int eigeneFarbe = FigurInt / Math.abs(FigurInt);
                if(pSquares[square] == leeresFeld)
                {
                    moves.add(new int[]{startPos,square});
                    addToAttackedPositions(startPos,square,attackedByColorPositions);
                }
                else
                {
                    int farbeAndereFigur = pSquares[square] / Math.abs(pSquares[square]);
                    if (eigeneFarbe != farbeAndereFigur) {
                        moves.add(new int[]{startPos,square});
                        addToAttackedPositions(startPos,square,attackedByColorPositions);
                    }
                    break; //in jedem Fall kann die Figur nachdem sie auf eine andere Figur getroffen ist nicht weiterlaufen
                }

                //TODO: kann vereinfacht werden
                if(absolutFigurInt == Piece.king){break;} // n könig kann nur ein Feld weit gehen
            }
        }
        return moves.toArray(new int[0][0]);
    }

    public int[][] generateKnightMoves(int startPos, int FigurInt,  int[] pSquares, LinkedList<int[]> attackedByColorPositions)
    {

        int eigeneFarbe = FigurInt / Math.abs(FigurInt);
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
            if(pSquares[neuersquare] == leeresFeld || eigeneFarbe != pSquares[neuersquare] / Math.abs(pSquares[neuersquare]))
            {
                moves.add(new int[]{startPos,neuersquare});
                addToAttackedPositions(startPos,neuersquare,attackedByColorPositions);
                System.out.println(neuersquare);

            }



        }
        return moves.toArray(new int[0][0]);
    }

    public int[][] generatePawnMoves(int startPos,int FigurInt, int[] pSquares, LinkedList<int[]> attackedByColorPositions)
    {
        LinkedList<int[]> moves = new LinkedList<>();

        int eigeneFarbe = FigurInt / Math.abs(FigurInt);
        int bewegungsrichtung = -8 * eigeneFarbe; //Weiß geht von höheren Zahlen zu niedrigeren#
        int anfangsReiheAnfang = eigeneFarbe == Piece.white ? 48 : 8;
        int anfangsReiheEnde = anfangsReiheAnfang + 7;


        /*Dadurch dass Zuerst nach moves gesucht wird, bei denen Bauern andere schlagen (ist immer mindestens ein gleichwertiger Trade),
        sind gute Moves am Anfang der Liste was z.B. bei Minimax mit Alpha-Beta-Pruning Sinn macht
        */

        //schraeg rechts
        int neuersquare = startPos+bewegungsrichtung+1;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == eigeneFarbe*-1) // Ist vorne und 1 nach rechts ein Gegner
        {
            moves.add(new int[]{startPos,neuersquare});
            addToAttackedPositions(startPos,neuersquare,attackedByColorPositions);
            System.out.println(neuersquare);
        }
        //schraeg links
        neuersquare = startPos+bewegungsrichtung-1;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == eigeneFarbe*-1) // Ist vorne und 1 nach links ein Gegner
        {
            moves.add(new int[]{startPos,neuersquare});System.out.println(neuersquare);
            addToAttackedPositions(startPos,neuersquare,attackedByColorPositions);

        }


        //geradeaus 1
        neuersquare = startPos+bewegungsrichtung;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == leeresFeld)
        {
            moves.add(new int[]{startPos,neuersquare});System.out.println(neuersquare);
            addToAttackedPositions(startPos,neuersquare,attackedByColorPositions);

        }

        // geradeaus 2
        neuersquare = startPos+bewegungsrichtung*2;
        if((anfangsReiheAnfang <= startPos && startPos <= anfangsReiheEnde) && pSquares[neuersquare] == leeresFeld)
        {
            moves.add(new int[]{startPos,neuersquare});System.out.println(neuersquare);
            addToAttackedPositions(startPos,neuersquare,attackedByColorPositions);

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
     * @param pPieceValue
     * @param pStartPos
     * @param pEndPos
     */
    public void execMove(int pPieceValue,int pStartPos, int pEndPos)
    {
        int color = pPieceValue / Math.abs(pPieceValue);



        LinkedList<int[]> ownAttackedPositions = color == Piece.white ? attackedByWhitePositions : attackedByBlackPositions;
        for (int i = 0; i < ownAttackedPositions.size(); i++) {
            if(ownAttackedPositions.get(i)[0] == pStartPos)
            {
                ownAttackedPositions.remove();
            }
        }


        LinkedList<Integer> Positionlist = color == Piece.white ? whitePositions : blackPositions;
        Positionlist.remove((Integer) pStartPos);
        Positionlist.add(pEndPos);



        LinkedList<int[]> enemyAttackedPositions = color == Piece.black ? attackedByWhitePositions : attackedByBlackPositions;
        if(Square[pEndPos] != leeresFeld)
        {
            //Wenn es eine Figur nicht mehr gibt, kann sie auch keine Felder mehr angreifen

            for (int i = 0; i < enemyAttackedPositions.size(); i++) {
                if(enemyAttackedPositions.get(i)[0] == pEndPos)
                {
                    enemyAttackedPositions.remove();
                }
            }

            //Figur wird aus den Positionen rausgenommen
            LinkedList<Integer> Position = color == Piece.black ? whitePositions : blackPositions;
            Position.remove((Integer) pEndPos); // dadurch das integer verwendet wird, wird nicht der Index entfernt, sondern das Objekt mit dem Wert
        }
        Square[pEndPos] = pPieceValue;
        Square[pStartPos] = leeresFeld;

        int[][] newMoves = getLegalMoves(pPieceValue,pEndPos,Square,enemyAttackedPositions,ownAttackedPositions,Positionlist);
        ownAttackedPositions.addAll(Arrays.asList(newMoves));

    }

    private void addToAttackedPositions(int pEigenePosition, int pAttackedSquare, LinkedList<int[]> pAttackedPositions)
    {
        pAttackedPositions.add(new int[]{pEigenePosition,pAttackedSquare});
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
            System.out.println("ACHTUNG FEHLER!!");
        }
        // Wert ==0
        return Square[pFeld] == 0;
    }

    /**
     * setter-methode um das square-array zu verändern mit
     * @param pIndex an welchem square-index
     * @param pValue welchen wert (Piece)
     */
    public void setSquare(int pIndex, int pValue){
        Square[pIndex] = pValue;
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
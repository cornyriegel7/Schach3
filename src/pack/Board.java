package pack;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Board extends JPanel {
    View view; //das ist die gui
    int[] Square;
    private static final int leeresFeld = 0;
    static int[][] distancesToEdge;


    static int oben = -8;
    static int unten = 8;
    static int rechts = 1;
    static int links = -1;
    static int obenrechts = -7;
    static int untenlinks = 7;
    static int obenlinks = -9;
    static int untenrechts = 9;
    
    static final int[] directions = {oben, unten,
            rechts, links,
            untenlinks, obenrechts,
            untenrechts, obenlinks};

    //Größe der Rechtecke
    public int titleSize = 100;
    Piece piece;

    //Debugging
    public int pSquareIndex = 30;

    //Konstruktor Board
    public Board(View pView){
        view = pView;
        Square = new int[64]; //brett als eindimensionales array (von oben links nach unten rechts) ist später praktisch
        distancesToEdge = getDistanceToEdges();

        this.setPreferredSize(new Dimension(8 * titleSize, 8 * titleSize));
        piece = new Piece();
        }



    //Zum Painten des kompletten Felds
    //Wird immer automatisch von Java Swing aufgerufen, wenn es nötig ist.
    //Man kann mit repaint() dem Swing einen Hinweis geben neu zu painten.
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        for(int r = 0; r < 8; r++){
            for(int c = 0; c < 8; c++){
                g2d.setColor((c+r) % 2 == 0 ? new Color(205, 133, 63) : new Color(101, 67, 33));
                g2d.fillRect(c * titleSize, r * titleSize, titleSize, titleSize);
            }
        }
        Square[pSquareIndex] = 2;
        paint(g2d,pieceIntToImage(Square[pSquareIndex]),squareToX(pSquareIndex),squareToY(pSquareIndex));
        System.out.println("X Koordinate Bild: " + squareToX(pSquareIndex) + "   Y-Koordinate Bild: " + squareToY(pSquareIndex));//Debugging
        System.out.println("titleSize: " + titleSize);//Debugging
        System.out.println("Painting component..."); //Debugging
    }


    //Bestimmtes Feld(pSquare Index) färben(um z.B. available Moves anzuzeigen)
    public void paintSquare(int pSquareInt) {
        Graphics g = getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        // Hier kannst du die Farbe für das bestimmte Feld festlegen
        g2d.setColor(new Color(1, 101, 1));

        // Hier zeichnest du das bestimmte Feld
        g2d.fillRect(squareToX(pSquareInt) * titleSize, squareToY(pSquareInt) * titleSize, titleSize, titleSize);
    }

    //Wandelt ein bestimmtes Square(Index pSquare Array) in die Y-Koordinate des Feldes um (linke obere Ecke)
    public int squareToY(int pSquareInt) {
            int minRange = 0;
            int maxRange = 63;
            int yOffset = 0;

            if (pSquareInt >= minRange && pSquareInt <= maxRange) {
                yOffset = (pSquareInt / 8) * 100;


                return yOffset;

            } else {
            // Umgang mit ungültigen pSquare-Werten(kann man rausmachen, aber so weiß man, wenn etwas falsches rauskommt)
            return -1;
        }

    }

    //Wandelt ein bestimmtes Square(Index pSquare Array) in die X-Koordinate des Feldes um (linke obere Ecke)
    public int squareToX(int pSquareInt) {
        if (pSquareInt >= 0 && pSquareInt < 64) {
            return (pSquareInt % 8) * 100;
        } else {
            // Umgang mit ungültigen pSquare-Werten
            return -1;
        }
    }

    //"Wandelt" den Inhalt des Arrays in ein Bild um und returnt das Bild.
    private  Image pieceIntToImage(int pPieceInt)
    {
        int absolutFigurInt = Math.abs(pPieceInt);
       int farbe = pPieceInt / absolutFigurInt;

        return switch (absolutFigurInt) {
            case (Piece.king) -> farbe == Piece.black ? piece.getImage(piece.getSheetScale(), piece.getSheetScale(), titleSize, titleSize) : piece.getImage(piece.getSheetScale(), 0, titleSize, titleSize);
            case (Piece.pawn) -> farbe == Piece.black ? piece.getImage(6 * piece.getSheetScale(), piece.getSheetScale(), titleSize, titleSize) : piece.getImage(6 * piece.getSheetScale(), 0, titleSize, titleSize);
            case (Piece.knight) -> farbe == Piece.black ? piece.getImage(4 * piece.getSheetScale(), piece.getSheetScale(), titleSize, titleSize) : piece.getImage(4 * piece.getSheetScale(), 0, titleSize, titleSize);
            case (Piece.bishop) -> farbe == Piece.black ? piece.getImage(3 * piece.getSheetScale(), piece.getSheetScale(), titleSize, titleSize) : piece.getImage(3 * piece.getSheetScale(), 0, titleSize, titleSize);
            case (Piece.rook) -> farbe == Piece.black ? piece.getImage(5 * piece.getSheetScale(), piece.getSheetScale(), titleSize, titleSize) : piece.getImage(5 * piece.getSheetScale(), 0, titleSize, titleSize);
            case (Piece.queen) -> farbe == Piece.black ? piece.getImage(2 * piece.getSheetScale(), piece.getSheetScale(), titleSize, titleSize) : piece.getImage(2 * piece.getSheetScale(), 0, titleSize, titleSize);
            default -> null;


        };
    }

    //Malt ein Bild an den Kordinaten x & y
    public void paint(Graphics2D g2d, Image img, int xPos, int yPos){
        g2d.drawImage(img,xPos,yPos,null);
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
     *
     * @param startPosition startPosition auf dem brett
     * @param figurInt den Integer ert einer Figur auf dem Brett
     * @return die Moves die gehen würden, wenn keine anderen Figuren auf dem Feld wären
     */
    public  Move[] generateMoves(int startPosition, int figurInt, int[] pSquares)
    {
        int absolutFigurInt = Math.abs(figurInt);
        if(absolutFigurInt == Piece.pawn)
        {
            return generatePawnMoves(startPosition,figurInt,pSquares);
        }
        else if(absolutFigurInt == Piece.queen || absolutFigurInt == Piece.rook || absolutFigurInt == Piece.bishop)
        {
            return generateSlidingPieceMoves(startPosition,figurInt,pSquares);
        }
        else if(absolutFigurInt == Piece.knight)
        {
            return generateKnightMoves(startPosition,figurInt,pSquares);
        }
        return null;
    }

    /**
     * generiert die möglichen Moves für Laeufer, Turm, Dame wenn
     * @param startPos aktuelle Position der Figur und
     * @param FigurInt Farbe + Art der Figur bekannt sind
     * @return Array an allen möglichen Moves
     */
    public Move[] generateSlidingPieceMoves(int startPos, int FigurInt, int[] pSquares)
    {
        ArrayList<Move> moves = new ArrayList<>();
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
                if(pSquares[square] == leeresFeld)
                {
                    moves.add(new Move(startPos,square));

                }
                else
                {
                    int eigeneFarbe = FigurInt / Math.abs(FigurInt);
                    int farbeAndereFigur = pSquares[square] / Math.abs(pSquares[square]);
                    if(eigeneFarbe == farbeAndereFigur) // wenn eigene Farbe auf Feld
                    {
                        break;
                    }
                    else // wenn Gegner auf Feld
                    {
                        moves.add(new Move(startPos,square));

                        break;
                    }
                }

            }
        }
        return moves.toArray(new Move[0]);
    }

    public Move[] generateKnightMoves(int startPos, int FigurInt, int[] pSquares)
    {
        int eigeneFarbe = FigurInt / Math.abs(FigurInt);
        ArrayList<Move> moves = new ArrayList<>();

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
                moves.add(new Move(startPos,neuersquare));
                System.out.println(neuersquare);

            }



        }
        return moves.toArray(new Move[0]);
    }

    public Move[] generatePawnMoves(int startPos, int FigurInt, int[] pSquares)
    {
        ArrayList<Move> moves = new ArrayList<>();

        int eigeneFarbe = FigurInt / Math.abs(FigurInt);
        int bewegungsrichtung = -8 * eigeneFarbe; //Weiß geht von höheren Zahlen zu niedrigeren#


        /*Dadurch dass Zuerst nach moves gesucht wird, bei denen Bauern andere schlagen (ist immer mindestens ein gleichwertiger Trade),
        sind gute Moves am Anfang der Liste was z.B. bei Minimax mit Alpha-Beta-Pruning Sinn macht
        */
        int neuersquare = startPos+bewegungsrichtung+1;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == eigeneFarbe*-1) // Ist vorne und 1 nach rechts ein Gegner
        {
            moves.add(new Move(startPos,neuersquare));
            System.out.println(neuersquare);
        }
        neuersquare = startPos+bewegungsrichtung-1;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == eigeneFarbe*-1) // Ist vorne und 1 nach links ein Gegner
        {
            moves.add(new Move(startPos,neuersquare));System.out.println(neuersquare);

        }


        neuersquare = startPos+bewegungsrichtung;
        if((0 <= neuersquare && neuersquare < 64) && pSquares[neuersquare] == leeresFeld)
        {
            moves.add(new Move(startPos,neuersquare));System.out.println(neuersquare);

        }

        return moves.toArray(new Move[0]);
    }

}
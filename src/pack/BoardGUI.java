package pack;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class BoardGUI extends JPanel {
    Controller c;

    Piece piece;
    Input input;
    public static int titleSize = 100;
    public int squareIndex = 0;
    public int[] Square;

    public BoardGUI(Controller pController) {

        c = pController;

        this.setPreferredSize(new Dimension(8 * titleSize, 8 * titleSize));
        Square = c.board.giveBoard();
        // Hinzufügen des Input-Listeners zum Board
        this.addMouseListener(c.input);
        this.addMouseMotionListener(c.input);
    }
        //Zum Painten des kompletten Felds
        //Wird immer automatisch von Java Swing aufgerufen, wenn es nötig ist.
        //Man kann mit repaint() dem Swing einen Hinweis geben neu zu painten.



            public void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;
                System.out.println("PaintComponent");

                //Das Brett painten
                for(int r = 0; r < 8; r++){
                    for(int c = 0; c < 8; c++){
                        g2d.setColor((c+r) % 2 == 0 ? new Color(205, 133, 63) : new Color(101, 67, 33));
                        g2d.fillRect(c * titleSize, r * titleSize, titleSize, titleSize);
                    }
                }

                //paint highlights

                //Führe aus WENN ein Piece ausgewählt ist
               if(c.input.getSelectedPieceValue() != 0) {

                   int i;

                   //Führe die Schleife so oft aus wie legalMoves Endpositionen hat

                   for (i = 0; i < Input.legalMoves.length; i++){
                       //paintComponent auf Grün setzen fürs painten der availableMoves
                       g2d.setColor(new Color(46, 217, 27, 202));
                       //Wandle den Index des Squares (gegeben vom LegalMovesArray) in Koordinaten um und painte es quasi genau über die andere Farbe des Feldes
                       g2d.fillRect(squareToX(c.input.legalMoves[i][1]), squareToY(c.input.legalMoves[i][1]), titleSize, titleSize);
                   }
               }


               //Die Pieces painten
                for(int i = 0; (i < Square.length); i++) {
                    squareIndex = i;
                    Image img = pieceIntToImage(Square[squareIndex]);
                    g2d.drawImage(img, squareToX(squareIndex), squareToY(squareIndex), null);
                }

                if(c.input.getSelectedPieceValue() != 0) {
                    //selectedPieces werden größer angezeigt!!!
                    Image selectedPiece = pieceIntToImage(c.input.getSelectedPieceValue()).getScaledInstance(120, 120, BufferedImage.SCALE_SMOOTH);
                    g2d.drawImage(selectedPiece, c.input.getxE() - titleSize / 2, c.input.getyE() - titleSize / 2, null);
                }

            }









        //Bestimmtes Feld(pSquare Index) färben(um z.B. available Moves anzuzeigen)
        public void paintSquare(int pSquareInt) {
        System.out.println("wRAAAAH MALEN");
            Graphics g = getGraphics();
            Graphics2D g2d = (Graphics2D) g;

            // Hier kannst du die Farbe für das bestimmte Feld festlegen
            g2d.setColor(new Color(0, 255, 0, 186));

            // Hier zeichnest du das bestimmte Feld
            g2d.fillRect(squareToX(pSquareInt) * titleSize, squareToY(pSquareInt) * titleSize, titleSize, titleSize);
        }


    //"Wandelt" den Inhalt des Arrays in ein Bild um und returnt das Bild.
    private  Image pieceIntToImage(int pPieceInt)
    {
        if(pPieceInt==0) return null; //man kann nicht durch 0 teilen

        int absolutFigurInt = Math.abs(pPieceInt);
        int farbe = pPieceInt / absolutFigurInt;

        return switch (absolutFigurInt) {
            case (Piece.king) -> farbe == Piece.black ? c.piece.getSubImage(0* c.piece.getSheetScale(), c.piece.getSheetScale()) : c.piece.getSubImage(0*c.piece.getSheetScale(), 0);
            case (Piece.pawn) -> farbe == Piece.black ? c.piece.getSubImage(5 * c.piece.getSheetScale(), c.piece.getSheetScale()) : c.piece.getSubImage(5 * c.piece.getSheetScale(), 0);
            case (Piece.knight) -> farbe == Piece.black ? c.piece.getSubImage(3 * c.piece.getSheetScale(), c.piece.getSheetScale()) : c.piece.getSubImage(3 * c.piece.getSheetScale(), 0);
            case (Piece.bishop) -> farbe == Piece.black ? c.piece.getSubImage(2 * c.piece.getSheetScale(), c.piece.getSheetScale()) : c.piece.getSubImage(2 * c.piece.getSheetScale(), 0);
            case (Piece.rook) -> farbe == Piece.black ? c.piece.getSubImage(4 * c.piece.getSheetScale(), c.piece.getSheetScale()) : c.piece.getSubImage(4 * c.piece.getSheetScale(), 0);
            case (Piece.queen) -> farbe == Piece.black ? c.piece.getSubImage(1 * c.piece.getSheetScale(), c.piece.getSheetScale()) : c.piece.getSubImage(1 * c.piece.getSheetScale(), 0);
            default -> null;
        };

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

    public void setSquare(int[] pSquare) {
        Square = pSquare;
    }
}

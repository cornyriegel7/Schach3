package pack;

import javax.swing.*;
import java.awt.*;

public class BoardGUI extends JPanel {
    public static int titleSize = 100;
    public Board board;
    public int squareIndex = 0;
    Piece piece;
    Input input;
    public int[] Square;

    public BoardGUI(Board pBoard) {
        piece = new Piece();
        board = pBoard;
        input = new Input(board);
        this.setPreferredSize(new Dimension(8 * titleSize, 8 * titleSize));
        Square = pBoard.giveBoard();
        // Hinzufügen des Input-Listeners zum Board
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
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

            for(int i = 0; (i < Square.length); i++) {
                squareIndex = i;
                Image img = pieceIntToImage(Square[squareIndex]);
                g2d.drawImage(img, squareToX(squareIndex), squareToY(squareIndex), null);
            }

            Image selectedPiece = pieceIntToImage(Input.getSelectedPieceValue());
            g2d.drawImage(selectedPiece, Input.getxE() - titleSize/2,Input.getyE() - titleSize/2,null);

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


    //"Wandelt" den Inhalt des Arrays in ein Bild um und returnt das Bild.
    private  Image pieceIntToImage(int pPieceInt)
    {
        if(pPieceInt==0) return null; //man kann nicht durch 0 teilen

        int absolutFigurInt = Math.abs(pPieceInt);
        int farbe = pPieceInt / absolutFigurInt;

        return switch (absolutFigurInt) {
            case (Piece.king) -> farbe == Piece.black ? piece.getImage(0*piece.getSheetScale(), piece.getSheetScale()) : piece.getImage(0*piece.getSheetScale(), 0);
            case (Piece.pawn) -> farbe == Piece.black ? piece.getImage(5 * piece.getSheetScale(), piece.getSheetScale()) : piece.getImage(5 * piece.getSheetScale(), 0);
            case (Piece.knight) -> farbe == Piece.black ? piece.getImage(3 * piece.getSheetScale(), piece.getSheetScale()) : piece.getImage(3 * piece.getSheetScale(), 0);
            case (Piece.bishop) -> farbe == Piece.black ? piece.getImage(2 * piece.getSheetScale(), piece.getSheetScale()) : piece.getImage(2 * piece.getSheetScale(), 0);
            case (Piece.rook) -> farbe == Piece.black ? piece.getImage(4 * piece.getSheetScale(), piece.getSheetScale()) : piece.getImage(4 * piece.getSheetScale(), 0);
            case (Piece.queen) -> farbe == Piece.black ? piece.getImage(1 * piece.getSheetScale(), piece.getSheetScale()) : piece.getImage(1 * piece.getSheetScale(), 0);
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

//    public void getString(String text){
//
//        if(text.equals("OKOK")){
//            text = "Server: Verbindung gewährt!";
//            lconnected.setText("Sie sind Verbunden!");
//
//        }
//        else if (text.equals ("RONL"))
//        {
//            text = "Raum " + tbRaumName.getText() + " wurde erstellt!" ;
//            cbChats.addItem(tbRaumName.getText());
//            cbChatNamenList.add(tbRaumName.getText());
//            raumFrame.dispose();
//        }
//        char[] ersteVierZeichen = new char[4];
//        if (text.length() > 3) {
//            for (int i = 0; i < 4; i++) {
//                //Fehler bei einem Zeichen
//                ersteVierZeichen[i] = text.toCharArray()[i];
//            }
//            String command = String.valueOf(ersteVierZeichen);
//
//            switch (command)
//            {
//                case("RONL"):
//                    String tempRaumName = text.replace("RONL","");
//                    text = "Server: Du wurdest zu Raum " + tempRaumName + " hinzugefuegt";
//                    cbChats.addItem(tempRaumName);
//                    cbChatNamenList.add(tempRaumName);
//                    break;
//
//                case("MSSG"):
//                    text = text.replace("MSSG","");
//                    String[] a = text.split("RNAM");
//
//                    String nachricht = a[0];
//                    String tmpRaumName = a[1];
//
//                    if(tmpRaumName.equals(cbChatNamenList.get(cbChats.getSelectedIndex())))
//                    {
//                        tAchatText.append( nachricht + "\r\n");
//                        return;
//                    }
//                    else
//                    {
//                        tAchatText.append(nachricht + " (in Raum " + tmpRaumName + ")\r\n");
//                        return;
//                    }
//                case("RLEV"):
//                    text = text.replace("RLEV","");
//                    cbChats.removeItem(cbChats.getItemAt(cbChats.getSelectedIndex()));
//                    cbChatNamenList.remove(text);
//                    return;
//            }
//        }
//        if(text.equals("DELG")) { tAchatText.setText(""); return;}
//        tAchatText.append(text + "\r\n");
//    }
}

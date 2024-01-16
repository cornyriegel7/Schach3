package pack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece {
    // Figur kann an einem int erkannt werden denn: Farbe * FigurTyp
    static final int black = -1;
    static final int white = 1; // moeglicherweise redundant, so aber verständlicher vllt


    static final int pawn = 1;
    static final int knight = 2;
    static final int bishop = 3;
    static final int rook = 4;
    static final int queen = 5;
    static final int king = 6;

    //Für die Bilder der Pieces
    BufferedImage sheet;
    {
        try{
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("pieces.png"));
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    protected int SheetScale = sheet.getWidth()/6;

    public int getSheetScale(){
        return SheetScale;
    }

    public Image getImage(int XImagePiece,int YImagePiece,int w, int h) {
        return sheet.getSubimage(XImagePiece,YImagePiece, w, h); //Die ersten beiden legen den Teil des Bildes fest, der ausgeschnitten wird und die letzten beiden die Größe
    }

    Image sprite;

}

package pack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece {
    // Figur kann an einem int erkannt werden denn: Farbe * FigurTyp
    static final int black = -1, white = 1;
    static final int pawn = 1, knight = 2, bishop = 3, rook = 4, queen = 5, king = 6;
    //Für die Bilder der Pieces
    BufferedImage sheet;
    public int sheetScale;

    public Piece() {
        try {
            sheet = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("pieces.png")));
            sheetScale = sheet.getWidth() / 6;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSheetScale() {
        return sheetScale;
    }

    public Image getSubImage(int XImagePiece, int YImagePiece) {
        //diese line nur test
        return sheet.getSubimage(XImagePiece,YImagePiece,sheetScale,sheetScale).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH); //Die ersten beiden legen den Teil des Bildes fest, der ausgeschnitten wird und die letzten beiden die Größe
    }


    public Image getImage(int pValue) {
        //gibt bild für schwarz/weiß
        if(pValue>0)
        return sheet.getSubimage(0,0,sheetScale*5,sheetScale).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH); //Die ersten beiden legen den Teil des Bildes fest, der ausgeschnitten wird und die letzten beiden die Größe
        else return sheet.getSubimage(0,sheetScale,sheetScale*5,sheetScale).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH);
    }
}

package pack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece {
    Controller c;
    // Figur kann an einem int erkannt werden denn: Farbe * FigurTyp
    static final int black = -1, white = 1;
    static final int pawn = 1, knight = 2, bishop = 3, rook = 4, queen = 5, king = 6;
    static final int[] pieces = new int[]{pawn,knight,bishop,queen,king};
    //Für die Bilder der Pieces
    BufferedImage sheet;
    public int sheetScale;

    public Piece(Controller pController) {
        c = pController;
        try {
            sheet = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("schwarz-weiß.png")));
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

    public void changeSkins(String pSkin)
    {
        switch(pSkin){
            case("schwarz-weiß") -> sheet = readPicture("schwarz-weiß.png");
            case("pink-blau") -> sheet = readPicture("pink-blau-ohne-apo.png");
            case("pink-blau-apo") -> sheet = readPicture("pink-blau-mit-apo.png");
            case("orange-lila") -> sheet = readPicture("orange-lila.png");
            case("hehe") -> sheet = readPicture("hehe.png");
        }
        c.boardGUI.repaint();
    }

    public BufferedImage readPicture(String pName){
        try {
            return ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(pName)));
        } catch (IOException e) {
             return null;
        }
    }
}

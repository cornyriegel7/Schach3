package pack;

import java.awt.*;

public class Controller {

    View view;
    Board board;
    public Controller(View pView){
        view = pView;
        board = new Board(view);


    }
    /**
    TODO: soll auf die generateMoves-Methode in Board zugreifen, alle Moves für die Figur auf dem Startsqure generieren, dann loopen ob der move ok ist, und wenns okay ist "true" zurückgeben 
    */
    public boolean handleMove(int startsquare, int endsquare)
    {
    return false;
    }
}

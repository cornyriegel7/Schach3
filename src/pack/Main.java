package pack;

public class Main {

    public static void main(String[] args) {
        //Programmstart
        View view = new View();
        //view.c.board.getLegalMoves( view.c.board.whitePositions,view.c.board.Square,view.c.board.attackedByBlackPositions,view.c.board.attackedByWhitePositions);
    }
}


/*
Zum Verständnis: Main erstellt view --> view erstellt Controller und gibt sich selbst weiter
Controller erstellt Board und gibt view an Board weiter.
*/

//TODO:die idee war eigentlich, dass dann dadurch board auf view zugreifen kann und die methoden von view benutzt um zu malen also z.b: view.paint()

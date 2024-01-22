package pack;
public class Controller {

    View view;
    Board board;
    Server server;
    Client client;
    public Controller(View pView){
        view = pView;
        //todo: noch nicht final ,sowohl client als auch server brauchen noch args. außerdem server als interface hinzufügen
        server = new Server();
        client = new Client();
    }
    /**
    TODO: soll auf die generateMoves-Methode in Board zugreifen, alle Moves für die Figur auf dem Startsqure generieren, dann loopen ob der move ok ist, und wenns okay ist "true" zurückgeben 
    */
    public boolean handleMove(int startsquare, int endsquare)
    {
    return false;
    }
    public void createBoard() {
        board = new Board(view);
    }
}

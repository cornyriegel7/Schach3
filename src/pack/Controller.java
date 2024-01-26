package pack;
public class Controller {
    View view;
    Board board;
    Server server;
    Client client;
    public Controller(View pView) {
        view = pView;
        //todo: noch nicht final ,sowohl client als auch server brauchen noch args. außerdem server als interface hinzufügen
        server = new Server();
        client = new Client();
    }
    public void createBoard() {
        board = new Board(view);
    }
}
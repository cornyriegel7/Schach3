package pack;
public class Controller {
    View view;
    Board board;
    Chatserver chatServer;
    ChatClient chatClient;
    public Controller(View pView) {
        view = pView;
        //todo: noch nicht final ,sowohl client als auch server brauchen noch args. außerdem server als interface hinzufügen
        //chatServer = new Chatserver();
        //chatClient = new ChatClient();
    }
    public void createBoard() {
        board = new Board(view);
    }
}
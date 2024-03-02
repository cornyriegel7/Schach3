package pack;

public class Controller {
    View view;
    Piece piece;
    Board board;
    Input input;
    BoardGUI boardGUI;
    Chatserver chatServer;
    ChatClient chatClient;
    public int dran;
    public Controller(View pView) {
        view = pView;
        dran = Piece.white;
        //todo: noch nicht final ,sowohl client als auch server brauchen noch args. außerdem server als interface hinzufügen
        //createChatClient();
    }

    public void restart() {

    }

    public void createChatClient()
    {
        if(view.getPickedMode() == 2){
            chatClient = new ChatClient(view.getIp(), view.getPickedPort(), this);
        }
    }

    public void createChatServer(int pPort)
    {
        if(view.getPickedMode() == 2){
            chatServer = new Chatserver(pPort);
        }
    }

    public void createBoard() {
        piece = new Piece(this);
        board = new Board(this);
        input = new Input(this);
        boardGUI = new BoardGUI(this);
    }
    public void setDran(int color)
    {
        dran = color;
    }

}
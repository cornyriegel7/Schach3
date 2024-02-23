package pack;
public class Controller {
    View view;
    Board board;
    Chatserver chatServer;
    ChatClient chatClient;
    public int dran;
    public Controller(View pView) {
        view = pView;
        dran = Piece.white;
        //todo: noch nicht final ,sowohl client als auch server brauchen noch args. außerdem server als interface hinzufügen
        createChatClient();
    }

    public void createChatClient()
    {
        if(view.getPickedMode() == 2){
            //chatServer = new Chatserver();
            chatClient = new ChatClient(view.getIp(), view.getPort(), this);
        }
    }
    public void createBoard() {
        board = new Board(view);
    }
    public void setDran(int color)
    {
        dran = color;
    }

}
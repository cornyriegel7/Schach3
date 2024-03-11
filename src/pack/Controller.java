package pack;

import java.util.LinkedList;

public class  Controller {
    View view;
    Piece piece;
    Board board;
    Input input;
    BoardGUI boardGUI;
    Chatserver chatServer;
    ChatClient chatClient;
    Bot bot;
    public int dran;
    public Controller(View pView) {
        view = pView;
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
        bot = new Bot(board);
    }

    public void vsBot()
    {
        input.setActive(false);
        //Bot tests
        LinkedList<Integer> ownPos = dran == Piece.white ? board.whitePositions : board.blackPositions;
        LinkedList<Integer> enemyPos = dran == Piece.black ? board.whitePositions : board.blackPositions;
        LinkedList<int[]> ownAttacked = dran == Piece.white ? board.attackedByWhitePositions : board.attackedByBlackPositions ;
        LinkedList<int[]> enemyAttacked = dran == Piece.black ? board.attackedByWhitePositions : board.attackedByBlackPositions ;
        int[] move = (bot.getMove(board.Square,dran,ownPos,enemyPos,ownAttacked,enemyAttacked,board.specialMovePositions));
        if(move == null)
        {
            boolean hasWhiteLost = dran == Piece.white;
            view.checkMateMessage(hasWhiteLost);
        }
        else {
            board.execMove(move[0], move[1], move[2]);
            dran *= -1;
            input.setActive(true);
        }
    }
    public void setDran(int color)
    {
        dran = color;
    }

}


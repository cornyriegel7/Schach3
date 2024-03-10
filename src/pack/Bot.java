package pack;

import java.util.*;

public class Bot {
    // Alle Ratings aus der Perspektive von unten
    final int[] pawnPosRating = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };
    final int[] knightPosRating = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -35,  5, 10, 15, 15, 10,  5,-35,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };
    final int[] bishopPosRating = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };
    final int[] rookPosRating = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };
    final int[] queenPosRating = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };
    final int[] kingMidGame = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
    };
    final int[] kingLateGame = {
            -50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50
    };

    double gameStatus; // soll null sein wenn das Spiel anfängt und sich gegen 1 bewegen desto weiter das Spiel fortschreitet
    Board board;
    public Bot(Board board) {
        gameStatus = 0;
        this.board = board;
    }

    /**
     * Funktion soll sehr grobb bestimmen ob das Spiel in der Endphase ist. Dazu wird bewertet wie viele Pieces (die nicht Bauer oder Koenig sind)
     * noch vorhanden sind. 1 = Engame, 0 = Eroeffnung
     * @return 1 = Engame, 0 = Eroeffnung
     */

    public double updateGameStatus(int[] pSquares, LinkedList<Integer> whitePositions, LinkedList<Integer> blackPositions)
    {
        int heavyPieceCount = 0;
        for (int i = 0; i < whitePositions.size(); i++) {
            if(pSquares[whitePositions.get(i)] != Piece.pawn && pSquares[whitePositions.get(i)] != Piece.king)
            {
                heavyPieceCount+= pSquares[whitePositions.get(i)];
            }
        }
        for (int i = 0; i < blackPositions.size(); i++) {
            if(pSquares[blackPositions.get(i)] != -Piece.pawn && pSquares[blackPositions.get(i)] != -Piece.king)
            {
                heavyPieceCount += -pSquares[blackPositions.get(i)];
            }
        }
        if(heavyPieceCount > 21)
        {
            return -0.025 * heavyPieceCount + 1.2;
        }
        return -0.015 * heavyPieceCount + 1;
    }

    /**
     * Werte ueber 0 wenn weiß besser ist, Werte unter null, wenn schwarz besser ist
     * @param pSquares
     * @param whitePositions
     * @param blackPositions
     * @return
     */
    public int evaluation(int[] pSquares, LinkedList<Integer> whitePositions, LinkedList<Integer> blackPositions){
        double gameStatus = updateGameStatus(pSquares,whitePositions,blackPositions);
        int balancing = 23;
        int whiteMaterial = 0;
        int pos;
        for (int i = 0; i < whitePositions.size(); i++) {
            pos = whitePositions.get(i);
            whiteMaterial += pSquares[pos] * balancing;
            whiteMaterial +=positionEvaluation(gameStatus,pos,pSquares);
        }
        int blackMaterial = 0;
        for (int i = 0; i < blackPositions.size(); i++) {
            pos = blackPositions.get(i);
            int PosIndexReversed = 63 - pos;
            blackMaterial += pSquares[pos] * balancing * -1;

            blackMaterial += positionEvaluation(gameStatus,PosIndexReversed,pSquares);
        }
        return  whiteMaterial - blackMaterial;
    }

    /**
     * passt die Tiefe grobb darauf an, wie viele Moves gemacht werden koennen
     * @param moveCount
     * @return
     */
    public int getDepth(int moveCount)
    {
        if(moveCount >= 40)
        {return 4;}

        return (int) (-0.058 * moveCount + 6.28);
    }
    public int positionEvaluation(double pGameStatus, int index, int[] pSquares)
    {
        switch (Math.abs(pSquares[index]))
        {
            case (Piece.king): return (int) (kingMidGame[index] * (1 - pGameStatus) + kingLateGame[index] * pGameStatus);
            case (Piece.queen): return queenPosRating[index];
            case (Piece.rook) : return  rookPosRating[index];
            case (Piece.bishop): return bishopPosRating[index];
            case (Piece.knight): return knightPosRating[index];
            default: return pawnPosRating[index];

        }
    }
    public int[] getMove(int[] pSquares, int pDran, LinkedList<Integer> ownPos, LinkedList<Integer> enemyPos, LinkedList<int[]> ownAttacked, LinkedList<int[]> enemyAttacked, ArrayList<Integer> spezialMoves)
    {
        long startTime = System.currentTimeMillis();
        LinkedList<int[]> moves = new LinkedList<>();
        int[] moveTotal = null;
        int total = pDran == Piece.white ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        LinkedList<Integer> whitePos = pDran == Piece.white ?  ownPos: enemyPos;
        LinkedList<Integer> blackPos = pDran == Piece.black ?  ownPos: enemyPos;


        LinkedList<Integer> ownPosN,enemyPosN;
        LinkedList<int[]> ownAttackedN, enemyAttackedN;
        ArrayList<Integer> spezialMovesN;
        int[] SquareN = new int[64];
        ownPosN = new LinkedList<>(ownPos);
        enemyPosN = new LinkedList<>(enemyPos);
        ownAttackedN = new LinkedList<>(ownAttacked);
        enemyAttackedN = new LinkedList<>(enemyAttacked);
        spezialMovesN = new ArrayList<>(spezialMoves);
        System.arraycopy(pSquares,0,SquareN,0,pSquares.length);

        for (int i = 0; i < ownPos.size(); i++) {
           moves.addAll(Arrays.stream(board.generateLegalMoves(ownPosN.get(i),SquareN[ownPosN.get(i)],SquareN,ownAttackedN,enemyAttackedN,ownPosN,spezialMovesN)).toList());
        }

        MoveComparator moveComparator = new MoveComparator(pSquares);
        moves.sort(moveComparator);

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int depth = getDepth(moves.size());
        System.out.println(depth);

        for (int i = 0; i < moves.size(); i++) {


            //System.out.println("MINMAX:"+i);
            int[] move = moves.get(i);
            board.execMove(move[0],move[1],move[2],SquareN,ownAttackedN,enemyAttackedN,ownPosN,enemyPosN,spezialMovesN);




             //evaluation(SquareN,whitePos,blackPos);
            int ev = minimax(SquareN,pDran * -1,alpha,beta,depth -1,enemyPosN,ownPosN,enemyAttackedN,ownAttackedN,spezialMovesN);
            //System.out.println(ev);
            if(pDran == Piece.white && ev > total)
            {
                total = ev;
                moveTotal = move;
                alpha = ev;
            }
            else if(pDran == Piece.black && ev < total)
            {
                total = ev;
                moveTotal = move;
                beta = ev;
            }
            if(alpha >= beta)
            {
                break;
            }
            ownPosN = new LinkedList<>(ownPos);
            enemyPosN = new LinkedList<>(enemyPos);
            ownAttackedN = new LinkedList<>(ownAttacked);
            enemyAttackedN = new LinkedList<>(enemyAttacked);
            spezialMovesN = new ArrayList<>(spezialMoves);
            System.arraycopy(pSquares,0,SquareN,0,pSquares.length);
        }
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println(execTime);
       // System.out.println(total);
        return moveTotal;
    }
    public int minimax(int[] pSquares, int pDran,int alpha, int beta, int depth,
                       LinkedList<Integer> ownPos, LinkedList<Integer> enemyPos, LinkedList<int[]> ownAttacked, LinkedList<int[]> enemyAttacked, ArrayList<Integer> spezialMoves)
    {
        LinkedList<int[]> moves = new LinkedList<>();
        int total = pDran == Piece.white ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        LinkedList<Integer> whitePos = pDran == Piece.white ?  ownPos: enemyPos;
        LinkedList<Integer> blackPos = pDran == Piece.black ?  ownPos: enemyPos;
        if(whitePos.size() + blackPos.size() <5 && board.isTie(whitePos,blackPos,pSquares)) // Unentschieden
        {
            return 0;
        }
        int[] SquareN = new int[64];
        System.arraycopy(pSquares,0,SquareN,0,pSquares.length);
        if(depth == 0)
        {
            return evaluation(SquareN,whitePos,blackPos);
        }
        LinkedList<Integer> ownPosN,enemyPosN;
        LinkedList<int[]> ownAttackedN, enemyAttackedN;
        ArrayList<Integer> spezialMovesN;

        ownPosN = new LinkedList<>(ownPos);
        enemyPosN = new LinkedList<>(enemyPos);
        ownAttackedN = new LinkedList<>(ownAttacked);
        enemyAttackedN = new LinkedList<>(enemyAttacked);
        spezialMovesN = new ArrayList<>(spezialMoves);


        for (int i = 0; i < ownPos.size(); i++) {
            moves.addAll(Arrays.stream(board.generateLegalMoves(ownPosN.get(i),SquareN[ownPos.get(i)],SquareN,ownAttackedN,enemyAttackedN,ownPosN,spezialMovesN)).toList());
        }
        if(moves.size() == 0 )
        {
            int[][] attacksOnKing = board.getAttacksOnKing(board.getKingPos(pSquares,ownPos),pSquares,ownPos,enemyAttacked);
            if(attacksOnKing.length == 0) // Patt -> unentschieden
            {
                return 0;
            }
            else // Schachmatt
            {
                return Integer.MIN_VALUE * pDran;
            }
        }
        MoveComparator moveComparator = new MoveComparator(pSquares);
        moves.sort(moveComparator);
        for (int i = 0; i < moves.size(); i++) {
            int[] move = moves.get(i);
            board.execMove(move[0],move[1],move[2],SquareN,ownAttackedN,enemyAttackedN,ownPosN,enemyPosN,spezialMovesN);

            int ev = minimax(SquareN,pDran * -1,alpha,beta,depth -1,enemyPosN,ownPosN,enemyAttackedN,ownAttackedN,spezialMovesN);

            if(pDran == Piece.white && ev > total)
            {
                total = ev;
                alpha = ev;
            }
            else if(pDran == Piece.black && ev < total)
            {
                total = ev;
                beta = ev;
            }
            if(alpha >= beta)
            {
                break;
            }
            ownPosN = new LinkedList<>(ownPos);
            enemyPosN = new LinkedList<>(enemyPos);
            ownAttackedN = new LinkedList<>(ownAttacked);
            enemyAttackedN = new LinkedList<>(enemyAttacked);
            spezialMovesN = new ArrayList<>(spezialMoves);
            System.arraycopy(pSquares,0,SquareN,0,pSquares.length);
        }
        return total;
    }



    private class MoveComparator implements Comparator<int[]>
    {
        private int[] Square;
        public MoveComparator(int[] pSquare)
        {
            this.Square = pSquare;
        }

        @Override
        public int compare(int[] o1, int[] o2) {
            if(predictMoveValue(o1,Square) > predictMoveValue(o2,Square))
            {
                return -1;
            }
            else if(predictMoveValue(o1,Square) < predictMoveValue(o2,Square))
            {
                return 1;
            }
            return 0;
        }

        public int predictMoveValue(int[] move, int[] pSquare)
        {
            int promoBonus = 5;
            int predict = 0;
            int ownPieceValue = Math.abs(pSquare[move[0]]);
            int enemyPieceValue = 0;
            if(pSquare[move[1]] != 0)
            {
                enemyPieceValue = Math.abs(pSquare[move[1]]);
            }
            predict += enemyPieceValue - ownPieceValue; // Zuege, bei denen Figuren mit groesserem Wert als die eigene geschlagen werden sind gut
            if(move[2] == Piece.pawn && (move[1] / 8 == 0 || move[1] / 8 == 7)) // Bauern umzuwandeln ist auch eig immer gut
            {
                predict += promoBonus;
            }
            return predict;
        }
    }
}


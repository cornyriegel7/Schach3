package pack;

import java.util.ArrayList;
import java.util.LinkedList;

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
            -30,  5, 10, 15, 15, 10,  5,-30,
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
    public int evaluation(int[] pSquares, LinkedList<Integer> whitePositions, LinkedList<Integer> blackPositions){
        int balancing = 20;
        int whiteMaterial = 0;
        int pos;
        for (int i = 0; i < whitePositions.size(); i++) {
            pos = whitePositions.get(i);
            //System.out.print(board.pieceIntToChar(pSquares[pos]));
            //System.out.println(positionEvaluation(gameStatus,pos,pSquares));
            whiteMaterial += pSquares[pos] * balancing;
            whiteMaterial +=positionEvaluation(gameStatus,pos,pSquares);
        }
        int blackMaterial = 0;
        for (int i = 0; i < blackPositions.size(); i++) {
            pos = blackPositions.get(i);
            int PosIndexReversed = 63 - pos;
            blackMaterial += pSquares[pos] * balancing * -1;
            //System.out.print(board.pieceIntToChar(pSquares[pos]));
           // System.out.println(positionEvaluation(gameStatus,PosIndexReversed,pSquares));

            blackMaterial += positionEvaluation(gameStatus,PosIndexReversed,pSquares);
        }
        return  whiteMaterial - blackMaterial;
    }
    public int positionEvaluation(double pGameStatus, int index, int[] pSquares)
    {
        switch (Math.abs(pSquares[index]))
        {
            case (Piece.king): return (int) (kingMidGame[index] * (1 - gameStatus) + kingLateGame[index] * gameStatus);
            case (Piece.queen): return queenPosRating[index];
            case (Piece.rook) : return  rookPosRating[index];
            case (Piece.bishop): return bishopPosRating[index];
            case (Piece.knight): return knightPosRating[index];
            default: return pawnPosRating[index];

        }
    }
    public int[] getMove(int pSquares, int pDran, LinkedList<Integer> ownPos, LinkedList<Integer> enemyPos, LinkedList<int[]> ownAttacked, LinkedList<int[]> enemyAttacked, ArrayList<Integer> spezialMoves)
    {
        return new int[]{0,0,0};
    }
    public int minimax(int pSquares, int pDran, LinkedList<Integer> ownPos, LinkedList<Integer> enemyPos, LinkedList<int[]> ownAttacked, LinkedList<int[]> enemyAttacked, ArrayList<Integer> spezialMoves, int alpha, int beta)
    {
        return 0;
    }

}


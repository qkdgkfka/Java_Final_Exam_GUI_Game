package Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

import Tetris.AI_Tetris;
import Tetris.Human_Tetris;
import Tetris.tetris_board;

public class TetrisPanel extends JPanel {
    private tetris_board board; // tetris_board 객체
    private AI_Tetris ai; // AI 객체
    private Human_Tetris rightBoardManager; // 오른쪽 보드 관리자 객체

    // 타이머 객체
    private Timer leftTimer;
    private Timer rightTimer;

    public TetrisPanel(tetris_board board) {
        this.board = board;
        this.ai = board.AI;
        this.rightBoardManager = board.rightBoardManager;

        this.setFocusable(true); // 키 이벤트를 받을 수 있도록 설정
        this.requestFocusInWindow(); // 키 이벤트를 받을 수 있도록 포커스 요청

        this.setBackground(Color.WHITE); // 배경을 흰색으로 설정
        
        // 키 이벤트 리스너 추가
        this.addKeyListener(board.new KeyListener());
  
        leftTimer = new Timer((int)ai.TIME, e -> repaint()); // AI 타이머 시작
        leftTimer.start();

        rightTimer = new Timer((int)rightBoardManager.time, e -> repaint()); // 인간 플레이어 타이머 시작
        rightTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 게임 보드 그리기
        drawBoard(g2d);
        
        // AI와 인간의 점수 표시
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        g.drawString("AI Score: " + ai.CURRENT_LINE, 120, 20);
        g.drawString("Human Score: " + rightBoardManager.CURRENT_LINE, 870, 20);
    }

    // 게임 보드를 그리는 메소드 (AI 보드, 인간 보드, 격자선)
    public void drawBoard(Graphics g) {
        // AI 보드 그리기
        for (int i = 0; i < ai.board.length; i++) {
            for (int j = 0; j < ai.board[i].length; j++) {
                if (ai.board[i][j] != 0) { // 칸이 비어있지 않으면
                    g.setColor(board.color[ai.board[i][j] - 1]);
                    g.fill3DRect(j * 30 + 30, i * 30 + 30, 30, 30, true);
                }
            }
        }

        // 인간 플레이어 보드 그리기
        for (int i = 0; i < rightBoardManager.rightBoard.length; i++) {
            for (int j = 0; j < rightBoardManager.rightBoard[i].length; j++) {
                if (rightBoardManager.rightBoard[i][j] != 0) { // 칸이 비어있지 않으면
                    g.setColor(board.color[rightBoardManager.rightBoard[i][j] - 1]);
                    g.fill3DRect(j * 30 + 800, i * 30 + 30, 30, 30, true);
                }
            }
        }

        // 게임 보드의 격자선 그리기 (AI 보드와 인간 보드 각각에 대해서)
        g.setColor(Color.BLACK);
        for (int i = 1; i <= 10; i++) {
            g.drawLine(i * 30 + 30, 30, i * 30 + 30, 630);
        }
        for (int i = 1; i <= 20; i++) {
            g.drawLine(30, i * 30 + 30, 330, i * 30 + 30);
        }

        for (int i = 1; i <= 10; i++) {
            g.drawLine(i * 30 + 800, 30, i * 30 + 800, 630);
        }
        for (int i = 1; i <= 20; i++) {
            g.drawLine(800, i * 30 + 30, 1100, i * 30 + 30);
        }

        // 게임 보드의 테두리 그리기
        g.drawLine(30, 30, 30, 630); // 왼쪽 테두리
        g.drawLine(30, 30, 330, 30); // 상단 테두리
        g.drawLine(800, 30, 800, 630); // 오른쪽 테두리
        g.drawLine(800, 30, 1100, 30); // 상단 테두리
    }
}

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
    private tetris_board board; // tetris_board ��ü
    private AI_Tetris ai; // AI ��ü
    private Human_Tetris rightBoardManager; // ������ ���� ������ ��ü

    // Ÿ�̸� ��ü
    private Timer leftTimer;
    private Timer rightTimer;

    public TetrisPanel(tetris_board board) {
        this.board = board;
        this.ai = board.AI;
        this.rightBoardManager = board.rightBoardManager;

        this.setFocusable(true); // Ű �̺�Ʈ�� ���� �� �ֵ��� ����
        this.requestFocusInWindow(); // Ű �̺�Ʈ�� ���� �� �ֵ��� ��Ŀ�� ��û

        this.setBackground(Color.WHITE); // ����� ������� ����
        
        // Ű �̺�Ʈ ������ �߰�
        this.addKeyListener(board.new KeyListener());
  
        leftTimer = new Timer((int)ai.TIME, e -> repaint()); // AI Ÿ�̸� ����
        leftTimer.start();

        rightTimer = new Timer((int)rightBoardManager.time, e -> repaint()); // �ΰ� �÷��̾� Ÿ�̸� ����
        rightTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // ���� ���� �׸���
        drawBoard(g2d);
        
        // AI�� �ΰ��� ���� ǥ��
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        g.drawString("AI Score: " + ai.CURRENT_LINE, 120, 20);
        g.drawString("Human Score: " + rightBoardManager.CURRENT_LINE, 870, 20);
    }

    // ���� ���带 �׸��� �޼ҵ� (AI ����, �ΰ� ����, ���ڼ�)
    public void drawBoard(Graphics g) {
        // AI ���� �׸���
        for (int i = 0; i < ai.board.length; i++) {
            for (int j = 0; j < ai.board[i].length; j++) {
                if (ai.board[i][j] != 0) { // ĭ�� ������� ������
                    g.setColor(board.color[ai.board[i][j] - 1]);
                    g.fill3DRect(j * 30 + 30, i * 30 + 30, 30, 30, true);
                }
            }
        }

        // �ΰ� �÷��̾� ���� �׸���
        for (int i = 0; i < rightBoardManager.rightBoard.length; i++) {
            for (int j = 0; j < rightBoardManager.rightBoard[i].length; j++) {
                if (rightBoardManager.rightBoard[i][j] != 0) { // ĭ�� ������� ������
                    g.setColor(board.color[rightBoardManager.rightBoard[i][j] - 1]);
                    g.fill3DRect(j * 30 + 800, i * 30 + 30, 30, 30, true);
                }
            }
        }

        // ���� ������ ���ڼ� �׸��� (AI ����� �ΰ� ���� ������ ���ؼ�)
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

        // ���� ������ �׵θ� �׸���
        g.drawLine(30, 30, 30, 630); // ���� �׵θ�
        g.drawLine(30, 30, 330, 30); // ��� �׵θ�
        g.drawLine(800, 30, 800, 630); // ������ �׵θ�
        g.drawLine(800, 30, 1100, 30); // ��� �׵θ�
    }
}

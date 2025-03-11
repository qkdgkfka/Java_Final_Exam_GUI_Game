package Panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Tetris.AI_Tetris;
import Tetris.tetris_board;

public class DifficultyPanel extends JPanel {
    private tetris_board parent;
    private AI_Tetris ai;
    
    public DifficultyPanel(tetris_board parent) {
        this.parent = parent;
        this.ai = new AI_Tetris(); // AI ��ü �ʱ�ȭ
        setLayout(new GridBagLayout());

        // ���̵� ��ư
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");
        JButton startButton = new JButton("Start");

        // �׼� ������ ����
        easyButton.addActionListener(e -> parent.setDifficulty(200, 400));
        mediumButton.addActionListener(e -> parent.setDifficulty(50, 200));
        hardButton.addActionListener(e -> parent.setDifficulty(1, 100));
     // Start ��ư Ŭ�� �� Tetris ȭ������ ��ȯ�ϰ� TetrisPanel�� ��Ŀ�� ���߱�
        startButton.addActionListener(e -> {
            parent.showPanel("Tetris");  // Tetris ȭ������ ��ȯ

            // TetrisPanel�� ��Ŀ���� ��û
            parent.getContentPane().getComponent(3).requestFocusInWindow();  // "Tetris" �гο� ��Ŀ�� ��û (mainPanel�� 3��° ������Ʈ)
            
            parent.startGame();
        });


        // ��ư ��ġ
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridy = 0;
        add(easyButton, gbc);

        gbc.gridy = 1;
        add(mediumButton, gbc);

        gbc.gridy = 2;
        add(hardButton, gbc);

        gbc.gridy = 3;
        add(startButton, gbc);
    }
}


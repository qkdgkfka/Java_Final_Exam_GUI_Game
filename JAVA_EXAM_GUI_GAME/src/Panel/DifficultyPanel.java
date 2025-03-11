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
        this.ai = new AI_Tetris(); // AI 객체 초기화
        setLayout(new GridBagLayout());

        // 난이도 버튼
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");
        JButton startButton = new JButton("Start");

        // 액션 리스너 설정
        easyButton.addActionListener(e -> parent.setDifficulty(200, 400));
        mediumButton.addActionListener(e -> parent.setDifficulty(50, 200));
        hardButton.addActionListener(e -> parent.setDifficulty(1, 100));
     // Start 버튼 클릭 시 Tetris 화면으로 전환하고 TetrisPanel에 포커스 맞추기
        startButton.addActionListener(e -> {
            parent.showPanel("Tetris");  // Tetris 화면으로 전환

            // TetrisPanel에 포커스를 요청
            parent.getContentPane().getComponent(3).requestFocusInWindow();  // "Tetris" 패널에 포커스 요청 (mainPanel의 3번째 컴포넌트)
            
            parent.startGame();
        });


        // 버튼 배치
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


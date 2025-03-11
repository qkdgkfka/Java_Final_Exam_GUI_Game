package Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Tetris.tetris_board;

public class MainPanel extends JPanel {
    private tetris_board parentFrame; // 부모 tetris_board 참조 변수

    public MainPanel(tetris_board parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("Human VS AI");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(0,0,0));
        
        
        JLabel ME = new JLabel("2023216046 방하람");
        ME.setFont(new Font("SansSerif", Font.BOLD, 20));
        ME.setForeground(new Color(0,0,0));
        
        // 버튼 스타일
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Verdana", Font.BOLD, 30));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(0, 128, 255));
        startButton.setFocusPainted(false);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Verdana", Font.BOLD, 30));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(128, 0, 0));
        exitButton.setFocusPainted(false);

        // 버튼 이벤트 추가
        startButton.addActionListener(e -> parentFrame.showPanel("GameSelection")); // "GameSelection" 화면으로 전환
        
        
        exitButton.addActionListener(e -> {
            // 확인 다이얼로그 표시
        	int result = JOptionPane.showConfirmDialog(
        		    this,
        		    "정말 종료하시겠습니까?",
        		    "종료 확인",
        		    JOptionPane.YES_NO_OPTION,
        		    JOptionPane.PLAIN_MESSAGE // 아이콘 제거
        		);


            // '예' 선택 시 프로그램 종료
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        
        
        
        // 버튼 배치
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 50, 10, 10);
        
        gbc.gridy = 0;
        add(titleLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(startButton, gbc);

        gbc.gridy = 2;
        add(exitButton, gbc);
        
        gbc.gridy = 3;
        add(ME, gbc);
        
        
    }
}

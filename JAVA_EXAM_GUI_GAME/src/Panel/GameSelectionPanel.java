package Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Tag_Game.ChasingGame;
import Tetris.tetris_board;

public class GameSelectionPanel extends JPanel {
    private tetris_board parent;
    private ChasingGame chasingGame; // ChasingGame 객체 선언
    
    public GameSelectionPanel(tetris_board parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 240, 255)); // 배경색 설정

        // 제목 라벨 설정
        JLabel titleLabel = new JLabel("Select Game");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(50, 75, 150));

        // 버튼 생성
        JButton tetrisButton = createStyledButton("Tetris");
        JButton tagButton = createStyledButton("Tag Game");

        // 리턴 버튼 생성
        JButton returnButton = createStyledButton("Return Menu");
        returnButton.setBackground(new Color(250, 100, 100)); // 리턴 버튼 배경색
        returnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                returnButton.setBackground(new Color(230, 80, 80)); // 마우스 오버 시 색상 변경
            }

            @Override
            public void mouseExited(MouseEvent e) {
                returnButton.setBackground(new Color(250, 100, 100)); // 마우스 아웃 시 색상 복원
            }
        });

        // 게임 버튼에 액션 리스너 추가
        tetrisButton.addActionListener(e -> parent.showPanel("Difficulty"));
   
        returnButton.addActionListener(e -> parent.showPanel("Menu"));

        // ChasingGame 객체 생성
        chasingGame = new ChasingGame();
        // Tag Game 버튼 클릭 시 ChasingGame 실행
        tagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 게임 선택 창 숨기기
                setVisible(false);
                parent.dispose();  // JFrame을 닫음 (혹은 setVisible(false)도 가능)
                
                chasingGame.setVisible(true); // ChasingGame 창 표시
                chasingGame.start();  // 게임 시작
            }
        });

        // GridBagLayout에 컴포넌트 배치
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(tetrisButton, gbc);

        gbc.gridy = 2;
        add(tagButton, gbc);

        gbc.gridy = 3;
        add(returnButton, gbc);
    }

    // 스타일을 적용한 버튼 생성 메소드
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(100, 150, 250));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(200, 50)); // 버튼 크기 설정

        // 버튼에 마우스 리스너 추가 (마우스 오버 시 색상 변경)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 130, 230)); // 마우스 오버 시 색상 변경
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(100, 150, 250)); // 마우스 아웃 시 색상 복원
            }
        });

        return button;
    }
}

package Tetris;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Panel.DifficultyPanel;
import Panel.GameSelectionPanel;
import Panel.MainPanel;
import Panel.TetrisPanel;


public class tetris_board extends JFrame {

    // 테트리스 블록 색상 배열
    public Color[] color = { Color.green, Color.blue, Color.red, Color.cyan, Color.yellow, Color.MAGENTA, Color.pink };

    private double previousScore = -1; // 이전 점수 (초기값: -1)

    private double previousX = 400; // X 좌표 초기값
    private double previousY = 540; // Y 좌표 초기값 (게임 시작 시 블록이 시작하는 위치)

    private tetris_block blockGenerator = new tetris_block(); // 테트리스 블록 생성기
    
    public boolean isGameStarted = false; // 게임 시작 여부
    public boolean isGameEnd = false; // 게임 종료 여부
    
    private List<Point> points = new ArrayList<>(); // 게임 중 플레이된 블록의 좌표를 저장할 리스트

    public AI_Tetris AI; // AI 객체
    public Human_Tetris rightBoardManager; // 플레이어의 게임 보드 관리 객체
    
    private CardLayout cardLayout = new CardLayout(); // 카드 레이아웃 (패널 전환을 위한 레이아웃)
    private JPanel mainPanel = new JPanel(cardLayout); // 게임 전체 화면을 담을 패널
    private MainPanel menuPanel = new MainPanel(this); // 메인 메뉴 패널
    public static Queue<List<Point>> sharedBlockQueue4 = new LinkedBlockingQueue<>();
    // 생성자
    public tetris_board() {
   
    }

    // AI와 플레이어의 속도를 설정하는 메서드
    public void setDifficulty(int aiSpeed, int playerSpeed) {
        AI.setTIME(aiSpeed); // AI 속도 설정
        rightBoardManager.setTime(playerSpeed); // 플레이어 속도 설정
    }

    // AI가 있는 테트리스 보드의 생성자
    public tetris_board(AI_Tetris AI) {
        this.AI = AI;
        rightBoardManager = new Human_Tetris(this, AI); // 플레이어와 AI를 연결하는 객체 생성
        
        setTitle("Tetris"); // 윈도우 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료 시 애플리케이션 종료
        setSize(1200, 700); // 윈도우 크기 설정
        setResizable(false); // 창 크기 조정 불가
        
        // 패널을 카드 레이아웃에 추가
        mainPanel.add(new MainPanel(this), "Menu"); // 메인 메뉴
        mainPanel.add(new GameSelectionPanel(this), "GameSelection"); // 게임 선택 패널
        mainPanel.add(new DifficultyPanel(this), "Difficulty"); // 난이도 설정 패널
        mainPanel.add(new TetrisPanel(this), "Tetris"); // 테트리스 게임 화면
       
        
        cardLayout.show(mainPanel, "Tetris"); // 테트리스 화면을 기본으로 표시

        setContentPane(mainPanel); // 메인 패널을 콘텐츠 패널로 설정
        setVisible(true); // 화면에 표시
        
        setLocation(500, 200); // 화면 위치 설정
        
        // 기본적으로 메뉴 화면을 표시
        cardLayout.show(mainPanel, "Menu");
    }

    // 배경 음악을 재생하는 메서드
    private void playBackgroundMusic() {
        try {
            // 리소스 파일에서 음악 파일을 불러옴
            InputStream audioStream = getClass().getClassLoader().getResourceAsStream("Resources/Music/Tetris.wav");

            if (audioStream == null) {
                System.err.println("음악 파일을 찾을 수 없습니다.");
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // 음악을 반복 재생
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // 오류가 발생하면 출력
        }
    }

    
   
    
    // 게임을 시작하는 메서드
    public void startGame() {
    	blockGenerator.initializeBlockQueue();

    	new Thread(() -> AI.do_tetris()).start();
      
    	    Thread rightBoardThread = new Thread(() -> {
    	 
    	        rightBoardManager.generateRandomBlockOnRightBoard();
    	    });
    	    rightBoardThread.start();
    
    }
    
    
    

    // 카드 레이아웃에서 특정 패널을 하는 메서드
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName); // 지정된 패널을 표시
    }

    

    
    // 키 입력을 처리하는 클래스 (게임 내 키 이벤트 처리)
    public class KeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent arg0) {
            super.keyPressed(arg0);

            // 왼쪽 화살표 키로 블록을 왼쪽으로 이동
            if (arg0.getKeyCode() == KeyEvent.VK_A) {
                rightBoardManager.moveBlockHorizontally(-1);
            }

            // 오른쪽 화살표 키로 블록을 오른쪽으로 이동
            else if (arg0.getKeyCode() == KeyEvent.VK_D) {
                rightBoardManager.moveBlockHorizontally(1);
            }

            // 아래 화살표 키로 블록을 아래로 이동
            else if (arg0.getKeyCode() == KeyEvent.VK_S) {
                if (!rightBoardManager.moveBlockDown(rightBoardManager.currentBlockShape)) {
                    rightBoardManager.checkAndClearLines(); // 라인 삭제 체크
                    rightBoardManager.generateRandomBlockOnRightBoard(); // 새로운 블록 생성
                }
            }

            // 위 화살표 키로 블록을 회전
            else if (arg0.getKeyCode() == KeyEvent.VK_W) {
                rightBoardManager.rotate(rightBoardManager.currentBlockShape);
            }

            // 스페이스 바로 블록을 아래로 강제 이동
            else if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
                rightBoardManager.moveEnd(rightBoardManager.currentBlockShape);
            }
        }
    }
}


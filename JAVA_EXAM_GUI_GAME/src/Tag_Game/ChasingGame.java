package Tag_Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ChasingGame extends JFrame {
    private Player player;
    private Chaser chaser;
    private List<Obstacle> obstacles;
    private List<SlowZone> slowZones; // �뒳濡쒖슦 議� 由ъ뒪�듃 異붽�
    private Timer gameTimer;
    private Point targetPoint;
    long startTime = System.currentTimeMillis();  // 게임 시작 시간
    private long lastFireTime;  // Track the last fire time to control firing rate
    private List<Portal> portals; // �룷�깉 由ъ뒪�듃 異붽�
    private List<SpeedItem> speedItems; // �븘�씠�뀥 由ъ뒪�듃 異붽�
    private static final int ITEM_EFFECT_DURATION = 5000; // �슚怨� 吏��냽 �떆媛� (5珥�)
    private boolean isGameRunning = false; // 게임이 실행 중인지 여부를 추적하는 변수
    private List<Projectile> projectiles;  // List of projectiles fired by the chaser
    
    
    
    public ChasingGame() {
        player = new Player(100, 100);
        chaser = new Chaser(300, 300);
        obstacles = new ArrayList<>();
        slowZones = new ArrayList<>(); // �뒳濡쒖슦 議� 珥덇린�솕
        createObstacles();
        createSlowZones(); // �뒳濡쒖슦 議� �깮�꽦
        createPortals(); // �룷�깉 �깮�꽦
        speedItems = new ArrayList<>(); // 由ъ뒪�듃 珥덇린�솕
        spawnSpeedItem(); // �븘�씠�뀥 �뒪�룿
        projectiles = new ArrayList<>();
        startTime = System.currentTimeMillis();
        lastFireTime = startTime;
        

        setTitle("Tag Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        add(new GamePanel());

        startTime = System.currentTimeMillis();  // 寃뚯엫 �떆�옉 �떆媛� 湲곕줉
        
        gameTimer = new Timer(1, e -> {
            // 게임이 실행 중이지 않으면 아무것도 하지 않음
            if (!isGameRunning) return; 

            // 플레이어가 목표 지점을 향해 이동하도록 처리
            if (targetPoint != null) {
                player.moveTowards(targetPoint, obstacles, slowZones, portals, projectiles); // 목표 지점으로 이동
            }

            // 추격자가 플레이어를 따라가도록 처리
            chaser.moveTowards(player, obstacles, slowZones); // 추격자가 플레이어를 추적

            // 2초마다 새로운 투사체 발사
            if (System.currentTimeMillis() - lastFireTime >= 2000) {  // 2초 간격
                fireProjectile();  // 새로운 투사체 발사
                lastFireTime = System.currentTimeMillis();  // 마지막 발사 시간을 갱신
            }

            // 모든 투사체를 이동시킴
            for (Projectile projectile : projectiles) {
                projectile.move();  // 각 투사체를 이동
            }

         // 투사체가 플레이어와 충돌했는지 확인
            for (int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile projectile = projectiles.get(i);
                
                // 플레이어와 충돌 여부 체크
                if (player.checkCollisionWithProjectile(projectile)) {  
                    player.decreaseSpeed();  // 충돌 시 플레이어의 이동 속도 감소
                    System.out.println("히트");  // 콘솔에 '히트' 출력 (디버깅용)
                    projectiles.remove(i);  // 충돌한 투사체는 리스트에서 제거
                }
                // 투사체가 맵을 벗어난 경우
                else if (projectile.x < 0 || projectile.x > 600 || projectile.y < 0 || projectile.y > 600) {
                    projectiles.remove(i);  // 맵을 벗어난 투사체는 리스트에서 제거
                }
            }

            // 스피드 아이템이 플레이어에게 먹혔는지 확인
            for (int i = speedItems.size() - 1; i >= 0; i--) {
                SpeedItem item = speedItems.get(i);
                if (item.isEatenBy(player.x, player.y)) {  // 플레이어가 아이템을 먹었는지 확인
                    player.activateSpeedBoost(ITEM_EFFECT_DURATION); // 아이템 효과 (속도 부스트) 활성화
                    speedItems.remove(i); // 아이템 리스트에서 해당 아이템 제거
                    spawnSpeedItem(); // 새로운 스피드 아이템 생성
                }
            }
            
            
            
            
            //寃뚯엫 吏�硫�
            if(isGameOver() == true) {
            	gameOver();
            }
            
            //寃뚯엫 �씠湲곕㈃
            // 20초 경과 후 게임이 종료되지 않았다면 승리 처리
            if (!isGameOver() && System.currentTimeMillis() - startTime >= 20000) {  // 20초 경과
                playerWins();  // 플레이어 승리 처리
            }

            
            repaint();
        });
        gameTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    targetPoint = e.getPoint();
                    player.setTargetPoint(targetPoint);
                }
            }
        });

        setFocusable(true);
    }

    private void playerWins() {
    	  JOptionPane.showMessageDialog(this, "You Win! You survived for 20 seconds!");
    	  System.exit(0);  // "확인" 클릭 시 프로그램 종료
    }
    
    
    private void fireProjectile() {

        Projectile projectile = new Projectile(chaser.x, chaser.y, player.x, player.y);
        projectiles.add(projectile);
    }    
    
    
    private void createObstacles() {
        obstacles.add(new Obstacle(200, 200, 100, 20));
        obstacles.add(new Obstacle(400, 100, 20, 100));
        obstacles.add(new Obstacle(300, 400, 100, 20));
    }

    private void createSlowZones() {
        slowZones.add(new SlowZone(100, 300, 100, 100));
        slowZones.add(new SlowZone(350, 200, 150, 150));
    }
    
    private void createPortals() {
    	portals = new ArrayList<>();  // 由ъ뒪�듃 珥덇린�솕 異붽�
        portals.add(new Portal(50, 50, 500, 500, 40, 40)); // �룷�깉 1
        portals.add(new Portal(500, 500, 50, 50, 40, 40)); // �룷�깉 2
    }

    
    private void spawnSpeedItem() {
        int itemSize = 20; // �븘�씠�뀥 �겕湲�
        int x = (int) (Math.random() * 550); // �븘�씠�뀥 �겕湲곕�� 怨좊젮�븳 X 醫뚰몴 踰붿쐞
        int y = (int) (Math.random() * 550); // �븘�씠�뀥 �겕湲곕�� 怨좊젮�븳 Y 醫뚰몴 踰붿쐞
        
        speedItems.add(new SpeedItem(x, y, itemSize)); // �깮�꽦�맂 �븘�씠�뀥 異붽�
    }


    private void playBackgroundMusic() {
        try {
      
            InputStream audioStream = getClass().getClassLoader().getResourceAsStream("Resources/Music/ChasingGame.wav");

            if (audioStream == null) {
                System.err.println("음악 파일을 찾을 수 없습니다.");
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // 반복 재생
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private boolean isGameOver() {
        // �닠�옒�� �뵆�젅�씠�뼱�쓽 �쐞移섍� 寃뱀튂�뒗吏� �솗�씤
        if(player.x < chaser.x + 5 && player.x + 5 > chaser.x&& player.y < chaser.y + 5 && player.y + 5 > chaser.y) {
        	return true;
        }
        
        else {
        	return false;
        }
    }

    
    private boolean isGameWin() {
    	 long elapsedTime = System.currentTimeMillis() - startTime;
     	
    	if(elapsedTime > 5000000) {  // 10珥�(10000ms)媛� 寃쎄낵�뻽�쑝硫�
             return true;
         }
    	
    	else {
    		return false;
    	}
    }
    
    
    public void start() {
    	isGameRunning = true; // 게임이 실행되도록 설정
    	
    	 playBackgroundMusic();
    	if (!this.isVisible()) {
            setVisible(true); // 게임 창을 표시
        }

    }

    private void gameWin() {
    	javax.swing.JOptionPane.showMessageDialog(null, "Game Win!");
        System.exit(0);  // �봽濡쒓렇�옩 醫낅즺
    }
    
    // 寃뚯엫 醫낅즺 泥섎━
    private void gameOver() {
    	javax.swing.JOptionPane.showMessageDialog(null, "Game Lose!");
        System.exit(0);  // �봽濡쒓렇�옩 醫낅즺
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            player.draw(g);
            chaser.draw(g);
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(g);
            }
            for (SlowZone slowZone : slowZones) {
                slowZone.draw(g); // �뒳濡쒖슦 議� 洹몃━湲�
            }
            for (Portal portal : portals) {
                portal.draw(g); // �룷�깉 洹몃━湲�
            }
            for (SpeedItem item : speedItems) {
                item.draw(g); // �븘�씠�뀥 洹몃━湲� 異붽�
            }
            
            for (Projectile projectile : projectiles) {
                projectile.draw(g);  // Draw each projectile
            }
        }
    }

   
}	

class Player {
    int x, y; // 플레이어의 현재 좌표
    int speed = 6; // 기본 이동 속도
    int decreasedSpeed = 4; // 속도 감소 후 속도
    Point targetPoint; // 플레이어가 이동하려는 목표 지점
    long lastPortalTime = 0; // 마지막으로 포탈을 사용한 시간
    long speedBoostEndTime = 0; // 스피드 부스트 효과가 끝나는 시간
    long speedDecreaseEndTime = 0; // 속도 감소 효과가 끝나는 시간
    private static final int CHARACTER_WIDTH = 20;
    private static final int CHARACTER_HEIGHT = 20;
    private static final int SPEED_DECREASE_DURATION = 1000; // 속도 감소 지속 시간 (1초)
   
    
    // 생성자: 플레이어 객체를 초기화합니다.
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
    public void decreaseSpeed() {
        if (System.currentTimeMillis() < speedDecreaseEndTime) {
        	speedDecreaseEndTime = System.currentTimeMillis() + SPEED_DECREASE_DURATION; // 속도 감소 효과 종료 시간을 설정
            speed = speed - 3; // 속도 감소
        }
    }
    
 
    // 투사체와 충돌 체크
    public boolean checkCollisionWithProjectile(Projectile projectile) {
        return (x < projectile.x + 5 && x + CHARACTER_WIDTH > projectile.x && 
                y < projectile.y + 5 && y + CHARACTER_HEIGHT > projectile.y);
    }

    // 목표 지점을 설정합니다.
    public void setTargetPoint(Point target) {
        // 캐릭터 중심이 마우스 클릭 지점에 위치하도록 보정
        this.targetPoint = new Point(target.x - CHARACTER_WIDTH / 2, target.y - CHARACTER_HEIGHT / 2);
    }


    // 스피드 부스트를 활성화하고 지속 시간을 설정합니다.
    public void activateSpeedBoost(int duration) {
        speedBoostEndTime = System.currentTimeMillis() + duration; // 현재 시간에 지속 시간을 더해 종료 시간 설정
    }

    // 현재 이동 속도를 반환합니다 (스피드 부스트가 적용 중이라면 속도를 2배로 반환).
    private int getCurrentSpeed() {
        if (System.currentTimeMillis() < speedBoostEndTime) {
            return speed * 2; // 스피드 부스트가 활성화된 상태라면 기본 속도의 2배 반환
        }
        
        if (System.currentTimeMillis() < speedDecreaseEndTime) {
        	return speed - 3; // 감소된 속도 반환
        }
        
        speed = 7; // 속도 감소 효과가 끝나면 원래 속도로 복귀
        return speed; // 그렇지 않으면 기본 속도 반환
    }

  
    // 목표 지점을 향해 이동합니다.
    public void moveTowards(Point target, List<Obstacle> obstacles, List<SlowZone> slowZones, List<Portal> portals, List<Projectile> projectiles) {
        if (target == null) return; // 목표 지점이 설정되지 않았다면 아무것도 하지 않음
        
       
        // 목표 지점과의 거리 및 방향을 계산합니다.
        int dx = target.x - x;
        int dy = target.y - y;
        double distance = Math.sqrt(dx * dx + dy * dy); // 피타고라스의 정리를 사용하여 거리 계산

        // 현재 속도를 가져옵니다.
        double currentSpeed = getCurrentSpeed();
       

        for (SlowZone slowZone : slowZones) {
            if (slowZone.isInside(x, y)) { 
                currentSpeed = speed / 1.6; // 슬로우 존 내에서만 속도 감소
          
                break;
            }
        }
        // 이동 거리 계산
        if (distance > currentSpeed) { // 목표 지점에 도달하지 않았을 때
            double moveX = currentSpeed * dx / distance; // 이동 비율에 따라 x 방향 이동 계산
            double moveY = currentSpeed * dy / distance; // 이동 비율에 따라 y 방향 이동 계산

            int newX = (int)(x + moveX); // 새로운 x 좌표
            int newY = (int)(y + moveY); // 새로운 y 좌표

            // 장애물과 충돌 여부 확인
            if (!isCollidingWithObstacle(newX, newY, obstacles)) {
                x = newX; // 충돌하지 않으면 x 좌표 갱신
                y = newY; // 충돌하지 않으면 y 좌표 갱신
            }
        } 
        else {
            // 목표 지점에 도달하면 좌표를 목표 지점으로 고정
            x = target.x;
            y = target.y;
        }

        // 포탈과의 상호작용 처리
        for (Portal portal : portals) {
            if (portal.isInside(x, y) && canUsePortal()) { // 현재 위치가 포탈 내부이고 포탈을 사용할 수 있다면
                Point exit = portal.getExitPoint(); // 포탈의 출구 좌표를 가져옴
                x = exit.x; // 출구 x 좌표로 이동
                y = exit.y; // 출구 y 좌표로 이동
                lastPortalTime = System.currentTimeMillis(); // 포탈 사용 시간을 기록
                break; // 포탈을 사용했으므로 더 이상 체크하지 않음
            }
        }
    }

    // 새 위치가 장애물과 충돌하는지 확인
    private boolean isCollidingWithObstacle(int newX, int newY, List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.collidesWith(newX, newY)) { // 장애물과 충돌하는지 확인
                return true; // 충돌하면 true 반환
            }
        }
        return false; // 충돌하지 않으면 false 반환
    }

    // 포탈을 사용할 수 있는지 확인 (3초의 쿨타임을 고려)
    private boolean canUsePortal() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastPortalTime > 3000; // 마지막 포탈 사용 시간으로부터 3초가 지났다면 true 반환
    }

    // 플레이어를 화면에 그립니다.
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT); // 캐릭터 그리기
    }
}




class Chaser { 
    int x, y; // Chaser의 현재 위치 (x, y 좌표)
    int speed = 2; // Chaser의 이동 속도

    // 생성자: Chaser 객체의 초기 위치를 설정
    public Chaser(int x, int y) {
        this.x = x; // Chaser의 초기 x 좌표
        this.y = y; // Chaser의 초기 y 좌표
    }

    /**
     * 플레이어를 향해 이동하는 함수
     * A* 알고리즘을 사용하여 최적 경로를 계산하고, SlowZone에서 속도를 조정함.
     *
     * @param player    플레이어 객체 (목표 지점)
     * @param obstacles 장애물 리스트
     * @param slowZones 속도를 감소시키는 구역 리스트
     */
    public void moveTowards(Player player, List<Obstacle> obstacles, List<SlowZone> slowZones) {
        Point current = new Point(x, y); // 현재 위치를 Point 객체로 저장
        Point target = new Point(player.x, player.y); // 플레이어의 위치를 Point 객체로 저장

        // A* 알고리즘으로 이동 경로 계산
        List<Point> path = findPath(current, target, obstacles);

        // 경로가 없거나 시작점 외 경로가 없으면 이동하지 않음
        if (path == null || path.size() < 2) {
            return;
        }

        // SlowZone 내에서 속도 조정 (기본 속도를 감소)
        double remainingSpeed = speed;
        for (SlowZone slowZone : slowZones) {
            if (slowZone.isInside(x, y)) { 
                remainingSpeed = speed / 1.6; // SlowZone 속도 감소
                break;
            }
        }

        // 속도 한도 내에서 이동
        double distance = 0; // 이동한 거리
        Point nextStep = null; // 다음 이동할 지점
        for (int i = 1; i < path.size(); i++) {
            Point step = path.get(i); // 현재 경로의 다음 지점
            double stepDistance = current.distance(step); // 현재 지점과의 거리

            // 속도 내에 다음 지점이 포함되지 않으면 루프 종료
            if (distance + stepDistance > remainingSpeed) {
                break;
            }

            // 누적 거리와 다음 지점 갱신
            distance += stepDistance;
            nextStep = step;
        }

        // 다음 지점으로 이동
        if (nextStep != null) {
            x = nextStep.x;
            y = nextStep.y;
        }
    }

    /**
     * A* 알고리즘으로 경로를 찾는 함수
     * 장애물 리스트를 고려하여 최적 경로를 탐색.
     *
     * @param current   현재 위치
     * @param target    목표 위치
     * @param obstacles 장애물 리스트
     * @return 목표 지점까지의 경로 (List<Point>) 또는 경로가 없으면 null
     */
    private List<Point> findPath(Point current, Point target, List<Obstacle> obstacles) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f)); // A*의 우선순위 큐
        Set<Point> closedSet = new HashSet<>(); // 탐색이 완료된 노드
        Map<Point, Node> allNodes = new HashMap<>(); // 방문한 모든 노드

        // 시작 노드를 생성하고 openSet에 추가
        Node startNode = new Node(current, null, 0, heuristic(current, target));
        openSet.add(startNode);
        allNodes.put(current, startNode);

        // A* 알고리즘 탐색
        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll(); // f 값이 가장 작은 노드를 선택

            // 목표 지점에 도달하면 경로 반환
            if (currentNode.point.equals(target)) {
                return reconstructPath(currentNode);
            }

            // 현재 노드를 closedSet에 추가
            closedSet.add(currentNode.point);

            // 현재 노드의 이웃 노드들을 탐색
            for (Point neighbor : getNeighbors(currentNode.point)) {
                // 이웃 노드가 이미 처리되었거나 장애물인 경우 건너뜀
                if (closedSet.contains(neighbor) || isBlocked(neighbor, obstacles)) {
                    continue;
                }

                // 시작점에서 이웃 노드까지의 거리 계산 (g 값)
                int tentativeG = currentNode.g + 1;

                // 이웃 노드 객체를 가져오거나 새로 생성
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor, null, Integer.MAX_VALUE, 0));

             // 더 좋은 경로를 발견하면 이웃 노드 업데이트
                int tentativeF = tentativeG + heuristic(neighbor, target); // 새로 계산된 f 값

                if (tentativeF < neighborNode.f) {  // f 값으로 비교해야 함
                    neighborNode.g = tentativeG;  // g 값 업데이트
                    neighborNode.f = tentativeF;  // f 값 업데이트
                    neighborNode.parent = currentNode; // 부모 노드 설정

                    // 이웃 노드가 openSet에 없으면 추가
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                    allNodes.put(neighbor, neighborNode);
                }
            }
        }

        // 경로를 찾지 못한 경우 null 반환
        return null;
    }

    /**
     * 탐색 결과를 기반으로 경로를 재구성하는 함수
     * 
     * @param node 목표 지점 노드
     * @return 시작점에서 목표 지점까지의 경로 (List<Point>)
     */
    private List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.point); // 경로를 역순으로 추가
            node = node.parent; // 부모 노드로 이동
        }
        return path;
    }

    /**
     * 두 지점 사이의 휴리스틱(추정 비용)을 계산하는 함수
     * 
     * @param a 시작 지점
     * @param b 목표 지점
     * @return 두 지점 사이의 유클리드 거리
     */
    private int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // 멘헤튼 거리
    }
    /**
     * 현재 지점에서 이동 가능한 이웃 지점들을 반환
     * 8방향(상하좌우 및 대각선)을 기준으로 계산.
     * 
     * @param p 현재 지점
     * @return 이동 가능한 이웃 지점 리스트
     */
    private List<Point> getNeighbors(Point p) {
        List<Point> neighbors = new ArrayList<>();
        int[][] directions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}, // 상, 하, 좌, 우
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1} // 대각선
        };

        // 각 방향으로 이동한 좌표를 계산
        for (int[] dir : directions) {
            int newX = p.x + dir[0];
            int newY = p.y + dir[1];

            // 좌표가 맵의 경계 내에 있는 경우에만 추가 (600x600 맵 가정)
            if (newX >= 0 && newX < 600 && newY >= 0 && newY < 600) {
                neighbors.add(new Point(newX, newY));
            }
        }

        return neighbors;
    }

    /**
     * 특정 지점이 장애물과 충돌하는지 확인
     * 
     * @param point 확인할 지점
     * @param obstacles 장애물 리스트
     * @return 충돌 여부 (true: 충돌, false: 비충돌)
     */
    private boolean isBlocked(Point point, List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.collidesWith(point.x, point.y)) {
                return true; // 장애물과 충돌하면 true 반환
            }
        }
        return false;
    }

    /**
     * Chaser 객체를 화면에 그리는 함수
     * 
     * @param g 그래픽 객체
     */
    public void draw(Graphics g) {
        g.setColor(Color.RED); // Chaser의 색상 설정
        g.fillRect(x, y, 20, 20); // Chaser를 20x20 크기의 빨간색 사각형으로 그림
    }
}





//장애물 클래스
class Obstacle {
 int x, y, width, height;  // 장애물의 위치(x, y)와 크기(width, height)

 // 생성자: 장애물의 초기 위치와 크기 설정
 public Obstacle(int x, int y, int width, int height) {
     this.x = x;
     this.y = y;
     this.width = width;
     this.height = height;
 }

 // 장애물을 화면에 그리는 메서드
 public void draw(Graphics g) {
     g.setColor(Color.GRAY);  // 장애물 색을 회색으로 설정
     g.fillRect(x, y, width, height);  // 주어진 위치와 크기로 사각형을 그림
 }

 // 주어진 (x, y) 좌표에서 장애물과 충돌하는지 확인하는 메서드
 public boolean collidesWith(int x, int y) {
     return x < this.x + width && x + 20 > this.x && y < this.y + height && y + 20 > this.y;
 }

 // Point 객체를 받아 충돌 여부를 확인하는 메서드
 public boolean collidesWith(Point point) {
     return collidesWith(point.x, point.y);  // (x, y) 좌표로 변환하여 충돌 여부 확인
 }
}

//느려지는 구역 (SlowZone) 클래스
class SlowZone {
 int x, y, width, height;  // 느려지는 구역의 위치(x, y)와 크기(width, height)

 // 생성자: 느려지는 구역의 초기 위치와 크기 설정
 public SlowZone(int x, int y, int width, int height) {
     this.x = x;
     this.y = y;
     this.width = width;
     this.height = height;
 }

 // 느려지는 구역을 화면에 그리는 메서드
 public void draw(Graphics g) {
     g.setColor(new Color(150, 150, 255, 100));  // 반투명한 파란색으로 설정
     g.fillRect(x, y, width, height);  // 주어진 위치와 크기로 사각형을 그림
 }

 // 주어진 (x, y) 좌표가 느려지는 구역 안에 있는지 확인하는 메서드
 public boolean isInside(int x, int y) {
     return x < this.x + width && x + 20 > this.x && y < this.y + height && y + 20 > this.y;
 }
}

//A* 알고리즘에서 사용하는 노드(Node) 클래스
class Node {
 Point point;  // 노드의 위치 (Point 객체)
 Node parent;  // 부모 노드 (경로 탐색을 위한)
 int g, h, f;  // g: 시작 지점부터 현재까지의 비용, h: 목표 지점까지의 추정 비용, f: g + h

 // 생성자: 노드의 위치, 부모 노드, g, h 값을 설정
 public Node(Point point, Node parent, int g, int h) {
     this.point = point;
     this.parent = parent;
     this.g = g;
     this.h = h;
     this.f = g + h;  // f = g + h (A* 알고리즘에서의 f 값)
 }
}

//포탈(Portal) 클래스
class Portal {
 int startX, startY, endX, endY, width, height;  // 포탈의 시작 위치(startX, startY), 끝 위치(endX, endY)와 크기

 // 생성자: 포탈의 시작과 끝 좌표 및 크기 설정
 public Portal(int startX, int startY, int endX, int endY, int width, int height) {
     this.startX = startX;
     this.startY = startY;
     this.endX = endX;
     this.endY = endY;
     this.width = width;
     this.height = height;
 }

 // 포탈을 화면에 그리는 메서드
 public void draw(Graphics g) {
     g.setColor(new Color(0, 255, 0, 100));  // 반투명한 초록색으로 설정
     g.fillRect(startX, startY, width, height);  // 주어진 위치와 크기로 사각형을 그림
 }

 // 주어진 (x, y) 좌표가 포탈 안에 있는지 확인하는 메서드
 public boolean isInside(int x, int y) {
     return x < startX + width && x + 20 > startX && y < startY + height && y + 20 > startY;
 }

 // 포탈의 출구 지점(Point 객체) 반환
 public Point getExitPoint() {
     return new Point(endX, endY);  // 포탈의 끝 지점 반환
 }
}

class Projectile {
    int x, y; // 투사체의 현재 좌표 (x, y)
    double velocityX, velocityY; // 투사체의 x, y 방향 속도
    static final int SPEED = 15;  // 투사체의 속도 (기본값 15)
    boolean isVisible = true; // 투사체의 가시성 상태 (기본적으로 보이도록 설정)
    
    // 생성자: 투사체 객체를 초기화합니다. (시작 좌표와 목표 좌표를 받음)
    public Projectile(int startX, int startY, int targetX, int targetY) {
        this.x = startX; // 시작 위치 x 좌표
        this.y = startY; // 시작 위치 y 좌표
        
        // 목표 지점까지의 방향 계산 (x, y 차이)
        double dx = targetX - startX;
        double dy = targetY - startY;
        
        // 시작 지점과 목표 지점 사이의 거리 계산
        double distance = Math.sqrt(dx * dx + dy * dy);

        // 방향을 정규화하여 속도 벡터를 계산 (단위 벡터로 만듦)
        velocityX = (dx / distance) * SPEED; // x 방향 속도
        velocityY = (dy / distance) * SPEED; // y 방향 속도
    }

    // 투사체 이동 메서드: 투사체를 직선으로 이동시킴
    public void move() {
        x += velocityX; // x 좌표 갱신
        y += velocityY; // y 좌표 갱신
    }

    // 투사체의 가시성 상태를 변경하는 메서드
    public void setVisible(boolean visible) {
        this.isVisible = visible; // 가시성 상태를 설정 (보이거나 보이지 않게)
    }
    
    // 투사체를 화면에 그리는 메서드
    public void draw(Graphics g) {
        g.setColor(Color.BLACK); // 색상을 검정색으로 설정
        g.fillRect(x, y, 5, 5);  // 투사체를 작은 사각형으로 그리기 (5x5 크기)
    }
    
    // 투사체가 보이는지 여부를 반환하는 메서드
    public boolean isVisible() {
        return isVisible; // 가시성 상태 반환 (true: 보임, false: 보이지 않음)
    }
}


class SpeedItem {
    int x, y, size;

    public SpeedItem(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillOval(x, y, size, size);
    }

    public boolean isEatenBy(int px, int py) {
        return px < x + size && px + 20 > x && py < y + size && py + 20 > y;
    }
}


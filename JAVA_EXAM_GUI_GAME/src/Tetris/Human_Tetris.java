package Tetris;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Human_Tetris {
    public int[][] rightBoard = new int[20][10]; // ?��?��?��?��?��?��?��?��?�� ?��?��?��?��?��?��
    private int[][] previousBoard = new int[20][10]; // ?��?��?��?��?��?�� ?��?��?��?��?��?�� ?��?��?��?��?��?�� ?��?��?��?��?��?��?���?
    public List<Point> currentBlockShape; // ?��?��?��?��?��?�� ?��?��?��?�� ?��?��?��?��?��?�� ?��?��?��?���?
    public int currentBlockColor; // ?��?��?��?���? ?��?��?��?��?��?��
    private tetris_block blockGenerator = new tetris_block(); // ?��?��?��?���? ?��?��?��?��?��?��?��?��?��
    public int time = 200; // ?��?���? ?��?��곤옙 (?��?��?��?��?��?��?���? ?��?��?��?��?��?��?��?��?��?��?��?�� ?��?��?��?��, ?��?��몌옙?��?��?�� ?��?��?��?��?��?��)
    private Random rn; // 랜덤 객체 (블록의 종류를 결정하기 위해 사용)
    private tetris_board t_board;
	private AI_Tetris AI;
	
	public static int CURRENT_LINE = 0;
	private boolean isGameEnd = false; // ?��?��?��?��?��?�� ?��?��?�� ?��?��?��?��?��?�� ?��?��뤄옙?��?��?��
    public  Queue<List<Point>> sharedBlockQueue3 = new LinkedBlockingQueue<>();
    
	public Human_Tetris(tetris_board t_board, AI_Tetris AI) {
	    this.t_board = t_board;
	    this.AI = AI;
	}
	

	public Human_Tetris(AI_Tetris AI) {
        this.AI = AI;
  
	}

	private boolean finish(List<Point> block) {
		for (Point p : block) {
			if (rightBoard[p.x][p.y] != 0)
			{
				isGameEnd = true;
				return true;
			}
			
			if(CURRENT_LINE >= 10) {
				isGameEnd = true;
			}
		}
		return false;
	}

	

	
	
 // ?��?��?��?��?��?�� ?��?��?��?��?��?�� ?��?��?��?��?��몌옙 ?��?��?��?��?��?��
 	public void saveCurrentBoardState() {
 	    for (int i = 0; i < 20; i++) {
 	        for (int j = 0; j < 10; j++) {
 	            previousBoard[i][j] = rightBoard[i][j];
 	        }
 	    }
 	}

 	public void moveBlockHorizontally(int direction) {
 	    boolean canMove = true;

 	    // 현재 블록이 이동할 수 있는지 확인
 	    for (Point p : currentBlockShape) {
 	        int newX = p.x;
 	        int newY = p.y + direction; // 블록을 수평으로 이동

 	        // 블록이 보드를 벗어나거나 다른 블록이 있는지 확인
 	        if (newY < 0 || newY >= 10 || newX > 20 || previousBoard[newX][newY] != 0) { // 이동 불가능
 	            canMove = false; // 이동할 수 없으면 false로 설정
 	            break;
 	        }
 	    }

 	    if (canMove) {
 	        // 기존 블록 위치를 비운다
 	        for (Point p : currentBlockShape) {
 	            int x = p.x;
 	            int y = p.y;

 	            if (x < 20 && y >= 0 && y < 10) {
 	                rightBoard[x][y] = 0; // 현재 위치의 블록을 제거
 	            }
 	        }

 	        // 블록을 수평으로 이동
 	        for (Point p : currentBlockShape) {
 	            p.y += direction;
 	        }

 	        // 이동한 블록을 새로운 위치에 배치
 	        for (Point p : currentBlockShape) {
 	            int x = p.x;
 	            int y = p.y;
 	            if (x < 20 && y >= 0 && y < 10) {
 	                rightBoard[x][y] = currentBlockColor; // 새로운 위치에 블록 색상 설정
 	            }
 	        }

 	        t_board.repaint(); // 보드 갱신
 	    }
 	}

 	public boolean moveBlockDown(List<Point> blockShape) {
 	    // 현재 블록을 보드에서 지우기
 	    for (Point p : blockShape) {
 	        if (p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = 0;
 	        }
 	    }

 	    // 블록이 아래로 이동할 수 있는지 확인
 	    boolean canMove = true;
 	    for (Point p : blockShape) {
 	        int newX = p.x + 1; // 아래로 한 칸 내려가기
 	        if (newX >= 20 || (p.y >= 0 && p.y < 10 && rightBoard[newX][p.y] != 0)) {
 	            canMove = false; // 내려갈 수 없으면 false로 설정
 	            break;
 	        }
 	    }

 	    if (canMove) {
 	        // 블록을 한 칸 아래로 이동
 	        for (Point p : blockShape) {
 	            p.x += 1;
 	        }
 	    }

 	    // 이동한 블록을 보드에 다시 배치
 	    for (Point p : blockShape) {
 	        if (p.x >= 0 && p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = currentBlockColor; // 새로운 위치에 블록 색상 설정
 	        }
 	    }

 	    return canMove; // 블록이 이동할 수 있으면 true 반환
 	}

 	public boolean moveEnd(List<Point> block) {
 	    // 블록을 보드에서 지우기
 	    for (Point p : block) {
 	        if (p.x >= 0 && p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = 0;
 	        }
 	    }

 	    // 블록이 내려갈 수 있는 최소 거리를 계산
 	    int minMoveDistance = Integer.MAX_VALUE;

 	    // 각 블록의 이동 가능한 최소 거리 계산
 	    for (Point p : block) {
 	        int nx = p.x;
 	        int ny = p.y;

 	        // 블록이 내려갈 수 있는 최대로 내려간 위치를 찾기
 	        while (nx + 1 < 20 && rightBoard[nx + 1][ny] == 0) {
 	            nx++;  // 블록을 계속 아래로 이동
 	        }

 	        // 내려갈 수 있는 거리 중 가장 작은 거리 계산
 	        minMoveDistance = Math.min(minMoveDistance, nx - p.x);
 	    }

 	    // 최소 거리만큼 블록을 아래로 이동
 	    for (Point p : block) {
 	        p.x += minMoveDistance;
 	    }

 	    // 이동한 블록을 보드에 다시 배치
 	    for (Point p : block) {
 	        if (p.x >= 0 && p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = currentBlockColor; // 새로운 위치에 블록 색상 설정
 	        }
 	    }

 	    // 보드 갱신
 	    t_board.repaint();

 	    return true;  // 블록이 성공적으로 이동
 	}



 	private void endGame() {
 	    System.out.println("Game Over!");
 	    // 게임 종료 시 처리:
 	    // - UI 메시지 출력
 	    // - 플레이어가 게임에서 승리하거나 패배했음을 알림
 	    // - 종료 전 최종 점수(클리어한 라인 수)를 표시

 	    if (CURRENT_LINE >= 10) { 
 	        // 클리어한 라인이 10개 이상인 경우, 승리 메시지 출력
 	        javax.swing.JOptionPane.showMessageDialog(
 	            null, 
 	            "Game Win!\nFinal Lines Cleared: " + CURRENT_LINE + "\nHuman Win"
 	        );
 	    } else { 
 	        // 클리어한 라인이 10개 미만인 경우, 패배 메시지 출력
 	        javax.swing.JOptionPane.showMessageDialog(
 	            null, 
 	            "Game Over!\nFinal Lines Cleared: " + CURRENT_LINE + "\nHuman Lose"
 	        );
 	    }
 	    
 	    System.exit(0); 
 	}


 	 public void refillBlockQueueIfNeeded() {
	        if (sharedBlockQueue3.size() <= 10) {
	        	blockGenerator.refillBlockQueueIfNeeded();
	            System.out.println("Block qnew blocks!");
	           Queue<List<Point>> copiedBlockQueue2 = new LinkedBlockingQueue<>(blockGenerator.getQueue());
	     	   sharedBlockQueue3 = copiedBlockQueue2;  // AI 스레드에 큐 복사본 할당
	        }
	      
	    }
 	
 	public void generateRandomBlockOnRightBoard() {
 		refillBlockQueueIfNeeded();
 	   Queue<List<Point>> copiedBlockQueue3 = new LinkedBlockingQueue<>(blockGenerator.getQueue());
 	   sharedBlockQueue3 = copiedBlockQueue3;  // 우측 보드 스레드에 큐 복사본 할당2
 	   
 	   currentBlockShape = blockGenerator.getNextBlock();

 	    
 	   rn = new Random(); // 랜덤 객체 초기화
 	   int j = rn.nextInt(7) + 1; // 1부터 7까지 랜덤으로 블록 종류 결정
       int color = j; // 결정된 블록 종류에 해당하는 색상 설정
 		
 	    currentBlockColor = color;
 	    
 	    finish(currentBlockShape);
 	    
 	    // 게임이 종료된 경우 게임 종료 처리
 	    if (isGameEnd) {
 	        endGame(); // 게임 종료
 	        return;
 	    } 

 	    // 새로운 블록을 오른쪽 보드에 배치
 	    for (Point p : currentBlockShape) {
 	        if (p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = currentBlockColor;
 	        }
 	    }
 	    
 		

 	    // 타이머를 설정하여 블록이 아래로 내려가도록 처리
 	    javax.swing.Timer timer = new javax.swing.Timer(time, new ActionListener() {
 	        @Override
 	        public void actionPerformed(ActionEvent e) {
 	            // 블록이 내려가면서 더 이상 내려갈 곳이 없으면
 	            if (!moveBlockDown(currentBlockShape)) {
 	                // 타이머를 중지하고
 	                ((javax.swing.Timer) e.getSource()).stop();
 	                // 라인 체크 후 클리어 처리
 	                checkAndClearLines(); 
 	                // 현재 보드 상태를 저장
 	                saveCurrentBoardState();
 	                // 새로운 블록을 오른쪽 보드에 생성
 	                generateRandomBlockOnRightBoard(); 
 	            }
 	            // 게임 보드를 다시 그리기
 	            t_board.repaint();
 	        }
 	    });
 	    timer.start();
 	}

 	
 	 
 	 
     
 	public void setTime(int time) {
        this.time = time;
    }

    
 	
 	
 	
 	public void rotate(List<Point> block) {
 	    int pivot_x = block.get(1).x;
 	    int pivot_y = block.get(1).y;

 	    // ?��?��?��?��?��?�� ?��?��?��?���? ?��?��?��?��?��?��?���?
 	    for (Point p : block) {
 	        if (p.x >= 0 && p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = 0;
 	        }
 	    }

 	    // ?��?��?��?�� ?��?��?��?�� ?��?��?��?��?��?��
 	    List<Point> rotatedBlock = new ArrayList<>();
 	    for (Point p : block) {
 	        int oldX = p.x, oldY = p.y;
 	        int newX = pivot_x - (oldY - pivot_y); // -y
 	        int newY = pivot_y + (oldX - pivot_x); // +x
 	        rotatedBlock.add(new Point(newX, newY));
 	    }

 	    // ?��?��?��?�� ?��?��?�� ?��?��?��?��?��?��?���? ?��?��?��?��?��?��?��?��?��?��?��?�� ?��?��?��?��?��?��?��?��?��?��?��?�� ?��?��?��?��
 	    boolean isOutOfBounds = false;
 	    for (Point p : rotatedBlock) {
 	        if (p.x < 0 || p.x >= 20 || p.y < 0 || p.y >= 10 || rightBoard[p.x][p.y] != 0) {
 	            isOutOfBounds = true;
 	            break;
 	        }
 	    }

 	    // ?��?��?��?��?��?��?�� ?��?��?��?��?��?��몌옙 ?��?��?��?��?��?��?���? ?��?��?��?��?��?��?�� ?��?��?��?��?��뤄옙 ?��?��?��?��?��?��?��?��?��?��
 	    if (!isOutOfBounds) {
 	        for (int i = 0; i < block.size(); i++) {
 	            block.get(i).x = rotatedBlock.get(i).x;
 	            block.get(i).y = rotatedBlock.get(i).y;
 	        }
 	    } else {
 	        // ?��?��?��?��?��?��?�� ?��?��?��?��?��?��?��?��?��?�� ?��?��?��?��?��?��?��?��?��, ?��?��?��?��?��?��?��?��?��?�� ?��?��?��?��?��?�� (?��?��?��?��?��?��?���? ?��?��?��?��?��?�� ?��?��?���? ?��?��?��?��?��?��)
 	        for (Point p : block) {
 	            rightBoard[p.x][p.y] = currentBlockColor;
 	        }
 	    }

 	    // ?��?��?��?��?��?��?�� ?��?��?��치占?��?�� ?��?��?��?���? ?��?��?���?
 	    for (Point p : block) {
 	        if (p.x >= 0 && p.x < 20 && p.y >= 0 && p.y < 10) {
 	            rightBoard[p.x][p.y] = currentBlockColor;
 	        }
 	    }

 	    // ?��?��?��?�� ?��?��?��?��?��?��
 	    t_board.repaint();
 	}




 	
 	public void checkAndClearLines() {
 	    for (int i = 0; i < rightBoard.length; i++) {
 	        boolean isFullLine = true;
 	        for (int j = 0; j < rightBoard[i].length; j++) {
 	            if (rightBoard[i][j] == 0) {
 	                isFullLine = false;
 	                break;
 	            }
 	        }
 	        if (isFullLine) {
 	        	CURRENT_LINE++;
 	            // ?��?��?��?��?��?��?��?��?�� ?��?��?��?��?��?��?��?��곤옙 ?��?��?��?��?��?��?��?��?�� ?��?��?�� �? ?��?��뤄옙?��?��?�� ?��?��?��?��
 	            for (int k = i; k > 0; k--) {
 	                for (int j = 0; j < rightBoard[k].length; j++) {
 	                    rightBoard[k][j] = rightBoard[k - 1][j];
 	                }
 	            }
 	            for (int j = 0; j < rightBoard[0].length; j++) {
 	                rightBoard[0][j] = 0; // ?��?��?��?��?���? ?��?��?�� ?��?��깍옙?��
 	            }
 	        }
 	    }
 	}
}

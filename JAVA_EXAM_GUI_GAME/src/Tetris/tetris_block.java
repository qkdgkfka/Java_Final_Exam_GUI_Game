package Tetris;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class tetris_block {
    private Random rn; // 랜덤 객체 (블록의 종류를 결정하기 위해 사용)
    public int color, numOfRotate = 4; // 블록 색상과 회전 가능한 횟수
    public static final int MAX_QUEUE_SIZE = 100; // 블록 큐 최대 크기
    private static final int MIN_QUEUE_SIZE = 1;  // 큐가 비어가는 기준점
    public static Queue<List<Point>> sharedBlockQueue = new LinkedBlockingQueue<>();

    
    // 생성자
    public tetris_block() {
        rn = new Random(); // 랜덤 객체 초기화
    }

    public static Queue<List<Point>> getQueue() {
        return sharedBlockQueue;
    }

    // 초기 블록 큐 생성
    public synchronized void initializeBlockQueue() {
   
    	if (!sharedBlockQueue.isEmpty()) {
            return; // 큐가 차있으면 초기화 x
        }
    	
    	
        for (int i = 0; i < MAX_QUEUE_SIZE; i++) {
            sharedBlockQueue.add(generateRandomBlock());
        } 
        System.out.println("Block queue initialized with " + MAX_QUEUE_SIZE + " blocks.");
    }

 // 큐를 보충하는 함수 (큐가 10개 이하일 경우 호출)
    public void refillBlockQueueIfNeeded() {
        if (sharedBlockQueue.size() <= MIN_QUEUE_SIZE) {
            for (int i = 0; i < MAX_QUEUE_SIZE; i++) {
                sharedBlockQueue.add(generateRandomBlock());
            }
            System.out.println("Block qnew blocks!");
        }
    }

    // 공통 블록 큐에서 블록 가져오기
    public List<Point> getNextBlock() {
        refillBlockQueueIfNeeded(); // 큐가 부족하면 블록 생성
        List<Point> block = sharedBlockQueue.poll(); // 큐에서 블록 가져오기

        return block; // 가져온 블록 반환
    }
    
    
    // 랜덤한 테트리스 블록을 생성하는 메서드
    public List<Point> generateRandomBlock() {
        int j = rn.nextInt(7) + 1; // 1부터 7까지 랜덤으로 블록 종류 결정
        this.color = j; // 결정된 블록 종류에 해당하는 색상 설정
        List<Point> list = new ArrayList<>(); // 블록의 좌표를 저장할 리스트

        // 블록 종류에 따른 회전 횟수 설정
        if (j == 1 || j == 5 || j == 6)
            this.numOfRotate = 2; // I, Z, S 블록은 회전이 2번만 가능
        else if (j == 7)
            this.numOfRotate = 1; // O 블록은 회전이 불가능
        else
            this.numOfRotate = 4; // 나머지 블록들은 4번 회전 가능

        // 블록의 좌표 설정
        if (j == 1) { // I 블록
            int[] x_idx = { 0, 0, 0, 0 }; // X 좌표
            int[] y_idx = { 0, 1, 2, 3 }; // Y 좌표
            make(list, x_idx, y_idx); // make 메서드를 사용해 블록 좌표 추가

        } else if (j == 2) { // L 블록
            int[] x_idx = { 1, 1, 0, 1 }; // X 좌표
            int[] y_idx = { 0, 1, 1, 2 }; // Y 좌표
            make(list, x_idx, y_idx);

        } else if (j == 3) { // J 블록
            int[] x_idx = { 1, 1, 1, 0 }; // X 좌표
            int[] y_idx = { 0, 2, 1, 2 }; // Y 좌표
            make(list, x_idx, y_idx);

        } else if (j == 4) { // T 블록
            int[] x_idx = { 0, 1, 1, 1 }; // X 좌표
            int[] y_idx = { 0, 0, 1, 2 }; // Y 좌표
            make(list, x_idx, y_idx);

        } else if (j == 5) { // Z 블록
            int[] x_idx = { 0, 0, 1, 1 }; // X 좌표
            int[] y_idx = { 0, 1, 1, 2 }; // Y 좌표
            make(list, x_idx, y_idx);

        } else if (j == 6) { // S 블록
            int[] x_idx = { 1, 1, 0, 0 }; // X 좌표
            int[] y_idx = { 0, 1, 1, 2 }; // Y 좌표
            make(list, x_idx, y_idx);

        } else { // O 블록
            int[] x_idx = { 0, 0, 1, 1 }; // X 좌표
            int[] y_idx = { 0, 1, 0, 1 }; // Y 좌표
            make(list, x_idx, y_idx);
        }


        return list; // 색상과 함께 Block 객체 반환
    }

    // 블록의 좌표를 리스트에 추가하는 메서드
    private void make(List<Point> list, int[] x_idx, int[] y_idx) {
        // 주어진 x, y 좌표 배열을 이용해 블록의 좌표를 생성하고 리스트에 추가
        for (int i = 0; i < 4; i++) {
            list.add(new Point(x_idx[i], y_idx[i])); // Point 객체를 생성하여 리스트에 추가
        }
    }
}
package Tetris;

import java.util.*;

public class Calculator {
	
	public static Double blockFitness() {
		double result = 0.0;

		// hole_score: 구멍의 개수에 따른 점수, 구멍이 많을수록 점수는 더 낮아짐
		double hole_score = hole_count() * 3; 
		// line_score: 완성된 라인의 개수에 따른 점수
		double line_score = complete_line() * 8.8;

		// 각 열의 높이를 저장할 배열
		int[] height = new int[AI_Tetris.board[0].length];
		// 높이에 대한 점수 계산
		double height_score = aggregate_height(height);
		// bumpiness: 높이 차이에 따른 점수 계산
		double gap_score = bumpiness(height);
		// 최대 높이 계산
		double max_height = max_height();
		
		// 결과 점수 계산 (점수의 합)
		return result = line_score - gap_score - hole_score + height_score - max_height; 
	}

	// 완성된 라인의 개수를 계산하는 함수
	private static int complete_line() {
		int complete_line = 0;
		// 보드의 각 행을 검사
		for (int i = 0; i < AI_Tetris.board.length; i++) {
			int count = 0;
			// 각 행의 열을 검사하여 블록이 채워진 상태인지 확인
			for (int j = 0; j < AI_Tetris.board[0].length; j++) {
				if (AI_Tetris.board[i][j] != 0) 
					count++;
			}

			// 모든 칸이 채워졌다면 한 줄이 완성된 것으로 간주
			if (count == AI_Tetris.board[0].length) 
				complete_line++;
		}

		return complete_line;
	}

	// bumpiness: 높이 차이에 따른 점수를 계산하는 함수
	private static int bumpiness(int height[]) {
		int bumpiness = 0;
		
		// 높이 차이를 구하여 bumpiness 값을 계산
		for (int i = 1; i < height.length; i++) {
			bumpiness += Math.abs(height[i - 1] - height[i]);
		}

		return bumpiness;
	}

	// aggregate_height: 전체 높이를 합산하는 함수
	private static int aggregate_height(int height[]) {
	    // 각 열의 최상단 블록을 찾고 높이를 계산
	    for (int i = 0; i < AI_Tetris.board[0].length; i++) {
	        int startX = -1;

	        // 현재 열에서 첫 번째 블록을 찾음
	        for (int j = 0; j < AI_Tetris.board.length; j++) {
	            if (AI_Tetris.board[j][i] != 0) {  
	                startX = j;  // 첫 번째 블록을 찾음
	                break;  // 블록을 찾으면 더 이상 탐색하지 않음
	            }
	        }

	        // 블록을 찾지 못했다면 0으로 설정
	        height[i] = (startX == -1) ? 0 : startX;
	    }

	    int Height_Sum = 0;

	    // 높이를 합산하여 총 높이를 구함
	    for (int i = 0; i < height.length; i++) {
	    	Height_Sum += height[i];
	    }

	    return Height_Sum;
	}

	// hole_count: 보드에 있는 구멍의 개수를 세는 함수
	private static int hole_count() {
	    int holeCount = 0;

	    // 각 열을 검사하여 구멍의 개수를 계산
	    for (int j = 0; j < AI_Tetris.board[0].length; j++) {
	        boolean blockFound = false;

	        // 각 행을 검사하여 블록이 있는 위치 이후의 구멍을 계산
	        for (int i = 0; i < AI_Tetris.board.length; i++) {
	            if (AI_Tetris.board[i][j] != 0) {
	                blockFound = true;  // 블록이 있는 위치를 찾음
	            } 
	            
	            // 블록이 있으면 그 아래에 있는 빈 공간을 구멍으로 간주
	            else if (blockFound) {
	            	  if (AI_Tetris.board[i][j] == 0)
	            		  holeCount++;  
	            }
	        }
	    }

	    return holeCount;
	}
	
	// max_height: 블록의 최대 높이를 구하는 함수
	private static int max_height() {
	    int maxHeight = 0; // 최대 높이를 저장할 변수

	    // 열(column)을 기준으로 반복
	    for (int i = 0; i < AI_Tetris.board[0].length; i++) {
	        int columnHeight = 0;

	        // 행(row)을 기준으로 최상단 블록을 찾음
	        for (int j = 0; j < AI_Tetris.board.length; j++) {
	            if (AI_Tetris.board[j][i] != 0) {
	                columnHeight = AI_Tetris.board.length - j; // 현재 열의 높이 계산
	                break; // 최상단 블록을 찾았으므로 반복 종료
	            }
	        }

	        // 현재 열의 높이가 최대 높이보다 크면 최대 높이를 갱신
	        if (columnHeight > maxHeight) {
	            maxHeight = columnHeight; 
	        }
	    }

	    return maxHeight; // 블록의 최대 높이를 반환
	}
}

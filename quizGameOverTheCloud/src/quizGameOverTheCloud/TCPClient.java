package quizGameOverTheCloud;
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
	public static void main(String[] args) throws Exception {
		try {
			String ip = "localhost"; // 기본값 설정
	        int port = 7777;         // 기본 포트 설정

	        // 파일 위치 설정
	        String path = "serverInfo.dat";

	        // 서버의 IP 주소와 포트를 파일에서 읽기
	        try (BufferedReader bis = new BufferedReader(new FileReader(path))) {
	            // 파일에서 읽어온 IP와 포트를 변수에 저장
	            ip = bis.readLine();
	            port = Integer.parseInt(bis.readLine());
	            System.out.println("Read IP and port from file: " + ip + ":" + port);
	            bis.close();
	        } catch (IOException | NumberFormatException e) { // 파일에서 IP주소와 포트를 읽어오는데 실패할 경우, 기본값 사용
	            System.out.println("Failed to read IP and port from file. Using default values: " + ip + ":" + port);
	        }		
			//소켓만들기
			var socket = new Socket(ip, port);
			//입력받기
			
			BufferedReader inFromServer = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in));
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to the server.");

            // 서버의 환영 메시지 수신
            String welcomeMessage = inFromServer.readLine();
            System.out.println("FROM SERVER: " + welcomeMessage);

            // 문제 5개에 대해 답을 입력하고, 점수를 받음
            String question;
            String c;
            for (int i = 0; i < 5; i++) {
                question = inFromServer.readLine();
                System.out.println("FROM SERVER: " + question);
                System.out.print("Your answer: ");
                String answer = userInput.readLine();
                outToServer.writeBytes(answer + '\n'); // 서버로 답 전송
                c = inFromServer.readLine();
                System.out.println("FROM SERVER: " + c); // feedback 받아오기
            }

            // 점수 받기
            String score = inFromServer.readLine();
            System.out.println("FROM SERVER: " + score);
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}

package quizGameOverTheCloud;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TCPServer {
    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(7777)) {
            System.out.println("The server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                Socket sock = listener.accept();
                pool.execute(new Quizgame(sock));
            }
        }
    }
}

class Quizgame implements Runnable {
    private Socket socket;
    private static final String[] QUESTIONS = {
        "What is the capital of South Korea?",
        "What is 2 + 2?",
        "What is the largest planet in our solar system?",
        "Who wrote 'And then There were none'?",
        "What is the square root of 16?"
    };
    private static final String[] ANSWERS = {
        "seoul",
        "4",
        "jupiter",
        "agatha christie",
        "4"
    };

    Quizgame(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Connected: " + socket);
        try {
            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

            // 클라이언트에게 인사 메시지 전송
            outToClient.writeBytes("Welcome to the Quiz Game!\n");
            int score = 0;

            // 5개의 문제를 순차적으로 클라이언트에게 전송하고 답을 받음
            for (int i = 0; i < QUESTIONS.length; i++) {
                outToClient.writeBytes("Question " + (i + 1) + ": " + QUESTIONS[i] + "\n");
                String clientAnswer = inFromClient.readLine().toLowerCase().trim();
                if (clientAnswer.equals(ANSWERS[i])) {
                	outToClient.writeBytes("Correct!\n");
                    score++;
                }
                else {
                	outToClient.writeBytes("Incorrect\n");
                }
            }

            // 점수 전송
            outToClient.writeBytes("Your score is: " + score + " out of 5\n");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
            System.out.println("Closed: " + socket);
        }
    }
}

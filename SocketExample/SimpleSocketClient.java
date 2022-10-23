package SocketExample;

// https://jdm.kr/blog/154
// 자바 동기(synchronous) 소켓 프로그래밍

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class SimpleSocketClient {
	// 연결할 포트를 지정합니다.
	private static final int PORT = 8080;
	public static void main(String[] args) {
		try {
			// 소켓을 생성합니다.
			Socket socket = new Socket("localhost", PORT);
			// 스트림을 얻어옵니다.
			InputStream stream = socket.getInputStream();
			// 스트림을 래핑합니다.
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			// 결과를 읽습니다.
			String response = br.readLine();
			System.out.println(response); // 결과물 출력

			socket.close(); // 소켓을 닫습니다.
			System.exit(0); // 프로그램 종료

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
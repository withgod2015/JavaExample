package SocketExample;

// https://jdm.kr/blog/154
// 자바 동기(synchronous) 소켓 프로그래밍


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SimpleSocketServer {
	// 연결할 포트를 지정합니다.
	private static final int PORT = 8080;
	public static void main(String[] args) {

		try {
			try (// 서버소켓 생성
			ServerSocket serverSocket = new ServerSocket(PORT)) {
				// 소켓서버가 종료될때까지 무한루프
				while(true){
					// 소켓 접속 요청이 올때까지 대기합니다.
					Socket socket = serverSocket.accept();
					try{
						// 응답을 위해 스트림을 얻어옵니다.
						OutputStream stream = socket.getOutputStream();
						// 그리고 현재 날짜를 출력해줍니다.
						stream.write(new Date().toString().getBytes());
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						// 반드시 소켓은 닫습니다.
						socket.close();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
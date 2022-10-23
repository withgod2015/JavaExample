package SocketExample;

// https://note.espriter.net/1097
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static void main(String[] args) {
        ServerSocket ss = null;
        
        Socket socket = null;
        try {
            ss = new ServerSocket(9998);
            System.out.println("서버 서비스 운영중....");
            socket = ss.accept(); // 클라이언트의 요청이 오면 소켓 객체를 만들어줌
            System.out.println("접속자 정보:" + socket.toString());
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data = reader.readLine();
            System.out.println("수신자료 :" + data);
            
            reader.close();
            socket.close();
            ss.close();
            
        } catch (Exception e) {
            System.out.println("server err : " + e);
        }
        
    }

}
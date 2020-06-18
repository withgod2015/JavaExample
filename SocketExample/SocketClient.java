
// https://note.espriter.net/1097
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.BufferUnderflowException;
public class SocketClient {
       public static void main(String[] args) {
              try {
                     Socket socket = new Socket("192.168.0.54", 9998);
              
              // 요청 후 자료 서버로 전송
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     out.println("Hi I'm espriter" + "\n");
                     out.close();
                     socket.close();
                     
                     
              } catch (Exception e) {
                     System.out.println("client err:" + e);
              }
       }
}

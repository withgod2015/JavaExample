import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Executors;
public class NonBlockingIOClient implements Runnable {
private InetSocketAddress connectAddress;
private static char CR = (char) 0x0D;
private static char LF = (char) 0x0A;
private Scanner scanner = new Scanner(System.in);
// ip와 port 설정
public NonBlockingIOClient(String address, int port) {
connectAddress = new InetSocketAddress(address, port);
}
// Thread 실행.
public void run() {
// 셀렉터 설정
try (Selector selector = Selector.open()) {
// 소켓 접속
SocketChannel channel = SocketChannel.open(connectAddress);
// 채널 Non-blocking 설정
channel.configureBlocking(false);
// Socket 채널을 channel에 송신 등록한다
channel.register(selector, SelectionKey.OP_READ, new StringBuffer());
// 셀렉터가 있을 경우.
while (selector.select() > 0) {
// 셀렉터 키 셋를 가져온다.
Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
// 키가 있으면..
while (keys.hasNext()) {
SelectionKey key = keys.next();
//키 셋에서 제거.
keys.remove();
if (!key.isValid()) {
continue;
}
// 수신일 경우..
if (key.isReadable()) {
this.receive(selector, key);
// 발신일 경우..
} else if (key.isWritable()) {
this.send(selector, key);
}
}
}
} catch (IOException e) {
e.printStackTrace();
}
}
// 발신시 호출 함수
private void receive(Selector selector, SelectionKey key) {
try {
// 키 채널을 가져온다.
SocketChannel channel = (SocketChannel) key.channel();
// 채널 Non-blocking 설정
channel.configureBlocking(false);
// 소켓 취득
Socket socket = channel.socket();
// Byte 버퍼 생성
ByteBuffer buffer = ByteBuffer.allocate(1024);
// ***데이터 수신***
int size = channel.read(buffer);
// ***데이터 수신***
if (size == -1) {
SocketAddress remoteAddr = socket.getRemoteSocketAddress();
System.out.println("Connection closed by client: " + remoteAddr);
// 소켓 채널 닫기
channel.close();
// 소켓 닫기
socket.close();
// 키 닫기
key.cancel();
return;
}
// ByteBuffer -> byte[]
byte[] data = new byte[size];
System.arraycopy(buffer.array(), 0, data, 0, size);
// StringBuffer 취득
StringBuffer sb = (StringBuffer) key.attachment();
// 버퍼에 수신된 데이터 추가
sb.append(new String(data));
// 데이터 끝이 개행 일 경우. + >
if (sb.length() > 3 && sb.charAt(sb.length() - 3) == CR && sb.charAt(sb.length() - 2) == LF && sb.charAt(sb.length() - 1) == '>') {
// 메시지를 콘솔에 표시
String msg = sb.toString();
System.out.print(msg);
// StringBuffer 초기화
sb.setLength(0);
// Socket 채널을 channel에 송신 등록한다
channel.register(selector, SelectionKey.OP_WRITE, sb);
}
} catch (IOException e) {
e.printStackTrace();
}
}
// 발신시 호출 함수
private void send(Selector selector, SelectionKey key) {
try {
// 키 채널을 가져온다.
SocketChannel channel = (SocketChannel) key.channel();
// 채널 Non-blocking 설정
channel.configureBlocking(false);
// 콘솔에서 값을 입력 받는다.
String msg = scanner.next() + "\r\n";
ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
// ***데이터 송신***
channel.write(buffer);
// Socket 채널을 channel에 수신 등록한다
channel.register(selector, SelectionKey.OP_READ, key.attachment());
} catch (IOException e) {
e.printStackTrace();
}
}
// 시작 함수
public static void main(String[] args) {
// 포트는 10000을 Listen한다.
Executors.newSingleThreadExecutor().execute(new NonBlockingIOServer("localhost", 10000));
// 클라이언트 쓰레드
Executors.newSingleThreadExecutor().execute(new NonBlockingIOClient("127.0.0.1", 10000));
}
}
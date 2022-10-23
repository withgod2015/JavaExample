package SocketExample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executors;
public class NonBlockingIOServer implements Runnable {
    
    private InetSocketAddress listenAddress;
    
    // 메시지는 개행으로 구분한다.
    private static char CR = (char) 0x0D;
    private static char LF = (char) 0x0A;

    // ip와 port 설정
    public NonBlockingIOServer(String address, int port) {
        listenAddress = new InetSocketAddress(address, port);
    }
    
    // Thread 실행.
    public void run() {
    
        // 셀렉터 설정
    try (Selector selector = Selector.open()) {
        // 채널 설정
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            // non-Blocking 설정
            serverChannel.configureBlocking(false);
            // 서버 ip, port 설정
            serverChannel.socket().bind(listenAddress);
            // 채널에 accept 대기 설정
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
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
                    // 접속일 경우..
                    if (key.isAcceptable()) {
                    this.accept(selector, key);
                    // 수신일 경우..
                    } else if (key.isReadable()) {
                    this.receive(selector, key);
                    // 발신일 경우..
                    } else if (key.isWritable()) {
                    this.send(selector, key);
                    }
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// 접속시 호출 함수..
private void accept(Selector selector, SelectionKey key) {
    try {
        // 키 채널을 가져온다.
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // accept을 해서 Socket 채널을 가져온다.
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        // 소켓 취득
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);
        // 접속 Socket 단위로 사용되는 Buffer;
        StringBuffer sb = new StringBuffer();
        sb.append("Welcome server!\r\n>");
        // Socket 채널을 channel에 송신 등록한다
        channel.register(selector, SelectionKey.OP_WRITE, sb);
    } catch (IOException e) {
            e.printStackTrace();
    }
}

// 수신시 호출 함수..
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
        // 수신 크기가 없으면 소켓 접속 종료.
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
        // 데이터 끝이 개행 일 경우.
        if (sb.length() > 2 && sb.charAt(sb.length() - 2) == CR && sb.charAt(sb.length() - 1) == LF) {
            // 개행 삭제
            sb.setLength(sb.length() - 2);
            // 메시지를 콘솔에 표시한다.
            String msg = sb.toString();
            System.out.println(msg);
            // exit 경우 접속을 종료한다.
            if ("exit".equals(msg)) {
                // 소켓 채널 닫기
                channel.close();
                // 소켓 닫기
                socket.close();
                // 키 닫기
                key.cancel();
                return;
            }
            // Echo - 메시지> 의 형태로 재 전송.
            sb.insert(0, "Echo - ");
            sb.append("\r\n>");
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
        // StringBuffer 취득
        StringBuffer sb = (StringBuffer) key.attachment();
        String data = sb.toString();
        // StringBuffer 초기화
        sb.setLength(0);
        // byte 형식으로 변환
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        // ***데이터 송신***
        channel.write(buffer);
        // Socket 채널을 channel에 수신 등록한다
        channel.register(selector, SelectionKey.OP_READ, sb);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


// 시작 함수
public static void main(String[] args) {
    // 포트는 10000을 Listen한다.
    Executors.newSingleThreadExecutor().execute(new NonBlockingIOServer("localhost", 10000));
}
}
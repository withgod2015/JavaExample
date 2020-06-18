/*
 * Author : Gompang
 * Desc : MessagePacker를 활용한 비동기 통신 프로그램 서버
 * Blog : http://gompangs.tistory.com/
 */

package Chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

import MsgPacker.MessagePacker;
import MsgPacker.MessageProtocol;

public class ChatServer implements Runnable {

	private Selector selector;private HashMap<SocketChannel,String>dataMapper;
	private InetSocketAddress socketAddress;
	private MessagePacker msg; // 여기서 MessagePacker를 써보자

	public ChatServer(String address, int port) {
		socketAddress = new InetSocketAddress(address, port); // 소켓 주소 설정
		dataMapper = new HashMap<SocketChannel, String>();
	}

	public static void main(String args[]) {
		ChatServer server = new ChatServer("localhost", 31203); // 서버 객체 생성
		server.run(); // 서버 실행
	}

	@Override
	public void run() { // 서버가 실행되면 호출된다.
		// TODO Auto-generated method stub
		try {
			selector = Selector.open(); // 소켓 셀렉터 열기
			ServerSocketChannel socketChannel = ServerSocketChannel.open(); // 서버소켓채널 열기
			socketChannel.configureBlocking(false); // 블럭킹 모드를 False로 설정한다.

			socketChannel.socket().bind(socketAddress); // 서버 주소로 소켓을 설정한다.
			socketChannel.register(selector, SelectionKey.OP_ACCEPT); // 서버셀렉터를 등록한다.

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Server Started!");

		while (true) {
			try {
				selector.select(); // 셀럭터로 소켓을 선택한다. 여기서 Blocking 됨.

				Iterator<?> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()) { // 셀렉터가 가지고 있는 정보와 비교해봄

					SelectionKey key = (SelectionKey) keys.next();
					keys.remove();

					if (!key.isValid()) { // 사용가능한 상태가 아니면 그냥 넘어감.
						continue;
					}

					if (key.isAcceptable()) { // Accept가 가능한 상태라면
						accept(key);
					}

					else if (key.isReadable()) { // 데이터를 읽을 수 있는 상태라면
						readData(key);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void accept(SelectionKey key) { // 전달받은 SelectionKey로 Accept를 진행
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel;

		try {
			channel = serverChannel.accept();
			channel.configureBlocking(false);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connected to: " + remoteAddr);

			// register channel with selector for further IO
			dataMapper.put(channel, remoteAddr.toString());
			channel.register(this.selector, SelectionKey.OP_READ);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readData(SelectionKey key) { // 전달받은 SelectionKey에서 데이터를 읽는다.

		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int numRead = -1;

		try {
			numRead = channel.read(buffer);

			if (numRead == -1) { // 아직 읽지 않았다면 읽는다.
				this.dataMapper.remove(channel);
				Socket socket = channel.socket();
				SocketAddress remoteAddr = socket.getRemoteSocketAddress();
				channel.close();
				key.cancel();
				return;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(buffer.array().length);

		msg = new MessagePacker(buffer.array()); // Byte Data를 다 받아왔다.
		byte protocol = msg.getProtocol();

		switch (protocol) {

		case MessageProtocol.CHAT: {
			System.out.println("CHAT");
			System.out.println(msg.getString());
			System.out.println(msg.getInt());
			break;
		}

		case MessageProtocol.BATTLE_START: {
			System.out.println("BATTLE_START");
			break;
		}

		case MessageProtocol.BATTLE_END: {
			System.out.println("BATTLE_END");
			break;
		}

		case MessageProtocol.WHISPHER: {
			System.out.println("WHISPHER");
			break;
		}
		}

	}
}
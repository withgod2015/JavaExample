/*
 * Author : Gompang
 * Desc : MessagePacker를 활용한 비동기 통신 프로그램 클라이언트
 * Blog : http://gompangs.tistory.com/
 */
package SocketExample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import MsgPacker.MessagePacker;
import MsgPacker.MessageProtocol;

public class ChatClient {
 
    public void startClient() throws IOException, InterruptedException {
 
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 31203);
        SocketChannel client = SocketChannel.open(hostAddress);
 
        System.out.println("Client Started!");
        
        MessagePacker msg = new MessagePacker(); // MessagePacker 사용해보자
        
        msg.SetProtocol(MessageProtocol.CHAT);
        msg.add("채팅 메세지 전송 테스트입니다~~ This is test message for CHAT");
        msg.add(1020302);
        msg.Finish();
        
        client.write(msg.getBuffer());
        client.close();
    }
    
    public static void main(String args[]) throws IOException, InterruptedException{
    	
    	ChatClient client = new ChatClient();
    	client.startClient();
    }
}
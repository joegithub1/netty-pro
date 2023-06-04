package com.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NioClient
 * @Description:
 * @Author huangjian
 * @Date 2023/6/3
 **/
public class NioClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);

        //提供服务端的ip和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        //链接服务器
        if(!socketChannel.connect(inetSocketAddress)){
            while(!socketChannel.finishConnect()){
                System.out.println("因为链接需要时间，客户端不会阻塞，可以做其它工作。");
            }
        }

        //如果连接成功，发送数据
        String msg = "hello send client.";
        //字节数组和msg数据字节数组大小一致
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes("UTF-8"));
        socketChannel.write(byteBuffer);
        System.in.read();
//        socketChannel.close();
    }
}

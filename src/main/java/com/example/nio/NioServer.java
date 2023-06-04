package com.example.nio;

import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName NioServer
 * @Description:
 * @Author huangjian
 * @Date 2023/6/3
 **/
public class NioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //selector
        Selector selector = Selector.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);

        //把serverSocketChannel 注册到 selector中  关系 OP_ACCEPT 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("注册后的selectionKey 数量："+selector.keys().size());
        while(true){
            if(selector.select(1000) == 0){
                System.out.println("服务器等待1秒，无连接。");
                continue;
            }
            //有事件发生的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while(keyIterator.hasNext()){
                SelectionKey selectionKey = keyIterator.next();
                if(selectionKey.isAcceptable()){//如果是op_accept事件
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置非阻塞
                    socketChannel.configureBlocking(false);
                    System.out.println("客户端链接成功，生成一个SocketChannel "+ socketChannel.hashCode());
                    //将SocketChannel 注册到selector中，关注事件为 op_read
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(selectionKey.isReadable()){//发生 op_read事件
//                        SelectableChannel channel = selectionKey.channel();
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    //获取到channel关联的byteBuffer
                    ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println("客户端发送的数据是："+new String(byteBuffer.array()));


                }
                if(selectionKey.isWritable()){//发生 op_write事件

                }

                //手动移除当前的selectiontKey
                keyIterator.remove();
            }


        }
    }
}

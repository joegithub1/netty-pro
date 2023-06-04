package com.example.nio.group;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName NioServerChat
 * @Description:
 * @Author huangjian
 * @Date 2023/6/3
 **/
public class NioServerChat {

    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static  final int PORT = 6667;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public NioServerChat(){
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();

            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            while(true){
                int count = selector.select();
                if(count > 0){//有事件处理
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    if(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        if(key.isAcceptable()){//op_accept事件
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            sc.register(selector,SelectionKey.OP_READ);

                            System.out.println(sdf.format(new Date()) + "【" + sc.getRemoteAddress() + "】 上线了...");
                        }
                        if(key.isReadable()) {//op_read事件  处理读数据
                            readClientData(key);
                        }

                        //移除，防止重复消费
                        iterator.remove();
                    }
                }else{
                    //等待
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    //读取客户端发送的消息
    public void readClientData(SelectionKey key){
        SocketChannel socketChannel = null;

        try {
            socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(200);
            int count = socketChannel.read(byteBuffer);
            if(count > 0){
                //有读取到数据
                String msg = new String(byteBuffer.array());
                System.out.println(sdf.format(new Date()) + " 客户端： "+msg);
                //向其它客户端转发消息
                sendInfoToOtherClients(sdf.format(new Date()) +" "+ msg,socketChannel);
            }
        } catch (IOException e) {
            try {
                System.out.println(sdf.format(new Date()) + "【" + socketChannel.getRemoteAddress() + "】 离线了...");
                //取消注册
                key.cancel();
                //关闭通道
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
        }

    }

    //把消息转发到其它客户端 msg：消息，self：自己客户端
    public void sendInfoToOtherClients(String msg,SocketChannel self){
        //遍历 所有注册到selection上的channel
        try {
            Set<SelectionKey> keys = selector.keys();
            for (SelectionKey key : keys) {
                Channel targetChannel = key.channel();

                //排除 self自己
                if(targetChannel instanceof SocketChannel && targetChannel != self){

                    SocketChannel dest = (SocketChannel) targetChannel;
                    ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());

                    //写入通道
                    dest.write(byteBuffer);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }

    public static void main(String[] args) {

        NioServerChat serverChat = new NioServerChat();
        serverChat.listen();

    }
}

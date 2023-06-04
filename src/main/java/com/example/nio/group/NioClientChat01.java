package com.example.nio.group;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @ClassName NioClientChat
 * @Description:
 * @Author huangjian
 * @Date 2023/6/3
 **/
public class NioClientChat01 {

    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;

    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public NioClientChat01(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            //客户端
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + " is ok");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //向服务器发送消息
    public void sendInfo(String info){
        String msg = "【"+userName + "】"+ " 说：" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取消息
    public void readInfo(){
        try {
            int count = selector.select();
            if(count > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        if(key.isReadable()){ //op_read 读
                            SocketChannel sc =(SocketChannel) key.channel();

                            sc.configureBlocking(false);
                            ByteBuffer byteBuffer =ByteBuffer.allocate(1024);
                            sc.read(byteBuffer);
                            //读取到的消息
                            String msg = new String(byteBuffer.array());
                            System.out.println(msg.trim());



                        }
                        //删除当前的selectionKey
                        iterator.remove();
                    }


            }else{
                //没有可以用的通道
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        NioClientChat01 clientChat = new NioClientChat01();
        //启动线程读取消息
        new Thread(){
            public void run(){
                while (true){
                    clientChat.readInfo();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String s = scanner.nextLine();
            clientChat.sendInfo(s);
        }


    }
}

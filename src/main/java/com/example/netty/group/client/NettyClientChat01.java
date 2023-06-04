package com.example.netty.group.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @ClassName NettyClientChat
 * @Description:
 * @Author huangjian
 * @Date 2023/6/4
 **/
public class NettyClientChat01 {

    private final String host;
    private final int port;
    public NettyClientChat01(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("myDecoder",new StringDecoder());
                            pipeline.addLast("myEncoder",new StringEncoder());
                            pipeline.addLast("myHandler",new NettyClientHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            System.out.println("[客户端]-----------------"+channel.localAddress()+"-------------");

            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()){
                String msg = scanner.nextLine();
                //发送到服务器端
                channel.writeAndFlush(msg);
            }
//            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }
    public static void main(String[] args) {
        new NettyClientChat01("127.0.0.1", 7000).run();
    }
}

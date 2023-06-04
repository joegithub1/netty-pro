package com.example.netty.group.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @ClassName NettyServerChat
 * @Description:
 * @Author huangjian
 * @Date 2023/6/4
 **/
public class NettyServerChat {

    private int port;

    public NettyServerChat(int port){
        this.port = port;
    }

    //处理客户端请求
    public void run(){
        //处理链接请求，处理客户端业务交给workGoup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //配置启动参数
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            //获取pipeline
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //向pipeline 增加解码器
                            pipeline.addLast("myDecoder",new StringDecoder());
                            //向pipeline增加解码器
                            pipeline.addLast("myEncoder",new StringEncoder());

                            //业务处理handler
                            pipeline.addLast("myHandler",new NettyServerHandler());

                        }
                    });

            System.out.println("netty 服务器启动");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }


    public static void main(String[] args) {
        new NettyServerChat(7000).run();
    }
}

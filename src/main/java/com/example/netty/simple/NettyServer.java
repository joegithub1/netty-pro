package com.example.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**简单的
 * @ClassName NettyServer
 * @Description:
 * @Author huangjian
 * @Date 2023/6/3
 **/
public class NettyServer {

    public static void main(String[] args) {

        //创建 bossGroup 和 workGroup

        //处理链接请求，处理客户端业务交给workGoup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //创建服务器端启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup,workGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel 作为服务器的通道
                    .option(ChannelOption.SO_BACKLOG,128) //设置线程队列得到的链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true)  //保持活动链接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道初始化对象
                        //
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //每个客户端对应一个socketChannel
                            //可以使用集合管理SocketChannel， 在推送消息时，可以将业务加入到各个的channel对应的nioEventLoop的taskQueue
                            //或者scheduleTaskQueue
                            System.out.println("客户SocketChannel hash:"+socketChannel.hashCode());

                            //向管道最后增加
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给workGroup的EventLoop 对应的管道设置处理器

            System.out.println("服务器 is ready...");
            //绑定端口并且同步 生成了ChannelFuture对象
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();

            //给ChannelFuture 注册一个监听器，
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        System.out.println("监听端口成功");
                    }else{
                        System.out.println("监听端口失败");
                    }
                }
            });

            //关闭通道进行监听
            ChannelFuture closeFuture = channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}

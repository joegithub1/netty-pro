package com.example.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @ClassName HttpServerInitializer
 * @Description:
 * @Author huangjian
 * @Date 2023/6/4
 **/
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        //向管道加入处理器

        //
        ChannelPipeline pipeline = socketChannel.pipeline();
        //加入一个netty 提供的 httpserverCodec
        //HttpServerCodec 是netty提供的http编码和解码
        pipeline.addLast("myHttpServerCodec",new HttpServerCodec());

        //增加自定义的handler
        pipeline.addLast("myHttpServerHandler",new HttpServerHandler());

    }
}

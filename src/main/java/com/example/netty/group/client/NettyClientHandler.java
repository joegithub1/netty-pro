package com.example.netty.group.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName NettyClientHandler
 * @Description:
 * @Author huangjian
 * @Date 2023/6/4
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    //
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        System.out.println(msg.trim());
    }
}

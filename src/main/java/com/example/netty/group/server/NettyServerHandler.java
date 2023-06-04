package com.example.netty.group.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName NettyServerHandler
 * @Description:
 * @Author huangjian
 * @Date 2023/6/4
 **/
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    //定义channel组，管理所有的channel组
    //全局事件执行器
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");




    //连接建立，表示一旦连接第一个被执行
    //将当前channel加入到channelGroup组中
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //把消息已发送给channelGroup中的channel
        channelGroup.writeAndFlush(sdf.format(new Date())+"["+channel.remoteAddress() + "] 加入聊天\n");
        channelGroup.add(channel);
    }

    //channel处于活动的状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //可以把消息给所有的客户端

        //也可以不发送给客户端
        System.out.println(sdf.format(new Date())+" ["+ctx.channel().remoteAddress()+"] 上线了~\n");
    }

    //断开连接 将客户离开信息 推送给所有在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //会自动把当前channel从channelGroup中移除
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(sdf.format(new Date())+" ["+ctx.channel().remoteAddress()+"] 离开了~\n");
    }

    //当channel处于非活动状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println(sdf.format(new Date())+" ["+ctx.channel().remoteAddress()+"] 离线了~\n");
        System.out.println("目前在线人数："+channelGroup.size());
    }

    //读客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

        Channel selfChannel = channelHandlerContext.channel();

        //遍历channelGoup 根据不同的情况，处理消息
        for (Channel channel : channelGroup) {
            if(channel != selfChannel){ //不是当前的channel，转发消息
                channel.writeAndFlush(sdf.format(new Date())+"["+channel.remoteAddress() + "] 发送了消息："+msg);
            }else{
                //是自己
                channel.writeAndFlush(sdf.format(new Date())+"[我] 发送了消息："+msg);
            }
        }
    }

    //异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭channel
        ctx.close();
    }
}

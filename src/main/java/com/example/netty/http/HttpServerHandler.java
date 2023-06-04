package com.example.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.util.StringUtils;

import java.net.URI;

/**
 * HttpObject：客户端和服务器端相互通信的数据被封装成 httpObject
 *
 * @ClassName HttpServerHandler
 * @Description:
 * @Author huangjian
 * @Date 2023/6/4
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest){

            /**
             * 不同的浏览器 的pipeline,HttpServerHandler的hashcode是不一样的
             * 同一个浏览器 ，每刷新一次：pipeline,HttpServerHandler会产生新的hashcode值
             */
            System.out.println("pipeline hashcode:"+channelHandlerContext.pipeline().hashCode()
            +"，HttpServerHandler hashcode:"+this.hashCode());

            //DefaultHttpRequest
//            System.out.println("数据类型："+msg);
            System.out.println("客户端地址："+ channelHandlerContext.channel().remoteAddress());

            //对特定的资源进行过滤
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri  http://127.0.0.1:8088/favicon.ico
            URI uri = new URI(httpRequest.uri());
            if(uri.getPath().equals("/favicon.ico")){
                System.out.println("请求了 favicon.ico 不做响应");
                return;
            }
            //回复消息给浏览器 [http协议]
            ByteBuf content = Unpooled.copiedBuffer("hello 我是服务器", CharsetUtil.UTF_8);

            //构造一个 httpresponse
            //http协议版本：HttpVersion
            //HttpResponseStatus 状态码
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plan;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());

            channelHandlerContext.writeAndFlush(response);
        }
    }
}

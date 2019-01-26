package com.rs.test.timeserver;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int counter = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {};
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//    	System.out.println("2");
        String body = (String) msg;
        // counter的作用是标记这是第几次收到客户端的请求
//        System.out.println("The time server receive order : " + body + " ; the counter is : " + ++counter);
//        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? 
//                new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        String say= "Hi";
        say = say + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(say.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	   System.out.println("断开连接");
   };
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
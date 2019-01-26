package com.rs.test.timeserver;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class TimeServerHandler2 extends ChannelInboundHandlerAdapter {

    private int counter = 0;
    public TimeServerHandler2(){
    	System.out.println("TimeServerHandler2");
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("1");
    	 ByteBuf buf= Unpooled.copiedBuffer(("hello2"+ System.getProperty("line.separator")).getBytes());
//    	 ReferenceCountUtil.release(msg);//丢弃收到的msg
    	 ctx.fireChannelRead(msg);//传递收到的msg
        ctx.write( buf);
        
        
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
package com.rs.test.timeserver;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeServerHandler.class.getName());

    private int counter;

    private byte[] req;
    private long ct;

    public TimeClientHandler() {
        req = ("hello!" + System.getProperty("line.separator")).getBytes();
        System.out.println("client");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        ByteBuf message = null;
        final int n =100000;
        long curTime= System.currentTimeMillis();
        ct=curTime;
        for(int i = 0; i < n; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            
            ctx.writeAndFlush(message);
            
        }
        System.out.println(System.currentTimeMillis()-curTime);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        // counter的作用是标记这是第几次收到客户端的请求
//        System.out.println("res: " + body + " ; the counter is : " + ++counter);
        
        
//        System.out.println(System.currentTimeMillis()-ct);
        
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("掉线了...");
        System.out.println(System.currentTimeMillis()-ct);
        //使用过程中断线重连
        
    }

    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("Unexpected exception from downstream : ");
//        ctx.close();
    }

}
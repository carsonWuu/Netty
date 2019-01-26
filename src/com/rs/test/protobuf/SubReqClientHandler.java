package com.rs.test.protobuf;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SubReqClientHandler extends ChannelInboundHandlerAdapter {
	private static int count =0;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for(int i = 0; i < 100; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    /**
     * 构建SubscribeReqProto.SubscribeReq对象
     * @param i
     * @return
     */
    private SubscribeReqProto.SubscribeReq subReq(int i) {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(i);
        builder.setUserName("xpleaf");
        builder.setProductName("Netty Book For Protobuf");
        List<String> address = new ArrayList<>();
        address.add("NanJing YuHuaTai");
        address.add("BeiJing LiuLiChange");
        address.add("ShenZhen HongShuLin");
        builder.addAllAddress(address);
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Service accept server subscribe response : [" + msg + "]");
        count++;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	ctx.close();
    	System.out.println(count);
    	
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
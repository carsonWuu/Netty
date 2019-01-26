package com.rs.test.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 由于ProtobufDecoder已经对消息进行了自动解码，因此接收到的订购请求消息可以直接使用
     * 对用户名进行校验，校验通过后构造应答消息返回给客户端，由于使用了ProtobufEncoder，
     * 所以不需要对SubscribeRespProto.SubscribeResp进行手工编码
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq)msg;
        String username = req.getUserName();
        if("xpleaf".equalsIgnoreCase(username)) {
            System.out.println("Service accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    /**
     * 构建SubscribeRespProto.SubscribeResp对象
     * @param subReqID
     * @return
     */
    private SubscribeRespProto.SubscribeResp resp(int subReqID) {
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 发生异常，关闭链路
        ctx.close();
    }
}
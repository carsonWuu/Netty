package com.rs.test.protobuf;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class SubReqServer {
    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 添加日志处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                    	ch.pipeline().addLast(new IdleStateHandler(5,5,10, TimeUnit.SECONDS));
                    	ch.pipeline().addLast(new ServerHeartbeatServerHandler());
                        // 添加ProtobufVarint32FrameDecoder，主要用于Protobuf的半包处理
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        // 添加ProtobufDecoder解码器，它的参数是com.google.protobuf.MessageLite
                        // 实际上就是要告诉ProtobufDecoder需要解码的目标类是什么，否则仅仅从字节数组中是
                        // 无法判断出要解码的目标类型信息的（服务端需要解析的是客户端请求，所以是Req）
                        ch.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                        /**
                         * 来自源码的代码注释，用于Protobuf的半包处理
                         * * An encoder that prepends the the Google Protocol Buffers
                         * <a href="https://developers.google.com/protocol-buffers/docs/encoding?csw=1#varints">Base
                         * 128 Varints</a> integer length field. For example:
                         * <pre>
                         * BEFORE ENCODE (300 bytes)       AFTER ENCODE (302 bytes)
                         * +---------------+               +--------+---------------+
                         * | Protobuf Data |-------------->| Length | Protobuf Data |
                         * |  (300 bytes)  |               | 0xAC02 |  (300 bytes)  |
                         * +---------------+               +--------+---------------+
                         * </pre> *
                         */
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        // 添加ProtobufEncoder编码器，这样就不需要对SubscribeResp进行手工编码
                        ch.pipeline().addLast(new ProtobufEncoder());
                        // 添加业务处理handler
                        ch.pipeline().addLast(new SubReqServerHandler());
                    }
                });

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if(args != null && args.length > 0) {
            try {
                port = Integer.valueOf(port);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        }
        new SubReqServer().bind(port);
    }
}
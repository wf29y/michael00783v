package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.listener.ListenerContent;
import com.leon.datalink.core.listener.ListenerTypeEnum;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.util.HexUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TcpDriver extends AbstractDriver {

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Channel channel;

    public TcpDriver(Map<String, Object> properties) {
        super(properties);
    }

    public TcpDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        Integer port = getIntProp("port");
        if (null == port) return;
        bossGroup = new NioEventLoopGroup(getIntProp("bossTread", 1));
        workerGroup = new NioEventLoopGroup(getIntProp("workerTread", 5));
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast("decoder", new ByteToMessageDecoder() {
                            @Override
                            protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
                                byte[] bytes = new byte[in.readableBytes()];
                                in.getBytes(in.readerIndex(), bytes);
                                in.readRetainedSlice(bytes.length);
                                Map<String, Object> map = new HashMap<>();
                                map.put("bytes", bytes);
                                map.put("hex", HexUtil.bytesToHex(bytes));
                                out.add(map);
                            }
                        });
                        socketChannel.pipeline().addLast("encoder", new MessageToByteEncoder<String>() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, String hex, ByteBuf byteBuf) throws Exception {
                                byteBuf.writeBytes(HexUtil.hexToBytes(hex));
                            }
                        });
                        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<Map<String, Object>>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Map<String, Object> map) throws Exception {
                                // 响应解析
                                String response = getStrProp("response");
                                if (!StringUtils.isEmpty(response)) {
                                    String render = templateEngine.getTemplate(response).render(getVariable(null));
                                    if (!StringUtils.isEmpty(render)) {
                                        response = render;
                                    }
                                    ctx.writeAndFlush(response);
                                }
                                map.put("response", response);
                                sendData(map);
                            }
                        });
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        //绑定端口,开始接收进来的连接
        channel = bootstrap.bind(port).sync().channel();
        ListenerContent.add(port, ListenerTypeEnum.TCP, "TCP driver port");
    }


    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        bossGroup = null;
        workerGroup.shutdownGracefully();
        workerGroup = null;
        channel.closeFuture().syncUninterruptibly();
        channel = null;
        Integer port = getIntProp("port");
        if (null == port) return;
        ListenerContent.remove(port);
    }

    @Override
    public boolean test() {
        Integer port = getIntProp("port");
        if (null == port) return false;
        return NetUtil.isUsableLocalPort(port);
    }

    @Override
    public Object handleData(Object data) throws Exception {
        throw new UnsupportedOperationException();
    }


}

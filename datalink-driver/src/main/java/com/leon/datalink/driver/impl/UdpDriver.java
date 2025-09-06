package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.listener.ListenerContent;
import com.leon.datalink.core.listener.ListenerTypeEnum;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.util.HexUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class UdpDriver extends AbstractDriver {

    private EventLoopGroup bossGroup;

    private Channel channel;

    public UdpDriver(Map<String, Object> properties) {
        super(properties);
    }

    public UdpDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        Integer port = getIntProp("port");
        if (null == port) return;
        bossGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                // 主线程处理
                .channel(NioDatagramChannel.class)
                // 广播
                .option(ChannelOption.SO_BROADCAST, false)
                // 设置读缓冲区为2M
                .option(ChannelOption.SO_RCVBUF, 2048 * 1024)
                // 设置写缓冲区为1M
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NioEventLoopGroup(), new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                // 接收报文
                                ByteBuf in = packet.content();
                                byte[] bytes = new byte[in.readableBytes()];
                                in.getBytes(in.readerIndex(), bytes);
                                in.readRetainedSlice(bytes.length);
                                // 响应解析
                                String response = getStrProp("response");
                                if (!StringUtils.isEmpty(response)) {
                                    String render = templateEngine.getTemplate(response).render(getVariable(null));
                                    if (!StringUtils.isEmpty(render)) {
                                        response = render;
                                    }
                                    byte[] responseBytes = HexUtil.hexToBytes(response);
                                    if (null != responseBytes) {
                                        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(responseBytes), packet.sender()));
                                    }
                                }
                                Map<String, Object> map = new HashMap<>();
                                map.put("bytes", bytes);
                                map.put("hex", HexUtil.bytesToHex(bytes));
                                map.put("response", response);
                                sendData(map);
                            }
                        });
                    }
                });
        channel = bootstrap.bind(port).sync().channel();
        ListenerContent.add(port, ListenerTypeEnum.UDP, "UDP driver port");
    }


    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        bossGroup = null;
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

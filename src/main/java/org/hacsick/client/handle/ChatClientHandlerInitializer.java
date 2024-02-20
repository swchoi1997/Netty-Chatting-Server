package org.hacsick.client.handle;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.hacsick.client.ClientInfo;
import org.hacsick.server.codec.PayloadDecoder;
import org.hacsick.server.codec.PayloadEncoder;

public class ChatClientHandlerInitializer extends ChannelInitializer<SocketChannel> {
    private final ClientInfo info;

    public ChatClientHandlerInitializer(final ClientInfo info) {
        this.info = info;
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JsonObjectDecoder(65536))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new PayloadDecoder()) // Decoder
                .addLast(new ChatClientInboundHandler(info))
                .addLast(new ChatClientOutboundHandler(info))
                .addLast(new PayloadEncoder())
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
        ;
    }
}

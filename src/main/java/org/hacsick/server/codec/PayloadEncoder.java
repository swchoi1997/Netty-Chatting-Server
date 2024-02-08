package org.hacsick.server.codec;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.hacsick.server.message.Message;
import utils.GsonUtils;

@Sharable
public class PayloadEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Message message, final List<Object> list) {
        try {
            final String jsonMessage = GsonUtils.convertClassToJson(message);
            list.add(jsonMessage);
        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
        }

    }
}

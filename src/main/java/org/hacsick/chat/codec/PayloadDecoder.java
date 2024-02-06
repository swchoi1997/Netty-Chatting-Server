package org.hacsick.chat.codec;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.hacsick.chat.message.Payload;
import utils.GsonUtils;

@Sharable
public class PayloadDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final String msg, final List<Object> list)
            throws Exception {
        try {
            Payload payload = GsonUtils.convertJsonToClass(msg, Payload.class);
            list.add(payload);
        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
        }
    }
}

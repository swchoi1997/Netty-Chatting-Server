package org.hacsick.client.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.hacsick.client.ClientInfo;
import org.hacsick.server.message.Command;
import org.hacsick.server.message.EntranceMessage;
import org.hacsick.server.message.Payload;

public class ChatClientInboundHandler extends ChannelInboundHandlerAdapter {

    private final ClientInfo info;

    public ChatClientInboundHandler(final ClientInfo info) {
        this.info = info;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        /*
        Server Connection Complete
         */
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final Payload payload = (Payload) msg;

        switch (payload.getCommand()) {
            case Command.SERVER_ENTER -> {
                EntranceMessage body = (EntranceMessage) payload.getBody();

                this.info.setClientInfo(body.getTo(), body.getUserId(), body.getLobbyId());
                System.out.println(body);
            }
            case Command.CHAT_ROOM_LIST -> {
                System.out.println(payload.getBody().getMessage());
            }
        }


    }
}

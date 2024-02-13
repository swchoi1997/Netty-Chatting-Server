package org.hacsick.server.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.hacsick.server.message.Command;
import org.hacsick.server.message.CommonMessage;
import org.hacsick.server.message.EntranceMessage;
import org.hacsick.server.message.Message;
import org.hacsick.server.message.Payload;
import org.hacsick.server.message.ServerMeta;
import org.hacsick.server.room.ChatRoomManager;
import org.hacsick.server.user.User;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private final ChatRoomManager manager;

    public ChatServerHandler(final ChatRoomManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        final User entranceUser = User.ofRandom(channel, this.manager.getLobby());
        String lobbyId = this.manager.enterLobby(entranceUser);

        EntranceMessage entranceMessage = EntranceMessage.of(
                ServerMeta.SERVER.getValue(),
                entranceUser.getName(),
                manager.getChatRoomContainer().toString(),
                lobbyId,
                entranceUser.getUniqueValue());

        ctx.writeAndFlush(Payload.of(Command.SERVER_ENTER, entranceMessage));
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final Payload payload = (Payload) msg;

        Payload<? extends Message> sendMessage = null;

        switch (payload.getCommand()) {
            case Command.CHAT_ROOM_ENTER -> {

            }
            case Command.CHAT_ROOM_LIST -> {
                final CommonMessage message = CommonMessage.ofAdmin(
                        payload.getBody().getFrom(),
                        this.manager.getChatRoomContainer().toString());

                sendMessage = Payload.of(Command.CHAT_ROOM_LIST,  message);
            }

        }

        ctx.writeAndFlush(sendMessage);
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {

    }
}

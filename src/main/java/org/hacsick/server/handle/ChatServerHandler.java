package org.hacsick.server.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
        String s = this.manager.enterLobby(User.ofRandom(channel, this.manager.getLobby()));
        //TODO Return Lobby and CharRoomList

    }
}

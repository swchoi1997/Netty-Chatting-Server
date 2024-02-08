package org.hacsick.server.handle;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import java.util.UUID;
import org.hacsick.server.codec.PayloadEncoder;
import org.hacsick.server.mq.BlockingMessageQueue;
import org.hacsick.server.mq.MessageQueue;
import org.hacsick.server.room.ChatRoom;
import org.hacsick.server.room.ChatRoomManager;
import org.hacsick.server.room.ChatRoomSubject;
import org.hacsick.server.codec.PayloadDecoder;
import org.hacsick.server.user.User;

public class ChatServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    public static final String LOBBY = "LOBBY";

    private static final ByteToMessageDecoder DELIM_DECODER =
            new DelimiterBasedFrameDecoder(64 * 1024, Delimiters.lineDelimiter());
    private static final StringEncoder ENCODER = new StringEncoder(CharsetUtil.UTF_8);

    private final ChatRoomSubject lobby;
    private final MessageQueue messageQueue;

    public ChatServerHandlerInitializer() {
        this.messageQueue = new BlockingMessageQueue();
        this.lobby = ChatRoom.of(UUID.randomUUID().toString(), LOBBY, messageQueue);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        this.lobby.changeOwner(this.createAdmin(ch));

        ChatRoomManager chatRoomManager = ChatRoomManager.of(this.lobby);

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JsonObjectDecoder(65536))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new PayloadDecoder()) // Decoder
                .addLast(new ChatServerHandler(chatRoomManager))
                .addLast(new PayloadEncoder())
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
        ;
    }

    private User createAdmin(SocketChannel channel) {
        return User.of("ADMIN", channel, this.lobby);
    }

}

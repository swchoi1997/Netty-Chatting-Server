package org.hacsick.chat.server;

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
import java.util.concurrent.LinkedBlockingQueue;
import org.hacsick.chat.mq.BlockingMessageQueue;
import org.hacsick.chat.mq.MessageQueue;
import org.hacsick.chat.room.ChatRoom;
import org.hacsick.chat.room.ChatRoomManager;
import org.hacsick.chat.room.ChatRoomSubject;
import org.hacsick.chat.codec.PayloadDecoder;
import org.hacsick.chat.user.User;

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
        User admin = this.createAdmin(ch);
        ChatRoomManager chatRoomManager = ChatRoomManager.of(this.lobby);

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JsonObjectDecoder(65536))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new PayloadDecoder()) // Decoder
                .addLast(new ChatServerHandler(chatRoomManager))
        ;
    }

    private User createAdmin(SocketChannel channel) {
        return User.of("ADMIN", channel, this.lobby);
    }

}

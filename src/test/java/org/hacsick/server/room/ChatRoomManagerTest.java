package org.hacsick.server.room;

import static org.hacsick.server.handle.ChatServerHandlerInitializer.LOBBY;

import java.util.UUID;
import org.hacsick.server.mq.BlockingMessageQueue;
import org.hacsick.server.mq.MessageQueue;
import org.junit.jupiter.api.Test;

class ChatRoomManagerTest {

    private final MessageQueue messageQueue = new BlockingMessageQueue();

    @Test
    public void constructorTest() {
        ChatRoom chatRoom = ChatRoom.of(UUID.randomUUID().toString(), LOBBY, messageQueue);
        ChatRoomManager manager = ChatRoomManager.of(chatRoom);

        manager.getChatRoomContainer().entrySet().forEach(System.out::println);

    }
}
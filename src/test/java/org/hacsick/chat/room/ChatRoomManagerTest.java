package org.hacsick.chat.room;

import static org.hacsick.chat.server.ChatServerHandlerInitializer.LOBBY;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.hacsick.chat.mq.BlockingMessageQueue;
import org.hacsick.chat.mq.MessageQueue;
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
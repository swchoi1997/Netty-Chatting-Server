package org.hacsick.chat.room;

import static org.hacsick.chat.server.ChatServerHandlerInitializer.LOBBY;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.example.thread.pool.DynamicThreadPool;
import org.example.time.timecal.TimeStringCal;
import org.hacsick.chat.message.Command;
import org.hacsick.chat.message.CommonMessage;
import org.hacsick.chat.mq.MessageQueue;
import org.hacsick.chat.thread.CustomThreadPool;
import org.hacsick.chat.user.User;
import utils.PayloadUtils;

public class ChatRoomManager {

    private final MessageQueue messageQueue;
    private final Map<String, ChatRoomSubject> chatRoomContainer;
    private final Lock roomManagerLock;
    private final ExecutorService pool;


    public static ChatRoomManager of(final ChatRoomSubject lobby) {
        final Map<String, ChatRoomSubject> initChatRoomContainer = new HashMap<>();
        initChatRoomContainer.put(lobby.getUniqueKey(), lobby);

        return new ChatRoomManager(initChatRoomContainer, lobby.getMessageQueue());
    }

    private ChatRoomManager(final Map<String, ChatRoomSubject> chatRoomContainer,
                            final MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
        this.chatRoomContainer = chatRoomContainer;
        this.pool = DynamicThreadPool.newDynamicThreadPool(5, messageQueue.getMessageQueue());
        this.roomManagerLock = new ReentrantLock();
    }

    public void create(final String chatRoomName,
                       final int limitedNum,
                       final boolean roomLatchFlag,
                       final User owner) {
        this.roomManagerLock.lock();
        try {
            final ChatRoom chatRoom = ChatRoom.of(UUID.randomUUID().toString(),
                    chatRoomName,
                    limitedNum,
                    roomLatchFlag,
                    this.messageQueue, owner);

            if (this.chatRoomContainer.containsValue(chatRoom)) {
                owner.notify(PayloadUtils.createMessage(Command.CHAT_ROOM_ERROR,
                        CommonMessage.ofAdmin(owner.getName(), "Duplicate Chat Room Name")));
            }
        } finally {
            this.roomManagerLock.unlock();
        }
    }

    public String enterLobby(final User user) {
        return this.enter(LOBBY, user);
    }

    public String enter(final String chatRoomName, final User user) {

        final Optional<Entry<String, ChatRoomSubject>> beGoingToEnterChatRoomName =
                this.chatRoomContainer.entrySet()
                        .stream()
                        .filter(entry -> chatRoomName.equals(entry.getValue().getRoomName()))
                        .findFirst();

        if (beGoingToEnterChatRoomName.isEmpty()) {
            this.messageQueue.offer(() ->{
                user.notify(PayloadUtils.createMessage(Command.CHAT_ROOM_ERROR,
                        CommonMessage.ofAdmin(user.getName(), "Chat Room Not Exist")));
            });

            return String.valueOf(Optional.empty());
        }
        this.roomManagerLock.lock();
        try {
            final ChatRoomSubject chatRoomSubject = beGoingToEnterChatRoomName.get().getValue();
            chatRoomSubject.enter(user);
            user.setChatRoom(chatRoomSubject);

            return beGoingToEnterChatRoomName.get().getKey();
        } finally {
            this.roomManagerLock.unlock();
        }
    }

    public void leave(final User user) {
        final ChatRoomSubject chatRoom = user.getChatRoom();
        chatRoom.leave(user);
        user.setDefaultChatRoom();
    }

    public void notifyMessage(final User user, final String message) {
        user.getChatRoom().notifyUsers(user, Command.CHAT_ROOM_MESSAGE, message);
    }



    public Map<String, ChatRoomSubject> getChatRoomContainer() {
        return chatRoomContainer;
    }

    public ChatRoomSubject getLobby() {
        final Optional<Entry<String, ChatRoomSubject>> lobby =
                this.chatRoomContainer.entrySet()
                        .stream()
                        .filter(entry -> LOBBY.equals(entry.getValue().getRoomName()))
                        .findFirst();

        return lobby.get().getValue();
    }
}

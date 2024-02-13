package org.hacsick.server.room;

import static org.hacsick.server.message.Command.CHAT_ROOM_ENTER;
import static org.hacsick.server.message.Command.CHAT_ROOM_LEAVE;
import static org.hacsick.server.handle.ChatServerHandlerInitializer.LOBBY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.hacsick.server.message.Command;
import org.hacsick.server.message.CommonMessage;
import org.hacsick.server.message.Payload;
import org.hacsick.server.message.ServerMeta;
import org.hacsick.server.mq.MessageQueue;
import org.hacsick.server.user.User;

public class ChatRoom implements ChatRoomSubject {

    private static final int DEFAULT_LIMITED_NUM = 30;

    private final String uniqueKey;
    private final AtomicBoolean activeFlag = new AtomicBoolean(true);
    private final String roomName;
    private User owner;
    private final Set<User> roomUsers;
    private final int limitedNum;
    private final AtomicBoolean roomLatchFlag;
    private final MessageQueue messageQueue;
    private final Lock lock;

    public static ChatRoom of(final String roomName,
                              final MessageQueue messageQueue) {
        return new ChatRoom(UUID.randomUUID().toString(), roomName, ConcurrentHashMap.newKeySet(), DEFAULT_LIMITED_NUM,
                false, messageQueue, null);
    }

    public static ChatRoom of(final String uniqueKey,
                              final String roomName,
                              final MessageQueue messageQueue) {
        return new ChatRoom(uniqueKey, roomName, ConcurrentHashMap.newKeySet(), DEFAULT_LIMITED_NUM, false,
                messageQueue, null);
    }

    public static ChatRoom of(final String uniqueKey,
                              final String roomName,
                              final MessageQueue messageQueue,
                              final User owner) {
        return new ChatRoom(uniqueKey, roomName, ConcurrentHashMap.newKeySet(), DEFAULT_LIMITED_NUM, false,
                messageQueue, owner);
    }

    public static ChatRoom of(final String uniqueKey,
                              final String roomName,
                              final int limitedNum,
                              final MessageQueue messageQueue,
                              final User owner) {
        return new ChatRoom(uniqueKey, roomName, ConcurrentHashMap.newKeySet(), limitedNum, false, messageQueue, owner);
    }

    public static ChatRoom of(final String uniqueKey,
                              final String roomName,
                              final int limitedNum,
                              final boolean roomLatchFlag,
                              final MessageQueue messageQueue,
                              final User owner) {
        return new ChatRoom(uniqueKey, roomName, ConcurrentHashMap.newKeySet(), limitedNum, roomLatchFlag, messageQueue,
                owner);
    }


    private ChatRoom(final String uniqueKey,
                     final String roomName,
                     final Set<User> roomUsers,
                     final int limitedNum,
                     final boolean roomLatchFlag,
                     final MessageQueue messageQueue,
                     final User owner) {
        this.uniqueKey = uniqueKey;
        this.roomName = roomName;
        this.roomUsers = roomUsers;
        this.limitedNum = limitedNum;
        this.roomLatchFlag = new AtomicBoolean(roomLatchFlag);
        this.messageQueue = messageQueue;
        this.owner = owner;
        this.lock = new ReentrantLock();
    }

    @Override
    public void enter(final User user) {
        if (!this.activeFlag.get()) {
            throw new UnsupportedOperationException("Unsupported Operation");
        }
        if (this.roomLatchFlag.get()) {
            this.notifyUser(user, user, CHAT_ROOM_ENTER, "The chat room is locked.");
        }

        this.lock.lock();
        try {
            if (this.roomUsers.size() >= this.limitedNum) {
                this.notifyUser(user, user, CHAT_ROOM_ENTER, "The chat room is full.");
                return;
            }

            this.roomUsers.add(user);

        } finally {
            this.lock.unlock();
        }
        if (!this.roomName.equals(LOBBY)) {
            this.notifyUsers(owner, CHAT_ROOM_ENTER, user.getName() + " has entered.");
        }
    }

    @Override
    public void leave(final User user) {
        if (!this.roomUsers.contains(user)) {
            throw new IllegalArgumentException();
        }

        this.lock.lock();
        try {
            this.roomUsers.remove(user);
            if (this.roomUsers.size() < this.limitedNum) {
                this.roomLatchFlag.set(false);
                return;
            }
            if (this.owner == user) {
                this.changeOwnerRandom();
            }

        } finally {
            this.lock.unlock();
        }

        if (!this.roomName.equals(LOBBY)) {
            this.notifyUsers(owner, CHAT_ROOM_LEAVE, user.getName() + " has left.");
        }
    }

    @Override
    public void changeOwner(final User from, final User to) {
        lock.lock();
        try {
            if (this.owner != from) {
                throw new UnsupportedOperationException();
            }

            if (this.roomUsers.contains(to)) {
                this.notifyUser(from, from, Command.CHAT_ROOM_ERROR, "This user does not exist in this chat room.");
            }

            this.owner = to;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void changeOwner(final User user) {
        lock.lock();
        try {
            if (ServerMeta.SERVER.getValue().equals(user.getName())) {
                this.owner = user;
            } else {
                throw new UnsupportedOperationException();
            }
        } finally {
            lock.unlock();
        }
    }

    public void changeOwnerRandom() {
        lock.lock();
        try {
            List<User> users = new ArrayList<>(this.roomUsers);
            Collections.shuffle(users);
            this.owner = users.get(0);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void notifyUser(final User from, final User to, final Command command, final String message) {
        if (this.roomName.equals(LOBBY)) {
            this.messageQueue.offer(() -> to.notifyLobby(
                    Payload.of(from.getUniqueValue(),
                            command,
                            CommonMessage.of(from.getName(), to.getName(), message)))
            );
        } else {
            this.messageQueue.offer(() -> to.notifyChatMessage(
                    Payload.of(from.getUniqueValue(),
                            command,
                            CommonMessage.of(from.getName(), to.getName(), message)))
            );
        }
    }


    @Override
    public void notifyUsers(final User from, final Command command, final String message) {
        this.messageQueue.offer(() -> {
            for (User roomUser : roomUsers) {
                this.notifyUser(from, roomUser, command, message);
            }
        });
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public AtomicBoolean getActiveFlag() {
        return activeFlag;
    }

    public boolean isActive() {
        return activeFlag.get();
    }

    public String getRoomName() {
        return roomName;
    }

    public User getOwner() {
        return owner;
    }

    public Set<User> getRoomUsers() {
        return roomUsers;
    }

    public int getLimitedNum() {
        return limitedNum;
    }

    public AtomicBoolean getRoomLatchFlag() {
        return roomLatchFlag;
    }

    @Override
    public MessageQueue getMessageQueue() {
        return this.messageQueue;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "uniqueKey='" + uniqueKey + '\'' +
                ", activeFlag=" + activeFlag +
                ", roomName='" + roomName + '\'' +
                ", owner=" + owner +
                ", roomUsers=" + roomUsers +
                ", limitedNum=" + limitedNum +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(roomName, chatRoom.roomName) && Objects.equals(owner, chatRoom.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName, owner);
    }
}

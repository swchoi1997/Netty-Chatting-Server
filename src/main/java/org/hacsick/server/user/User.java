package org.hacsick.server.user;

import io.netty.channel.Channel;
import java.util.Objects;
import java.util.UUID;
import org.hacsick.server.room.ChatRoomSubject;
import org.hacsick.server.message.Command;
import org.hacsick.server.message.CommonMessage;
import org.hacsick.server.message.Payload;

public class User implements Observer {

    private final static String BASE_NAME = "User-";
    private final String uniqueValue;
    private final String name;
    private final Channel channel;
    private final ChatRoomSubject lobby;
    private ChatRoomSubject chatRoom;

    public static User ofAdmin(final Channel channel) {
        return new User(channel, null);
    }

    public static User ofRandom(final Channel channel, final ChatRoomSubject chatRoom) {
        return new User(channel, chatRoom);
    }

    public static User of(final String name, final Channel channel, final ChatRoomSubject chatRoom) {
        return new User(name, channel, chatRoom);
    }

    private User(final Channel channel, final ChatRoomSubject chatRoom) {
        this.uniqueValue = UUID.randomUUID().toString();
        this.name = this.makeRandomName();
        this.channel = channel;
        this.chatRoom = chatRoom;
        this.lobby = chatRoom;
    }


    private User(final String name, final Channel channel, final ChatRoomSubject chatRoom) {
        this.uniqueValue = UUID.randomUUID().toString();
        this.name = name;
        this.channel = channel;
        this.chatRoom = chatRoom;
        this.lobby = chatRoom;
    }

    public void setChatRoom(final ChatRoomSubject chatRoom) {
        synchronized (this) {
            this.chatRoom = chatRoom;
        }
    }

    public void setDefaultChatRoom() {
        synchronized (this) {
            this.chatRoom = this.lobby;
        }
    }

    public void notify(final Payload payload) {
        this.channel.writeAndFlush(payload);
    }

    public void notifyChatMessage(final Payload payload) {
        if (this.chatRoom == null || !this.chatRoom.isActive()) {
            this.notify(Payload.of("ADMIN",
                    Command.CHAT_ROOM_DESTROY,
                    CommonMessage.of("ADMIN", this.getName(), this.chatRoom.getRoomName() + " Is Destroy")));
            return;
        }
        this.notify(payload);
    }

    public void notifyLobby(final Payload payload) {
        if (this.chatRoom == this.lobby || this.chatRoom == null) {
            this.notify(payload);
        }
    }

    private String makeRandomName() {
        return BASE_NAME + this.uniqueValue.split("-")[4];
    }


    public String getUniqueValue() {
        return uniqueValue;
    }

    public String getName() {
        return name;
    }

    public Channel getChannel() {
        return channel;
    }

    public ChatRoomSubject getLobby() {
        return lobby;
    }

    public ChatRoomSubject getChatRoom() {
        return chatRoom;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(uniqueValue, user.uniqueValue) && Objects.equals(name, user.name)
                && Objects.equals(channel, user.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueValue, name, channel);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}

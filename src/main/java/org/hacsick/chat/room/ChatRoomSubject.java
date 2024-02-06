package org.hacsick.chat.room;

import org.hacsick.chat.message.Command;
import org.hacsick.chat.mq.MessageQueue;
import org.hacsick.chat.user.User;

public interface ChatRoomSubject {

    void enter(final User user);

    void leave(final User user);

    void changeOwner(final User user);

    void changeOwner(final User from, final User to);

    void notifyUser(final User from, final User to, final Command command, final String message);

    void notifyUsers(final User from, final Command command, final String message);

    boolean isActive();

    String getRoomName();

    String getUniqueKey();

    MessageQueue getMessageQueue();
}

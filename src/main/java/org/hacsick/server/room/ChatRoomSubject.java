package org.hacsick.server.room;

import org.hacsick.server.message.Command;
import org.hacsick.server.mq.MessageQueue;
import org.hacsick.server.user.User;

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

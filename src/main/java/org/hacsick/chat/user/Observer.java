package org.hacsick.chat.user;

import org.hacsick.chat.message.Payload;

public interface Observer{

    void notifyChatMessage(final Payload payload);

}

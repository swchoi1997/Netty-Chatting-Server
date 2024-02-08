package org.hacsick.server.user;

import org.hacsick.server.message.Payload;

public interface Observer{

    void notifyChatMessage(final Payload payload);

}

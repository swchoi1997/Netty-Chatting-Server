package utils;

import java.util.UUID;
import org.hacsick.server.message.Command;
import org.hacsick.server.message.Message;
import org.hacsick.server.message.Payload;

public class PayloadUtils {

    public static <T extends Message> Payload<T> createMessage(final Command command,
                                                               final T message) {
        return Payload.of(command, message);
    }
}

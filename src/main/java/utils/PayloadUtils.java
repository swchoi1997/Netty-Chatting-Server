package utils;

import java.util.UUID;
import org.hacsick.chat.message.Command;
import org.hacsick.chat.message.Message;
import org.hacsick.chat.message.Payload;

public class PayloadUtils {

    public static <T extends Message> Payload<T> createMessage(final Command command,
                                                               final T message) {
        return Payload.of(UUID.randomUUID().toString(), command, message);
    }
}

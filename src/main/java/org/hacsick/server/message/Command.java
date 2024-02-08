package org.hacsick.server.message;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {

    SERVER_ENTER(100),
    ROOM_CREATE(200),
    CHAT_ROOM_ENTER(201),
    CHAT_ROOM_LEAVE(202),
    CHAT_ROOM_STATUS_CHANGE(203),
    CHAT_ROOM_DESTROY(204),
    CHAT_ROOM_ERROR(205),
    CHAT_ROOM_MESSAGE(206),
    KNOWN(-1)
    ;

    private final static Map<Integer, Command> COMMAND_MAP =
            Stream.of(values()).collect(Collectors.toMap(Command::getKey, Function.identity()));

    private final int key;

    Command(final int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public static Command find(final Integer key) {
        return Optional.ofNullable(COMMAND_MAP.get(key)).orElse(KNOWN);
    }
}

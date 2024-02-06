package org.hacsick.chat.message;

import java.io.Serializable;

public class Payload<T extends Message> implements Serializable {
    private final String uniqueValue;
    private final Command command;
    private final T body;

    public static <T extends Message> Payload<T> of(final String uniqueValue, final Command command, final T body) {
        return new Payload<T>(uniqueValue, command, body);
    }

    private Payload(final String uniqueValue, final Command command, final T body) {
        this.uniqueValue = uniqueValue;
        this.command = command;
        this.body = body;
    }

    public String getUniqueValue() {
        return uniqueValue;
    }

    public Command getCommand() {
        return command;
    }

    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "uniqueValue='" + uniqueValue + '\'' +
                ", command=" + command +
                ", body=" + body +
                '}';
    }
}

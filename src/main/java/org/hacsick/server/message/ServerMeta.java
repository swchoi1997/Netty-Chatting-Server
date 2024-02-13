package org.hacsick.server.message;

public enum ServerMeta {
    SERVER("SERVER");

    private final String value;

    ServerMeta(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

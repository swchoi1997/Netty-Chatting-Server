package org.hacsick.server.message;

public class EntranceMessage extends CommonMessage {

    private final String lobbyId;

    private final String userId;

    protected EntranceMessage(final String from,
                              final String to,
                              final String message,
                              final String lobbyId,
                              final String userId) {
        super(from, to, message);
        this.lobbyId = lobbyId;
        this.userId = userId;
    }

    public static EntranceMessage of(final String from,
                                     final String to,
                                     final String message,
                                     final String lobbyId,
                                     final String userId) {
        return new EntranceMessage(from, to, message, lobbyId, userId);
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        final String parent = super.toString();

        return parent + "\n" + "EntranceMessage{" +
                "lobbyId='" + lobbyId + '\'' +
                '}';
    }
}

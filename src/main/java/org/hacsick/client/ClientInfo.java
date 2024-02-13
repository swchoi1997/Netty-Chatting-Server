package org.hacsick.client;

public class ClientInfo {

    private String uniqueValue;
    private String name;
    private String currentChatRoomName;


    public ClientInfo() {
    }

    public void setClientInfo(final String uniqueValue,
                              final String name,
                              final String currentChatRoomName) {


    }

    public String getUniqueValue() {
        return uniqueValue;
    }

    public void setUniqueValue(final String uniqueValue) {
        this.uniqueValue = uniqueValue;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCurrentChatRoomName() {
        return currentChatRoomName;
    }

    public void setCurrentChatRoomName(final String currentChatRoomName) {
        this.currentChatRoomName = currentChatRoomName;
    }
}

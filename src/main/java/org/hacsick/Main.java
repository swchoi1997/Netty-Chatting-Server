package org.hacsick;

import org.hacsick.server.ChatServer;

public class Main {
    public static void main(String[] args) {
        //ENTRY
        ChatServer chatServer = new ChatServer(11111);
        chatServer.launch();

    }
}
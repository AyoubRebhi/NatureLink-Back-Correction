package com.example.naturelink.chat;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

public class ChatHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming message from client
        System.out.println("Received message: " + message.getPayload());

        // Send a response back to the client
        session.sendMessage(new TextMessage("Notification sent: " + message.getPayload()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Code to handle after a connection is established
        System.out.println("Connection established with: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Code to handle when the connection is closed
        System.out.println("Connection closed with: " + session.getId());
    }
}

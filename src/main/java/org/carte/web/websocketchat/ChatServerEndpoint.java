package org.carte.web.websocketchat;


import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(
        value="/chat/{username}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class

)
public class ChatServerEndpoint {

    private Session session;
    private static final Set<ChatServerEndpoint> CHAT_SERVER_ENDPOINTS
            = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> USERS = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("username") String username) throws IOException, EncodeException {
        this.session = session;
        CHAT_SERVER_ENDPOINTS.add(this);
        USERS.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, Message message)
            throws IOException, EncodeException {

        message.setFrom(USERS.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {

        CHAT_SERVER_ENDPOINTS.remove(this);
        Message message = new Message();
        message.setFrom(USERS.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {

        synchronized (CHAT_SERVER_ENDPOINTS) {

            CHAT_SERVER_ENDPOINTS.forEach(endpoint -> {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            });

        }
    }
}

package com.abug.chat_service.infrastructure.web.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketHandler {
    private final SocketIOServer server;

    @OnConnect
    public void clientConnected(SocketIOClient client){

    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client){

    }


    @PostConstruct
    public void startServer(){
        server.addListeners(this);
        server.start();
    }

    @PreDestroy
    public void stopServer(){
        server.stop();
    }
}

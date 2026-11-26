package com.abug.chat_service.infrastructure.web.websocket;

import com.abug.chat_service.application.port.in.CreateSessionUseCase;
import com.abug.chat_service.application.port.in.DeleteSessionUseCase;
import com.abug.chat_service.application.port.in.command.CreateWebSocketSessionCommand;
import com.abug.chat_service.application.port.out.IdentityPort;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketHandler {
    private final SocketIOServer server;
    private final IdentityPort identityPort;
    private final CreateSessionUseCase createSessionUseCase;
    private final DeleteSessionUseCase deleteSessionUseCase;

    @OnConnect
    public void clientConnected(SocketIOClient client){
        String token = client.getHandshakeData().getSingleUrlParam("token");

        IdentityPort.Snapshot introspected = identityPort.introspect(token);

        if(introspected.valid()){
            log.info("Client connected: {}, {}", client.getSessionId(), token);
            CreateWebSocketSessionCommand command = new CreateWebSocketSessionCommand(client.getSessionId().toString(), introspected.userId());
            createSessionUseCase.create(command);
        } else {
            log.info("Authentication failed: {}", client.getSessionId());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client){
        log.info("Client Disconnected: {}", client.getSessionId());
        deleteSessionUseCase.deleteSession(client.getSessionId().toString());
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

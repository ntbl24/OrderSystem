package com.abug.chat_service.infrastructure.external.httpclient.adapter;

import com.abug.chat_service.application.port.out.ProfileUserPort;
import com.abug.chat_service.common.exception.AppException;
import com.abug.chat_service.common.exception.ErrorCode;
import com.abug.chat_service.domain.UserId;
import com.abug.chat_service.infrastructure.external.httpclient.ProfileClient;
import com.abug.chat_service.infrastructure.external.httpclient.dto.ProfileUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProfileUserAdapter implements ProfileUserPort {
    private final ProfileClient profileClient;

    public Snapshot getProfile(UserId userId){
        var userInfoResponse = profileClient.getProfile(userId.toString());

        if(Objects.isNull(userInfoResponse)){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        ProfileUserResponse userInfo = userInfoResponse.getData();

        return new Snapshot(userInfo.userId(), userInfo.username(), userInfo.firstName(), userInfo.lastName(), userInfo.avatar());
    }
}

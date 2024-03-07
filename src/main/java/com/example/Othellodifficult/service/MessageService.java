package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.message.MessageInput;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.UserChatMapEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.entity.message.MessageEntity;
import com.example.Othellodifficult.mapper.MessageMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.EventHelper;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final CustomRepository customRepository;

    @Transactional
    public String sendMessage(MessageInput messageInput, String accessToken) {
        LocalDateTime now = LocalDateTime.now();
        Long senderId = TokenHelper.getUserIdFromToken(accessToken);
        ChatEntity chatEntity = customRepository.getChat(messageInput.getChatId());

        MessageEntity messageEntity = messageMapper.getEntityFromInput(messageInput);
        messageEntity.setSenderId(senderId);
        messageEntity.setCreatedAt(LocalDateTime.now());
        Long chatId2;
        if (chatEntity.getChatType().equals(Common.USER)) {
            ChatEntity chatEntity2 = chatRepository.findByUserId1AndUserId2(chatEntity.getUserId2(), chatEntity.getUserId1());
            chatId2 = chatEntity2.getId();
            messageEntity.setChatId1(chatEntity.getId());
            messageEntity.setChatId2(chatEntity2.getId());
            chatEntity2.setNewestChatTime(now);
            chatRepository.save(chatEntity2);
        } else {
            chatId2 = null;
            messageEntity.setGroupChatId(chatEntity.getId());
        }
        messageRepository.save(messageEntity);
        CompletableFuture.runAsync(() -> {
            chatEntity.setNewestChatTime(now);
            chatRepository.save(chatEntity);

            // if chat user-user
            if (chatEntity.getChatType().equals(Common.USER)) {
                eventNotificationRepository.save(
                        EventNotificationEntity.builder()
                                .eventType(Common.MESSAGE)
                                .userId(chatEntity.getUserId2())
                                .state(Common.NEW_EVENT)
                                .chatId(chatId2)
                                .build()
                );
                EventHelper.pushEventForUserByUserId(chatEntity.getUserId2());
            } else { // if chat user-group
                List<UserChatMapEntity> userChatEntities = userChatRepository.findAllByChatId(messageInput.getChatId()).stream()
                        .filter(userChatEntity -> !userChatEntity.getUserId().equals(senderId))
                        .collect(Collectors.toList());
                if (!userChatEntities.isEmpty()) {
                    for (UserChatMapEntity userChatEntity : userChatEntities) {
                        eventNotificationRepository.save(
                                EventNotificationEntity.builder()
                                        .eventType(Common.MESSAGE)
                                        .userId(userChatEntity.getUserId())
                                        .state(Common.NEW_EVENT)
                                        .chatId(chatEntity.getId())
                                        .build()
                        );
                        EventHelper.pushEventForUserByUserId(userChatEntity.getUserId());
                    }
                }
            }
        });
        return messageInput.getMessage();
    }
}

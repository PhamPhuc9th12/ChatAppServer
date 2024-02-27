package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.friend.FriendRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestReposiroty extends JpaRepository<FriendRequestEntity, Long> {
    void deleteByReceiverIdAndSenderId(Long receiverId, Long senderId);
}

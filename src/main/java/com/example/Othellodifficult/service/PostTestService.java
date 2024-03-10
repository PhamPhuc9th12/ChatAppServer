package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.post.CreatePostInput;
import com.example.Othellodifficult.dto.post.PostOutput;
import com.example.Othellodifficult.entity.PostEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.helper.StringUtils;
import com.example.Othellodifficult.mapper.PostMapper;
import com.example.Othellodifficult.repository.PostRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PostTestService {
private final PostMapper postMapper;
private final PostRepository postRepository;
    @Transactional(readOnly = true)
    public Page<PostOutput> getMyPost(String accessToken, Pageable pageable) {
        return null;
    }

    @Transactional
    public void creatPost(String accessToken, CreatePostInput createPostInput) {
            Long userId = TokenHelper.getUserIdFromToken(accessToken);
            PostEntity postEntity = postMapper.getEntityFromInput(createPostInput);
            postEntity.setUserId(userId);
            postEntity.setCreatedAt(LocalDateTime.now());
            postEntity.setCommentCount(0);
            postEntity.setLikeCount(0);
            postEntity.setShareCount(0);
            postEntity.setImageUrlsString(StringUtils.convertListToString(createPostInput.getImageUrls()));
            postRepository.save(postEntity);
    }

    @Transactional
    public void updatePost(String accessToken, Long postId, CreatePostInput updatePostInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(
                () ->  new RuntimeException(Common.ACTION_FAIL)
        );
        if(!userId.equals(postEntity.getUserId())) throw new RuntimeException(Common.ACTION_FAIL);
        postMapper.updateEntityFromInput(postEntity,updatePostInput);
        postEntity.setImageUrlsString(StringUtils.convertListToString(updatePostInput.getImageUrls()));
        postRepository.save(postEntity);
    }

    @Transactional
    public void deletePost(String accessToken, Long postId) {

    }

    @Transactional
    public void sharePost(String accessToken, Long shareId, CreatePostInput sharePostInput) {

    }
}

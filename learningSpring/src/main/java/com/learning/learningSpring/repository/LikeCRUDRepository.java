package com.learning.learningSpring.repository;

import org.springframework.data.repository.CrudRepository;

import com.learning.learningSpring.entity.LikeRecord;
import com.learning.learningSpring.entity.Post;

public interface LikeCRUDRepository extends CrudRepository<LikeRecord, Integer> {
    public Integer countByLikeIdPost(Post post);
}

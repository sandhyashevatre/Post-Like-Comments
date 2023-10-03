package com.prodapt.learningspring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.prodapt.learningspring.entity.Comment;
import com.prodapt.learningspring.entity.LikeId;
import com.prodapt.learningspring.entity.LikeRecord;
import com.prodapt.learningspring.entity.Post;
import com.prodapt.learningspring.entity.User;

public interface LikeCRUDRepository extends CrudRepository<LikeRecord, LikeId>{
    public Integer countByLikeIdPost(Post post);
    public List<LikeRecord> findByLikeIdPost(Post post);
    public LikeRecord findByLikeIdUserAndLikeIdPost(User liker, Post post);
    boolean existsByLikeIdUserAndLikeIdPost(User user, Optional<Post> post);
   
}



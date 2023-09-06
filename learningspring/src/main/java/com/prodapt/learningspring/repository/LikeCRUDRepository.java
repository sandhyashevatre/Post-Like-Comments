package com.prodapt.learningspring.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.prodapt.learningspring.entity.LikeId;
import com.prodapt.learningspring.entity.LikeRecord;
import com.prodapt.learningspring.entity.Post;

public interface LikeCRUDRepository extends CrudRepository<LikeRecord, LikeId>{
  public Integer countByLikeIdPost(Post post);
    public List<LikeRecord> findByLikeIdPost(Post post);
}

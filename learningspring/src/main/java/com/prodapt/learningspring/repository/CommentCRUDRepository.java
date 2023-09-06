package com.prodapt.learningspring.repository;

import java.util.List;

 

import org.springframework.data.repository.CrudRepository;

import com.prodapt.learningspring.entity.Comment;
import com.prodapt.learningspring.entity.Post;

 

public interface CommentCRUDRepository extends CrudRepository<Comment,Integer>{

    List<Comment> findAllByPost(Post post);

}

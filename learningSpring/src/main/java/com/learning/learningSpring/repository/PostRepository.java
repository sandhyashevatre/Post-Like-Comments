package com.learning.learningSpring.repository;

import org.springframework.data.repository.CrudRepository;

import com.learning.learningSpring.entity.Post;

public interface PostRepository extends CrudRepository<Post, Integer> {

}

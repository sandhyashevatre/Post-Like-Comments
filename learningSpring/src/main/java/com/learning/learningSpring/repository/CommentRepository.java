package com.learning.learningSpring.repository;

import com.learning.learningSpring.entity.Comment;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
	@Query(value = "select * from comment c where post_id = ?1", nativeQuery= true)
	List<Comment> findAllByPostId(Integer postId);

	@Modifying
    @Query("UPDATE Comment c SET c.likes = :likes, c.likedByUser = :likedByUser WHERE c.id = :commentId")
    void updateCommentLikeStatus(Integer commentId, int likes, boolean likedByUser);
}

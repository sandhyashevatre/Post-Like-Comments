package com.prodapt.learningspring.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.prodapt.learningspring.entity.Comment;
import com.prodapt.learningspring.entity.LikeCommentId;
import com.prodapt.learningspring.entity.LikeCommentRecord;
import com.prodapt.learningspring.entity.Post;
import com.prodapt.learningspring.entity.User;

public interface CommentLikeCRUDRepository extends CrudRepository<LikeCommentRecord,LikeCommentId>{
    public Integer countByLikeCommentIdComment(Comment comment);
    
    // public User findLikedUserByComment(Comment comment);
}

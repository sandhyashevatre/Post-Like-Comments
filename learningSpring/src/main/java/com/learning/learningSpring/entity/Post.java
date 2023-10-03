package com.learning.learningSpring.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Post")
@Data
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String content;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "author_id", referencedColumnName = "id")
	private User author;



	@ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "postId", referencedColumnName = "id")
    private Post post;

	

    private int likes;

    private boolean likedByUser;

    @CreationTimestamp
    @Column(name = "timestamp")
    private Date timestamp;

    @UpdateTimestamp
    private Date updatedAt;


    
}
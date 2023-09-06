// package com.prodapt.learningspring.entity;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Column;
// import jakarta.persistence.Embedded;
// import jakarta.persistence.EmbeddedId;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.Data;

// @Entity
// @Data
// @Table(name="Comments")
// public class Comment {
	
// 	@Column(unique=true)
// 	@GeneratedValue(strategy=GenerationType.IDENTITY)
// 	private int id;
	
// 	private String content;
	  
// 	@ManyToOne(cascade = CascadeType.ALL)
// 	@JoinColumn(name = "author_id", referencedColumnName = "id")
// 	private User author;
	
// 	@ManyToOne(cascade = CascadeType.ALL)
// 	@JoinColumn(name = "post_id", referencedColumnName = "id")
// 	private Post post;
	
// }

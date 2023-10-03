package com.prodapt.learningspring.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Post {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private int id;
  
  private String content;
  
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "author_id", referencedColumnName = "id")
  private User author;

  public boolean isPresent() {
    return false;
  }
}

package com.prodapt.learningspring.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.prodapt.learningspring.business.LoggedInUser;
import com.prodapt.learningspring.business.NeedsAuth;
import com.prodapt.learningspring.controller.binding.AddPostForm;
import com.prodapt.learningspring.controller.exception.ResourceNotFoundException;
import com.prodapt.learningspring.entity.Comment;
import com.prodapt.learningspring.entity.LikeCommentId;
import com.prodapt.learningspring.entity.LikeCommentRecord;
import com.prodapt.learningspring.entity.LikeId;
import com.prodapt.learningspring.entity.LikeRecord;
import com.prodapt.learningspring.entity.Post;
import com.prodapt.learningspring.entity.User;
import com.prodapt.learningspring.repository.CommentCRUDRepository;
import com.prodapt.learningspring.repository.CommentLikeCRUDRepository;
import com.prodapt.learningspring.repository.LikeCRUDRepository;
import com.prodapt.learningspring.repository.PostRepository;
import com.prodapt.learningspring.service.UserService;

import jakarta.servlet.ServletException;

@Controller

@RequestMapping("/forum")

public class ForumController {

  @Autowired

  private PostRepository postRepository;

  @Autowired

  private LoggedInUser loggedInUser;

  @Autowired

  private CommentCRUDRepository commentCRUDRepository;

  @Autowired

  private LikeCRUDRepository likeCRUDRepository;

  @Autowired
  private CommentLikeCRUDRepository commentLikeCRUDRepository;

  @Autowired

  UserService userService;

  @NeedsAuth(loginPage = "/loginpage")

  @GetMapping("/post/form")

  public String getPostForm(Model model) {

    model.addAttribute("postForm", new AddPostForm());

    return "forum/postForm";

  }

  @PostMapping("/post/add")

  public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
      RedirectAttributes attr) throws ServletException {

    if (bindingResult.hasErrors()) {

      System.out.println(bindingResult.getFieldErrors());

      attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);

      attr.addFlashAttribute("post", postForm);

      return "redirect:/forum/post/form";

    }

    var user = this.loggedInUser.getLoggedInUser();

    if (user == null) {

      return "redirect:/loginpage";

    }

    var userId = user.getId();

    var freshUser = userService.findById(userId);

    Post post = new Post();

    post.setAuthor(freshUser.get());

    post.setContent(postForm.getContent());

    post.setCreatedDate(LocalDateTime.now());

    post.setUpdatedDate(LocalDateTime.now());

    postRepository.save(post);

    return String.format("redirect:/forum/post/%d", post.getId());

  }

  @GetMapping("/post/{id}")
  public String postDetail(@PathVariable int id, Model model, Principal principal) throws ResourceNotFoundException {
    
    Optional<Post> post = postRepository.findById(id);

    if (post.isEmpty()) {
      throw new ResourceNotFoundException("No post with the requested ID");
    }

    model.addAttribute("post", post.get());

    int numLikes = likeCRUDRepository.countByLikeIdPost(post.get());
    List<LikeRecord> allLikers = likeCRUDRepository.findByLikeIdPost(post.get());
    List<Comment> allCommentRecords = commentCRUDRepository.findAllByPost(post.get());
    List<Integer> numLikeCommentList = new ArrayList<>();
    List<Boolean> islikedList = new ArrayList<>();
    allCommentRecords.stream()
        .forEach(comment -> numLikeCommentList.add(commentLikeCRUDRepository.countByLikeCommentIdComment(comment)));
        
    // allCommentRecords.stream().forEach(comment -> {
    //   User user = commentLikeCRUDRepository.findLikedUserByComment(comment);
    //   if (principal != null) {
    //     String loggedInUser = principal.getName();
    //     islikedList.add(user.getName().equals(loggedInUser));
    //   } else {
    //     islikedList.add(false);
    //   }
    // });
    System.out.println(numLikeCommentList);
      
    List<String> likerNames = allLikers.stream().map(likeRecord -> likeRecord.getLikeId().getUser().getName())
        .collect(Collectors.toList());
    model.addAttribute("isLikedList", islikedList);
    model.addAttribute("numLikesComment", numLikeCommentList);
    model.addAttribute("AllComments", allCommentRecords);
    model.addAttribute("likeCount", numLikes);
    model.addAttribute("allLikers", likerNames);
    

  

// Existing code for adding attributes to the model


    return "forum/postDetail";
  }


  @PostMapping("/post/{id}/like")

  public String postLike(@PathVariable int id, String Comment, RedirectAttributes attr) {

    var post = postRepository.findById(id);

    var user = this.loggedInUser.getLoggedInUser();

    if (user == null) {

      return "redirect:/loginpage";

    }

    var freshUser = userService.findById(user.getId());

    
    LikeId likeId = new LikeId();

    likeId.setUser(freshUser.get());

    likeId.setPost(post.get());

    LikeRecord like = new LikeRecord();

    like.setLikeId(likeId);

    likeCRUDRepository.save(like);

    postRepository.save(post.get());

    return String.format("redirect:/forum/post/%d", id);

  }

  @PostMapping("/post/{id}/comment")

  public String postComment(@PathVariable int id, String Comment, RedirectAttributes attr) {

    var post = postRepository.findById(id);

    var user = this.loggedInUser.getLoggedInUser();

    if (user == null) {

      return "redirect:/loginpage";

    }

    var freshUser = userService.findById(user.getId());

    Comment commentRecord = new Comment();

    if (!Comment.equals(null) && !Comment.equals("")) {

      commentRecord.setPost(post.get());

      commentRecord.setUser(freshUser.get());

      commentRecord.setContent(Comment);

      commentRecord.setCreatedDate(LocalDateTime.now());

      commentCRUDRepository.save(commentRecord);

    }

    

    postRepository.save(post.get());

    return String.format("redirect:/forum/post/%d", id);

  }

  @PostMapping("/comment/{commentId}/like/{id}")
  public String likeComment(@PathVariable int commentId, Model model, @PathVariable int id, RedirectAttributes attr,
      String likebutton) {
    var comment = commentCRUDRepository.findById(commentId);
    var user = this.loggedInUser.getLoggedInUser();

    if (user == null) {
      return "redirect:/loginpage";
    }

    var freshUser = userService.findById(user.getId());
    if (comment.isPresent()) {
      Comment commentRecord = comment.get();

      LikeCommentId likeCommentId = new LikeCommentId();
      likeCommentId.setUser(freshUser.get());
      likeCommentId.setComment(commentRecord);

      LikeCommentRecord likeComment = new LikeCommentRecord();
      likeComment.setLikeCommentId(likeCommentId);
      commentLikeCRUDRepository.save(likeComment);
    }
    return String.format("redirect:/forum/post/%d", id);
  }

  @PostMapping("/comment/{commentId}/unlike/{id}")
  public String unlikeComment(@PathVariable int commentId, Model model, @PathVariable int id, RedirectAttributes attr,
      String likebutton) {
    var comment = commentCRUDRepository.findById(commentId);
    var user = this.loggedInUser.getLoggedInUser();

    if (user == null) {
      return "redirect:/loginpage";
    }

    var freshUser = userService.findById(user.getId());
    if (comment.isPresent()) {
      Comment commentRecord = comment.get();

      LikeCommentId likeCommentId = new LikeCommentId();
      likeCommentId.setUser(freshUser.get());
      likeCommentId.setComment(commentRecord);

      LikeCommentRecord likeComment = new LikeCommentRecord();
      likeComment.setLikeCommentId(likeCommentId);
      commentLikeCRUDRepository.delete(likeComment);
    }
    return String.format("redirect:/forum/post/%d", id);
  }
}

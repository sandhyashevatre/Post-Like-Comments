package com.prodapt.learningspring.controller;

import java.util.Optional;

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
import com.prodapt.learningspring.entity.User;
import com.prodapt.learningspring.business.LoggedInUser;
import com.prodapt.learningspring.business.NeedsAuth;
import com.prodapt.learningspring.controller.binding.AddPostForm;
import com.prodapt.learningspring.controller.exception.ResourceNotFoundException;
import com.prodapt.learningspring.entity.Post;
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
  private UserService userService;

 

  @NeedsAuth(loginPage = "/loginpage")

  @GetMapping("/post/form")

  public String getPostForm(Model model) {

    model.addAttribute("postForm", new AddPostForm());

    return "forum/postForm";

  }
 

  @PostMapping("/post/add")

  public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult, RedirectAttributes attr) throws ServletException {

    if (bindingResult.hasErrors()) {

      System.out.println(bindingResult.getFieldErrors());

      attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);

      attr.addFlashAttribute("post", postForm);

      return "redirect:/forum/post/form";

    }

    var user = this.loggedInUser.getLoggedInUser();

    if (user==null) {

        System.out.println("hell messed up");

    }
    var userId = user.getId();

    var freshUser = userService.findById(userId);

    Post post = new Post();

    post.setAuthor(freshUser.get());

    post.setLikeids(String.valueOf(user.getId()));

    post.setContent(postForm.getContent());

    postRepository.save(post);

    return String.format("redirect:/forum/post/%d", post.getId());

  }

 

  @GetMapping("/post/{id}")

  public String postDetail(@PathVariable int id, Model model) throws ResourceNotFoundException {

    Optional<Post> post = postRepository.findById(id);

    if (post.isEmpty()) {

      throw new ResourceNotFoundException("No post with the requested ID");

    }

    model.addAttribute("post", post.get());

    int numLikes = post.get().getLikeids().split(",").length;

    model.addAttribute("likeCount", numLikes);

    return "forum/postDetail";

  }

 

  @PostMapping("/post/{id}/like")

  public String postLike(@PathVariable int id, RedirectAttributes attr) {

    var post = postRepository.findById(id);

    var user = this.loggedInUser.getLoggedInUser();

    if(user==null){

      System.out.println("hell messed up");

    }

    if(!post.get().getLikeids().contains(String.valueOf(user.getId())))

      post.get().setLikeids(post.get().getLikeids().concat(","+user.getId()));

    postRepository.save(post.get());

    return String.format("redirect:/forum/post/%d", id);

  }

 

}

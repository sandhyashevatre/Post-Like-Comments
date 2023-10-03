package com.learning.learningSpring.controller.poststats;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.learningSpring.controller.binding.AddCommentForm;
import com.learning.learningSpring.controller.binding.AddPostForm;
import com.learning.learningSpring.controller.exceptions.ResourceNotFoundException;
import com.learning.learningSpring.entity.Comment;
import com.learning.learningSpring.entity.LikeId;
import com.learning.learningSpring.entity.LikeRecord;
import com.learning.learningSpring.entity.Post;
import com.learning.learningSpring.entity.User;
import com.learning.learningSpring.model.RegistrationForm;
import com.learning.learningSpring.repository.CommentRepository;
import com.learning.learningSpring.repository.LikeCRUDRepository;
import com.learning.learningSpring.repository.PostRepository;
import com.learning.learningSpring.repository.UserRepository;
import com.learning.learningSpring.service.DomainUserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;

@Controller
@RequestMapping("/forum")
public class ForumController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private DomainUserService domainUserService;

	@Autowired
	private LikeCRUDRepository likeCRUDRepository;

	@Autowired
	private CommentRepository commentRepository;

	@PostConstruct
	public void init() {
	}

	@GetMapping("/post/form")
	public String getPostForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		AddPostForm postForm = new AddPostForm();
		User author = domainUserService.getByName(userDetails.getUsername()).get();
		postForm.setUserId(author.getId());
		model.addAttribute("postForm", postForm);
		return "forum/postForm";
	}

	@PostMapping("/post/add")
	public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
			RedirectAttributes attr) throws ServletException, ParseException {
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult.getFieldErrors());
			attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);
			attr.addFlashAttribute("post", postForm);
			return "redirect:/forum/post/form";
		}

		Optional<User> user = userRepository.findById(postForm.getUserId());
		if (user.isEmpty()) {
			throw new ServletException("Something went seriously wrong, and we couldn't find the user in the DB");
		}

		Post post = new Post();
		post.setAuthor(user.get());
		post.setContent(postForm.getContent());
		postRepository.save(post);
		return String.format("redirect:/forum/post/%d", post.getId());
	}

	@GetMapping("/post/{id}")
	public String postDetail(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails)
			throws ResourceNotFoundException {
		Optional<Post> post = postRepository.findById(id);
		if (post.isEmpty()) {
			throw new ResourceNotFoundException("No post with the requested ID");
		}
		model.addAttribute("post", post.get());

		List<Comment> commentList = commentRepository.findAllByPostId(id);
		model.addAttribute("commentList", commentList);

		model.addAttribute("likerName", userDetails.getUsername());
		int numLikes = likeCRUDRepository.countByLikeIdPost(post.get());
		model.addAttribute("likeCount", numLikes);

		model.addAttribute("commentForm", new AddCommentForm());
		return "forum/postDetail";
	}

	

	


	@PostMapping("/post/{id}/comment")
	public String addCommentToPost(@ModelAttribute("commentForm") AddCommentForm commentForm, @PathVariable int id,
			@AuthenticationPrincipal UserDetails userDetails) {
		Optional<Post> post = postRepository.findById(id);
		if (post.isPresent()) {
			Comment comment = new Comment();
			comment.setContent(commentForm.getContent());
			comment.setPost(post.get());
			comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
			commentRepository.save(comment);
		}
		return String.format("redirect:/forum/post/%d", id);
	}

	@PostMapping("/post/{postId}/like-unlike-comment/{commentId}")
	public String likeUnlikeComment(@PathVariable("postId") int postId,
			@PathVariable("commentId") int commentId,
			@RequestParam("likedByUser") boolean likedByUser) {
		// Retrieve the comment by its ID
		Optional<Comment> commentOptional = commentRepository.findById(commentId);

		if (commentOptional.isPresent()) {
			Comment comment = commentOptional.get();

			// Update the likedByUser field
			comment.setLikedByUser(!likedByUser);

			// Update the likes count based on likedByUser
			int likes = comment.getLikes();
			if (!likedByUser) {
				likes++;
			} else {
				likes--;
			}
			comment.setLikes(likes);

			// Save the updated comment
			commentRepository.save(comment);

			// Redirect back to the post detail page
			return "redirect:/forum/post/" + postId;
		} else {
			// Handle the case where the comment with the given ID is not found
			return "redirect:/forum"; // or wherever you want to redirect
		}
	}

	@PostMapping("/post/{postId}/like-unlike-post")
public String likeUnlikePost(@PathVariable("postId") int postId,
                             @RequestParam("likedByUser") boolean likedByUser) {
    // Retrieve the post by its ID
    Optional<Post> postOptional = postRepository.findById(postId);

    if (postOptional.isPresent()) {
        Post post = postOptional.get();

        // Update the likedByUser field
        post.setLikedByUser(!likedByUser);

        // Update the likes count based on likedByUser
        int likes = post.getLikes();
        if (!likedByUser) {
            likes++;
        } else {
            likes--;
        }
        post.setLikes(likes);

        // Save the updated post
        postRepository.save(post);

        // Redirect back to the post detail page
        return "redirect:/forum/post/" + postId;
    } else {
        // Handle the case where the post with the given ID is not found
        return "redirect:/forum"; // or wherever you want to redirect
    }
}

	@GetMapping("/register")
	public String getRegistrationForm(Model model) {
		if (!model.containsAttribute("registrationForm")) {
			model.addAttribute("registrationForm", new RegistrationForm());
		}
		return "forum/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("registrationForm") RegistrationForm registrationForm,
			BindingResult bindingResult,
			RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
			attr.addFlashAttribute("registrationForm", registrationForm);
			return "redirect:/register";
		}
		if (!registrationForm.isValid()) {
			attr.addFlashAttribute("message", "Passwords must match");
			attr.addFlashAttribute("registrationForm", registrationForm);
			return "redirect:/register";
		}
		domainUserService.save(registrationForm.getUsername(), registrationForm.getPassword());
		attr.addFlashAttribute("result", "Registration success!");
		return "redirect:/login";
	}

}

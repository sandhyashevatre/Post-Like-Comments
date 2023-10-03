package com.learning.learningSpring.controller.binding;

import lombok.Data;

@Data
public class AddPostForm {
	private String content;
	private int userId;
}

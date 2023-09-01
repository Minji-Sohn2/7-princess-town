package com.example.princesstown.controller.comment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommentViewController {
    @GetMapping("/view/posts/1/comment")
    public String ViewComment() {
        return "postDetails";
    }
}

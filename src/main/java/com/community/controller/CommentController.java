package com.community.controller;

import com.community.entity.Comment;
import com.community.service.CommentService;
import com.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {

        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;


    }
}

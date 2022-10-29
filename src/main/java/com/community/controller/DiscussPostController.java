package com.community.controller;


import com.community.entity.Comment;
import com.community.entity.DiscussPost;
import com.community.entity.Page;
import com.community.entity.User;
import com.community.service.CommentService;
import com.community.service.DiscussPostService;
import com.community.service.UserService;
import com.community.util.CommunityConstant;
import com.community.util.CommunityUtil;
import com.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {


    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "还没有登录哦~");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错的情况，后面统一处理
        return CommunityUtil.getJSONString(0, "发布成功哦~");
    }

    @GetMapping("/detail/{idscussPostId}")
    public String findDiscussPostDetails(@PathVariable("idscussPostId") int discussPostId, Model model, Page page) {
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostDetails(discussPostId);
        model.addAttribute("post", post);
        //查询帖子作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //查询评论分页信息
        page.setLimit(5);
        page.setPath("?discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论:给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论Vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //评论Vo
                HashMap<String, Object> commentVo = new HashMap<>();
                //获得评论
                commentVo.put("comment", comment);
                //获得作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyVoList != null) {
                    for (Comment reply : commentList) {
                        //回复Vo
                        HashMap<String, Object> replyVo = new HashMap<>();
                        //获得回复评论
                        commentVo.put("reply", reply);
                        //获得回复作者
                        commentVo.put("replyuser", userService.findUserById(reply.getUserId()));

                        //回复的目标评论
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);

                    }
                }
                commentVo.put("replys", replyVoList);
                //查询回复数量
                int replyCount = commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}

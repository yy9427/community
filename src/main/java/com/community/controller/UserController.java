package com.community.controller;

import com.community.annotataion.LoginRequired;
import com.community.entity.User;
import com.community.service.UserService;
import com.community.util.CommunityUtil;
import com.community.util.HostHolder;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //跳转到用户账号设置界面
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }


    //跳转到用户登录后才出现的消息-朋友私信界面
    @LoginRequired
    @GetMapping("/letter")
    public String getLetterPage() {
        return "/site/letter";
    }

    //跳转到用户登录后才出现的消息-系统通知界面
    @LoginRequired
    @GetMapping("/notice")
    public String getNoticePage() {
        return "/site/notice";
    }

    //跳转到用户个人主页-个人信息界面
    @LoginRequired
    @GetMapping("/profile")
    public String getProfilePage() {
        return "/site/profile";
    }

    //跳转到用户登录后才出现的消息-我的帖子界面
    @LoginRequired
    @GetMapping("/my-post")
    public String getMyPostPage() {
        return "/site/my-post";
    }

    //跳转到用户登录后才出现的消息-我的回复界面
    @LoginRequired
    @GetMapping("/my-reply")
    public String getMyReplyPage() {
        return "/site/my-reply";
    }


    @PostMapping("/upload")
    //判断是否选择了文件，若没有选择，则返回原界面
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "还没有选择图片");
            return "/site/setting";
        }
        //判断选择的文件是否有后缀，若没有后缀，则返回原界面
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isNullOrEmpty(suffix)) {
            model.addAttribute("error", "图片格式不正确");
            return "/site/setting";
        }
        //为文件生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败", e);
        }
        //更新当前用户的头像的路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header" + filename;
        userService.updateHeader(user.getId(), headUrl);

        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        //图片在服务器存放路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("/image" + suffix);
        try (FileInputStream fis = new FileInputStream(fileName); OutputStream os = response.getOutputStream();) {

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }

        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //修改用户密码
    @LoginRequired
    @PostMapping("/setting")
    public String updatePwd(Model model, String password, String newPassword, String confirmPassword) {
        if (StringUtils.isNullOrEmpty(password)) {
            model.addAttribute("passwordMsg", "请输入原始密码！");
            return "/site/setting";
        }
        if (StringUtils.isNullOrEmpty(newPassword)) {
            model.addAttribute("newPasswordMsg", "请输入新密码！");
            return "/site/setting";
        }
        if (StringUtils.isNullOrEmpty(confirmPassword)) {
            model.addAttribute("confirmPasswordMsg", "请再次输入新密码！");
            return "/site/setting";
        }
        if (!confirmPassword.equals(newPassword)) {
            model.addAttribute("newPasswordMsg", "两次输入的新密码不相同！");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(password, newPassword, user.getId());
        if (map == null || map.isEmpty()) {
            //传给templates注册成功信息
            model.addAttribute("msg", "密码修改成功");
            //跳到回个人设置页面
            model.addAttribute("target", "/user/setting");
            return "/site/operate-result";
        } else {
            //失败了传失败信息，跳到到原来的页面
            model.addAttribute("passwordMsg", "输入的原始密码错误！");
            return "/site/setting";
        }
    }
}

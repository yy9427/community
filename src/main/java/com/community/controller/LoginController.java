package com.community.controller;

import com.community.entity.User;
import com.community.service.UserService;
import com.community.util.CommunityConstant;
import com.google.code.kaptcha.Producer;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }


    //注册新用户
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，激活邮件已发送");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    //激活注册的账户
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {

        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，重复激活！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //生成验证码
    @GetMapping("/kaptcha")
    public void getKpatcha(HttpServletResponse response, HttpSession session) {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha", text);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme, Model model, HttpSession session, HttpServletResponse response) {
        //检查验证码是否为空和是否填写正确
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isNullOrEmpty(kaptcha) || StringUtils.isNullOrEmpty(code) || code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }
        //检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }

    }

    //用户退出登录
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";

    }
}

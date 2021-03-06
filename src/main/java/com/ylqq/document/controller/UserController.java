package com.ylqq.document.controller;

import com.ylqq.document.pojo.Document;
import com.ylqq.document.pojo.User;
import com.ylqq.document.service.DocumentRepository;
import com.ylqq.document.service.UserRepository;
import com.ylqq.document.service.impl.UserServiceImpl;
import com.ylqq.document.util.Layui;
import com.ylqq.document.util.MD5Util;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * @author ylqq
 */
@Controller
public class UserController {

    private static final String USER = "user";
    /**
     * 用户Service
     */
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final HttpSession session;

    public UserController(UserServiceImpl userService, UserRepository userRepository, DocumentRepository documentRepository, HttpSession session) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.session = session;
    }

    @RequestMapping("/home")
    public String home() {
        if (session.getAttribute(USER) == null) {
            return "redirect:toLogin";
        } else {
            return "user/home";
        }
    }

    @RequestMapping("/toModifyUser")
    public String totoModifyUser() {
        return "/user/home";
    }

    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }

    @RequestMapping("/toAllUser")
    public String toAllUser() {
        if (session.getAttribute(USER) == null) {
            return "redirect:toLogin";
        } else {
            return "sysManager/user/userList";
        }
    }

    /**
     * 注册新用户
     */
    @RequestMapping("/addUser")
    public String addUser(User user, Model model) {
        user.setUserStatus(0);
        String md5 = MD5Util.getMD5(user.getPassword());
        user.setPassword(md5);
        try {
            if (!userRepository.existsByUserid(user.getUserid()) && userRepository.findByLoginName(user.getLoginName()) == null) {
                userRepository.insert(user);
            } else {
                model.addAttribute("error", "用户id或者loginName已存在！");
            }
            return "login";
        } catch (Exception exception) {
            exception.printStackTrace();
            return "login";
        }
    }

    /**
     * 修改个人资料
     */
    @RequestMapping("/modifyUser/{userid}")
    public String modifyUser(User user, @PathVariable Integer userid) {
        user.setUserid(userid);
        try {
            userService.updateById(user);
            for (Document document : documentRepository.findAll()) {
//                for (User documentReceiver : document.getReceivers()) {
//                    if (documentReceiver.getUserid().equals(userid)) {
//                        documentReceiver.setEmail(user.getEmail());
//                        documentReceiver.setPhone(user.getPhone());
//                        documentReceiver.setUserName(user.getUserName());
//                    }
//                }
            }
            return "user/home";
        } catch (Exception exception) {
            exception.printStackTrace();
            return "/user/home";
        }
    }

    /**
     * 修改密码
     */
    @RequestMapping("/modifyPassword")
    public String modifyPassword(String newPassword, @org.jetbrains.annotations.NotNull String oldPassword) {
        User sessionUser = (User) session.getAttribute("user");
        //从数据库中取出用户信息,注意，取出来的密码是加密后的
        Optional<User> user = userRepository.findByUserid(sessionUser.getUserid());
        String md5pass = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        //如果两者加密后相等，即输入的密码正确
        if (user.isPresent()) {
            if (Integer.parseInt(md5pass) == user.get().getUserid()) {
                Update update = new Update().set("password", DigestUtils.md5DigestAsHex(newPassword.getBytes()));
                Query query = Query.query(Criteria.where("userid").is(user.get().getUserid()));
                userService.updatePassword(update, query);
            }
        }
        return "redirect:toLogin";
    }

    @RequestMapping("allUser")
    @ResponseBody
    public Layui allUser() {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Layui.data("用户未登录或登录信息失效", 0, null);
        } else {
            return Layui.data("", Math.toIntExact(userRepository.count()), userRepository.findAll());
        }
    }

    @RequestMapping("deleteUser/{userid}")
    public String deleteUser(@PathVariable Integer userid) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            userRepository.deleteByUserid(userid);
            //TODO 前端收到信号后刷新页面，硬删除用户
            return "location.replace(location.href)";
        } else {
            return "redirect:toLogin";
        }
    }

    @RequestMapping("findUser/{userId}")
    @ResponseBody
    public User findUser(@PathVariable Integer userId) {
        if (userRepository.findByUserid(userId).isPresent()) {
            return userRepository.findByUserid(userId).get();
        }
        return null;
    }
}

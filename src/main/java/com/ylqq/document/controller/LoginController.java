package com.ylqq.document.controller;

import com.ylqq.document.pojo.User;
import com.ylqq.document.service.UserRepository;
import com.ylqq.document.service.impl.DocumentServiceImpl;
import com.ylqq.document.util.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ylqq
 */
@Controller
public class LoginController {
    private final UserRepository userRepository;

    private final DocumentServiceImpl documentService;

    public LoginController(UserRepository userRepository, DocumentServiceImpl documentService) {
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    @RequestMapping({"/","/toLogin"})
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/register")
    public String toRegister(){
        return "register";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    /**
     * 用户注销
     *
     * @param session
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session) {

        //移除user属性
        session.removeAttribute("user");

        //注销Session
        session.invalidate();

        //返回登录界面
        return "redirect:/toLogin";
    }


    /**
     * 用户登陆
     *
     * @param session   存取用户信息
     * @param loginName 提交的登录名
     * @param password  提交的密码
     * @param model       保存结果集
     * @return
     */
    @RequestMapping("login")
    public String userLogin(HttpSession session,String loginName, String password, Model model) {
        //1.首先检查登录名、密码和验证码用户是否都填写了，如果有一样没填写就直接打回

        if (!StringUtils.hasText(loginName) || !StringUtils.hasText(password)) {

            //1.1 告诉用户登陆失败，这三个字段都是必填项
            model.addAttribute("msg", "登录名、密码都是必填项！");
            model.addAttribute("result", false);

            //1.2 直接跳回登录界面
            return "index";
        }

        //检查用户输入的账号是否正确

        //去数据库查询用户名和密码
        String md5pass=MD5Util.getMD5(password);
        User user=userRepository.findByLoginName(loginName);
        //检查能不能找到
        if (user!=null&&user.getPassword().equals(md5pass)) {
            session.setAttribute("user", user);
            return "redirect:/toIndex";
        } else {
            model.addAttribute("msg", "登录名或密码错误！");
            model.addAttribute("result", false);
            return "redirect:/toLogin";
        }
    }

    @RequestMapping("/toIndex")
    public String toIndex() {
        return "index";
    }

    /**
     * 访问欢迎页
     */
    @RequestMapping("/toWelcome")
    public String toWelcome(Map<String, Object> map, HttpSession session) {

        //1.从Session中取出用户信息，并得到用户id和角色id
        User user = (User) session.getAttribute("user");
        Integer userId = user.getUserid();
        Integer roleid = user.getRoleId();

        //2.找出要统计的4个数字

        //2.1 找出待处理公文数量
        Long dealcount = null;
        if (roleid == 1 || roleid == 2) {
            dealcount = documentService.selectMyDealCount(userId);
        }

        //2.2 找出审核驳回公文数量
        Long failcount = documentService.selectMyFailCount(userId);

        //2.3 找出待接收公文数量
        Long receivecount = documentService.selectMyCountReceiver(userId);

        //2.4 找出等待审核通过公文数量
        Long waitcount = documentService.selectMyWaitingCount(userId);

        //3 保存查询结果
        map.put("dealcount", dealcount);
        map.put("failcount", failcount);
        map.put("receivecount", receivecount);
        map.put("waitcount", waitcount);

        //4.返回首页
        return "/user/home";
    }


}

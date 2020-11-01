package com.ylqq.document.controller;

import com.ylqq.document.pojo.Document;
import com.ylqq.document.pojo.User;
import com.ylqq.document.service.DocumentRepository;
import com.ylqq.document.service.InstitutionRepository;
import com.ylqq.document.service.impl.DocumentServiceImpl;
import com.ylqq.document.util.Layui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author ylqq
 */
@Controller
public class DocumentController {
    @Autowired
    private HttpSession session;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private DocumentServiceImpl documentService;

    @RequestMapping("toAddDoc")
    public String toAddDoc() {
        if (session.getAttribute("user") == null) {
            return "redirect:toLogin";
        } else {
            return "doc/addArticle";
        }
    }

    @RequestMapping("todocReceive")
    public String todocReceive() {
        if (session.getAttribute("user") == null) {
            return "redirect:toLogin";
        } else {
            return "doc/docReceive";
        }
    }

    @RequestMapping("todocAuditOfMe")
    public String todocOfMe() {
        if (session.getAttribute("user") == null) {
            return "redirect:toLogin";
        } else {
            return "doc/docAuditOfMe";
        }
    }

    @RequestMapping("todocWriteByMe")
    public String todocWriteByMe() {
        if (session.getAttribute("user") == null) {
            return "redirect:toLogin";
        } else {
            return "doc/docWriteByMe";
        }
    }

    @PostMapping("addDoc")
    public String addDocument(Document document, HttpSession httpSession, Model model) {
        //先判断编号是否可用
        //先取到user,表格不能填完所有doc数据
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            model.addAttribute("msg", "请先登陆系统！");
            return "redirect:/toLogin";
        }
        if (documentRepository.existsById(document.getDocumentId())) {
            model.addAttribute("repeat_error", "编号重复");
            return "doc/addArticle";
        } else {
            document.setPublishTime(new Date());
            document.setWriterId(user.getUserid());
            document.setCopywriter(user);
            document.setInstId(user.getInstId());
            if (institutionRepository.findById(user.getInstId()).isPresent()) {
                document.setInstitution(institutionRepository.findById(user.getInstId()).get());
            } else {
                model.addAttribute("noInst_error", "无此机构！");
                return "doc/addArticle";
            }
            /*设置公文状态为审核中*/
            document.setArticleStatus(0);
            /*
             * 记得要在表格中增加接收者*/

            documentRepository.insert(document);
            return "forward:/allDoc";
        }
    }

    @RequestMapping("deleteDoc")
    public String deleteDocument(Integer docId) {
        documentRepository.deleteById(docId);
        return "docAuditOfMe";
    }

    @RequestMapping("allDoc")
    public Layui findAllDoc() {
        return Layui.data("", (int) documentRepository.count(), documentRepository.findAll());
    }

    @RequestMapping("updateDoc")
    public String updateDoc(Document document) {
        documentService.updateByPrimaryKeySelective(document);
        return "doc/docAuditOfMe";
    }

    @RequestMapping("toUpdateDoc/{docId}")
    public String toUpdateDoc(@PathVariable Integer docId) {
        return "/doc/docModify" + docId;
    }

    @RequestMapping("docAuditOfMe")
    public Layui docOfMe(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Layui.data("用户未登录或登录信息失效", 0, null);
        } else {
            return Layui.data("", documentRepository.countByAuditorId(user.getUserid()), documentRepository.findDocumentsByAuditorIdOrderByPublishTime(user.getUserid()));
        }
    }

    @RequestMapping("docWriteByMe")
    @ResponseBody
    public Layui docWriteByMe() {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Layui.data("用户未登录或登录信息失效", 0, null);
        } else {
            return Layui.data("", documentRepository.countByWriterId(user.getUserid()), documentRepository.findDocumentsByWriterIdOrderByPublishTime(user.getUserid()));
        }
    }

    @RequestMapping("docReceive")
    public Layui docReceive() {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Layui.data("用户未登录或登录信息失效", 0, null);
        } else {
            return Layui.data("", Math.toIntExact(documentService.selectMyDealCount(user.getUserid())), documentService.selectMyReceiveList(user.getUserid()));
        }
    }

}

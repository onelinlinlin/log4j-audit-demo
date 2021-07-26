package com.lzp.controller;

import com.lzp.RequestContext;
import com.lzp.entity.User;
import com.lzp.service.TransferService;
import com.lzp.service.UserService;
import org.apache.logging.log4j.audit.LogEventFactory;
import org.apache.logging.log4j.audit.event.Deposit;
import org.apache.logging.log4j.audit.event.Login;
import org.apache.logging.log4j.audit.event.Transfer;
import org.apache.logging.log4j.core.util.NetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Controller
@RequestMapping(value = "/bank")
public class BankController {

    @Autowired
    private UserService userService;
    @Autowired
    private TransferService transferService;


    // 登陆界面
    @RequestMapping(value = "/login")
    public String login(){
        return "userlogin";
    }

    // 个人界面
    @RequestMapping(value = "/user")
    public String user(HttpSession session, Map<String,Object> result){

        if (session.getAttribute("userid") != null) {
            /*获取用户个人信息*/
            int userid = (int)session.getAttribute("userid");
            User user = userService.finduserByid(userid);
            result.put("user",user);

            return "user";
        } else {
            return "userlogin";
        }
    }

    // 用户存款界面
    @RequestMapping(value = "/userdeposit")
    public String userdeposit(HttpSession session, Map<String,Object> result){

        if (session.getAttribute("userid") != null) {
            /*获取用户个人信息*/
            int userid = (int)session.getAttribute("userid");
            User user = userService.finduserByid(userid);
            result.put("user",user);

            return "deposit";
        } else {
            return "userlogin";
        }
    }

    // 用户转账界面
    @RequestMapping(value = "/usertransfer")
    public String usertransfer(HttpSession session, Map<String,Object> result){

        if (session.getAttribute("userid") != null) {
            /*获取用户个人信息*/
            int userid = (int)session.getAttribute("userid");
            User user = userService.finduserByid(userid);
            result.put("user",user);

            return "transfer";
        } else {
            return "userlogin";
        }
    }


    /*退出登陆操作*/
    @RequestMapping(value = "/logout")
    public String logout(HttpSession session){

        if (session.getAttribute("userid") != null) {
            /*删除会话记录*/
            session.removeAttribute("userid");
            session.removeAttribute("username");
            session.removeAttribute("password");
            return "userlogin";
        } else {
            return "user";
        }
    }


    // 登陆验证
    @RequestMapping(value = "/logincheck")
    @ResponseBody
    public String logincheck(String username, String password,HttpSession session) throws UnknownHostException {

        // 获取主机名
        String hostName = NetUtils.getLocalHostname();
        RequestContext.setHostName(hostName);
        // 获取设备ip地址
        String inetAddress = InetAddress.getLocalHost().getHostAddress();
        RequestContext.setIpAddress(inetAddress);


        /*生成该用户登陆的审计日志*/
        Login login = LogEventFactory.getEvent(Login.class);
        login.setUsername(username);
        login.logEvent();
        String result = login1(username,password,session);
        login.setCompletionStatus(result);
        login.logEvent();

        if (result.equals("Login Success")) {
            return "success";
        } else {
            return "fail";
        }
    }

    /*进行登陆账号验证*/
    private String login1(String username,String password,HttpSession session) {

        User result = userService.checkUser(username,password);

        /*进行登陆结果返回*/
       if (result != null) {
           session.setAttribute("userid",result.getUserid());
           session.setAttribute("username",result.getUsername());
           session.setAttribute("password",result.getPassword());
           return "Login Success";
       } else {
           return "Login Fail";
       }
    }


    /*
    * 用户的存款操作
    * */
    @RequestMapping(value = "/deposit")
    @ResponseBody
    public String deposit(int account,HttpSession session) {

        /*获取当前的账户编号和账户名称*/
        int userid = (int)session.getAttribute("userid");
        String username = session.getAttribute("username").toString();

        /*进行存款事件的日志审计*/
        Deposit deposit = LogEventFactory.getEvent(Deposit.class);
        /*将这些内容添加到审计日志中*/
        deposit.setUserid(userid);
        deposit.setUsername(username);
        deposit.setAmount(account);
        deposit.logEvent();
        String result = deposit1(account,userid,username);
        deposit.setCompletionStatus(result);
        deposit.logEvent();

        if (result.equals("Deposit Success")) {
            return "success";
        } else {
            return "fail";
        }
    }


    /*进行存款操作*/
    private String deposit1(int account,int userid, String username) {

        /*用户存款操作*/
        int result = userService.updateUserAccount(userid,account);

        if (result > 0) {
            return "Deposit Success";
        } else {
            return "Deposit Fail";
        }
    }

    /*
     * 用户的转账操作
     * */
    @RequestMapping(value = "/transfer")
    @ResponseBody
    public String transfer(String toUsername, int account,HttpSession session) {

        /*获取当前的账户编号和账户名称，即转账发起者*/
        int fromUserid = (int)session.getAttribute("userid");
        String fromUsername = session.getAttribute("username").toString();

        // 加载转账Java事件日志接口
        Transfer transfer = LogEventFactory.getEvent(Transfer.class);
        // 添加用户操作信息到审计日志中
        // 转账金额
        transfer.setAmount(account);
        // 转账发起者
        transfer.setFromUser(fromUsername);
        // 转账接收者
        transfer.setToUser(toUsername);
        // 进行事件日志输出
        transfer.logEvent();
        // 事件处理结果
        String result = transfer1(fromUsername,toUsername,account);
        // 完成转账操作后 得到转账事件Transfer的结果
        transfer.setCompletionStatus(result);
        transfer.logEvent();

        if (result.equals("Transfer Success")) {
            return "success";
        } else if (result.equals("Transfer Fail-The transfer amount is wrong")){
            return "failbyamount";
        } else if (result.equals("Transfer Fail toUser does not exist")) {
            return "failbytouser";
        } else {
            return "fail";
        }
    }

    /*进行转账操作*/
    private String transfer1(String fromUsername,String toUsername,int account) {

        // 通过toUsername获得转账发起者的用户实体
        User fromUser = userService.findUserByName(fromUsername);

        if (fromUser.getAccount() >= account) {

            // 通过toUsername获得转账接收者的用户实体
            User toUser = userService.findUserByName(toUsername);

            if (toUser != null) {

                // 更新fromUsername用户的最新余额
                userService.updateUserAccount1(fromUsername,account);

                // 更新toUsername用户的最新余额
                userService.updateUserAccount(toUser.getUserid(),account);

                /*用户转账操作*/
                int result = transferService.addTransferRecord(fromUsername,toUsername,account);

                if (result > 0) {
                    return "Transfer Success";
                } else {
                    return "Transfer Fail";
                }

            } else {
                return "Transfer Fail toUser does not exist";
            }

        } else {
            return "Transfer Fail The transfer amount is wrong";
        }

    }

}

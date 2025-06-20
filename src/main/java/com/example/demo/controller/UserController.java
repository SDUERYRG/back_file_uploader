/*
 * @author 作者姓名
 * @date 日期
 */
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.MD5;
import com.example.demo.utils.Result;
import com.example.demo.utils.ResultCode;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    // @PostMapping("/login")
    // public String login(@RequestBody User user) {
    //     // 这里应添加根据用户名和密码查询数据库的逻辑
    //     // 示例中简单返回登录结果
    //     if ("test".equals(user.getUsername()) && "test".equals(user.getPassword())) {
    //         return "登录成功";
    //     } else {
    //         return "登录失败";
    //     }
    // }

    /**
     * 登录
     * @param user 用户名和密码
     * @return 登录结果
     * @author wyc
     * @date 5/6
     */
    @PostMapping("/login")
    @CrossOrigin
    public Result login(@RequestBody User user, @RequestParam(required = false) HttpServletRequest request) {
        System.out.println("开始登陆，user："+user);
        User currentUser = userService.userAuthentication(user);
        if (currentUser == null)
            return Result.error(ResultCode.USER_LOGIN_ERROR.code(), ResultCode.USER_LOGIN_ERROR.message()); //账号或密码错误
        //对数据库二次加密的密码解密
        String jmPassword = MD5.md5MinusSalt(currentUser.getPassword());
        System.out.println("数据库密码"+jmPassword);
        //前端密码加密
        String password = DigestUtils.md5Hex(user.getPassword());
        System.out.println("前端密码"+password);
        if(!jmPassword.equals(password)){
            //密码错误
            return Result.error(ResultCode.USER_LOGIN_ERROR.code(), ResultCode.USER_LOGIN_ERROR.message()); //账号或密码错误
        }
        Map<String, Object> info = new HashMap<>();
        info.put("username", currentUser.getUsername());
        info.put("password", currentUser.getPassword());
        //生成JWT字符串
        String token = JwtUtil.sign(currentUser.getId().toString(), info);
        if (request != null) {
            request.setAttribute("username",currentUser.getUsername());
        }

        currentUser.setPassword(user.getPassword());
        if (currentUser != null){
            return Result.success(200,token, currentUser);  //登录成功，并返回token
        }
        else
            return Result.error(ResultCode.ERROR.code(), "未知错误,登录失败(≧﹏ ≦)");
    }

    
    @GetMapping("/hello")
    public User hello() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        return userService.userAuthentication(user);
    }

    /**
     * 发送验证码
     * @param email 邮箱地址
     * @return 发送结果
     * @author wyc
     * @date 4/25
     */
    @Transactional
    @CrossOrigin(origins = "*") // 允许的源
    @GetMapping("/sendMail/{email}")
    public Result sendMail(@PathVariable String email) {
        System.out.println("发送给"+email);
        userService.sendMail(email);
        return Result.success(ResultCode.SUCCESS.code(), "验证码已发送至邮箱(。・∀・)ノ");
    }

    /**
     * 验证验证码
     * @param request 请求参数，包含email和code
     * @return 验证结果
     * @author wyc
     * @date 4/25
     */
    @PostMapping("/verifyCode")
    public Result verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        System.out.println("邮箱："+email+"验证码："+code);
        boolean isVerified = userService.verifyCode(email, code);
        if (isVerified) {
            return Result.success(ResultCode.SUCCESS.code(), "验证码验证成功");
        } else {
            return Result.error(ResultCode.CODE_ERROR.code(),ResultCode.CODE_ERROR.message());
        }
    }

    /**
     * 注册
     * @param user 用户信息
     * @return 注册结果
     * @author wyc
     * @date 5/4
     */
    @PostMapping("/userRegister")
    @Transactional
    public Result userRegister(@RequestBody User user) throws Exception {

        //密码MD5加密
        user.setPassword(MD5.md5PlusSalt(user.getPassword()));
        //默认用户名
        System.out.println(user);
        try{
            if (userService.save(user)) {
                return Result.success(ResultCode.SUCCESS.code(), "注册成功(✿◠‿◠)");
            } else {
                return Result.error(ResultCode.USER_REGISTER_ERROR.code(), ResultCode.USER_REGISTER_ERROR.message());
            }
        } catch (Exception e) {
            System.out.println(ResultCode.USER_HAS_EXISTED.code());
            System.out.println(ResultCode.USER_HAS_EXISTED.message());
            return Result.error(ResultCode.USER_HAS_EXISTED.code(), ResultCode.USER_HAS_EXISTED.message());
        }
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/getById/{id}")
    public Result getUserById(@PathVariable("id") Integer id) {
        try {
            System.out.println("===== 获取用户信息 =====");
            System.out.println("用户ID: " + id);

            User user = userService.getById(id);
            if (user == null) {
                System.err.println("用户不存在，ID: " + id);
                return Result.error(404, "用户不存在");
            }

            // 创建安全用户对象，排除敏感信息
            Map<String, Object> safeUser = new HashMap<>();
            safeUser.put("id", user.getId());
            safeUser.put("username", user.getUsername());
            safeUser.put("name", user.getName());
            safeUser.put("avatar", user.getAvatar());

            System.out.println("返回用户信息: " + safeUser);
            return Result.success(200, "获取用户信息成功", safeUser);
        } catch (Exception e) {
            System.err.println("获取用户信息失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }
}
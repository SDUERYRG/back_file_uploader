package com.example.demo.service.imp;

import javax.annotation.Resource;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.CodeUtils;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImp extends ServiceImpl<UserDao, User> implements UserService{
    private final Map<String, Map<String, Object>> codeStore = new HashMap<>();

    @Resource
    private UserDao userDao;

    @Override
    public User userAuthentication(User user){
        return userDao.userAuthentication(user);
    }

    @Resource
    private CodeUtils codeUtils;
    @Resource
    private JavaMailSender javaMailSender;
    @Override
    public void sendMail(String email) {
        // 生成验证码及时间戳
        java.util.Map<String, Object> codeInfo = codeUtils.generateCode(email);
        String code = (String) codeInfo.get("code");
        codeStore.put(email, codeInfo); // 保存验证码信息到存储结构中

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1941456753@qq.com(姚仁广)");
        message.setTo(email);
        message.setSubject("文件上传助手.验证码"); //标题
        message.setText("您的验证码为:" + code + ",有效期为5分钟，此邮件为自动发送，请勿回复");    //正文
        javaMailSender.send(message);
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        // 保存验证码信息到存储结构
        // 从存储结构获取验证码信息
        java.util.Map<String, Object> codeInfo = codeStore.get(email);
        if (codeInfo == null) {
            return false;
        }
        
        String storedCode = (String) codeInfo.get("code");
        System.out.println("storedCode: " + storedCode);
        long timestamp = (long) codeInfo.get("timestamp");
        long currentTime = System.currentTimeMillis();
        long timeDiff = (currentTime - timestamp) / (1000 * 60);

        if (timeDiff > 5) {
            return false; // 验证码已过期
        }

        return storedCode.equals(inputCode);
    }
}

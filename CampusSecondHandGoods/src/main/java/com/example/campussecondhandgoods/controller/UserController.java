package com.example.campussecondhandgoods.controller;

import com.example.campussecondhandgoods.entity.User;
import com.example.campussecondhandgoods.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController   //标记接口层,返回json格式结果
@CrossOrigin
@RequestMapping("/user")  //所有接口的前缀，这里如登录接口为/user/login
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result=new HashMap<>();
        User loginUser=userService.login(user.getUsername(), user.getPassword());
        if(loginUser!=null){
            result.put("code",200);
            result.put("msg","登录成功");
            result.put("data",loginUser);
        }
        else{
            result.put("code",400);
            result.put("msg","用户名或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result=new HashMap<>();
        int count=userService.register(user);
        if(count==-1){
            result.put("code",409);
            result.put("msg","用户名已存在，请更换后重试");
        }
        else if(count>0){
            result.put("code",200);
            result.put("msg","注册成功");
        }
        else{
            result.put("code",400);
            result.put("msg","注册失败");
        }
        return result;
    }

    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam("userId") int userId) {
        Map<String, Object> result=new HashMap<>();
        User userDetail = userService.getUserDetail(userId);
        if (userDetail != null) {
            result.put("code", 200);
            result.put("msg", "查询用户详情成功");
            result.put("data", userDetail);
        } else {
            result.put("code", 404);
            result.put("msg", "该用户不存在");
        }
        return result;
    }

    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            int userId = Integer.parseInt(payload.getOrDefault("userId", "0"));
            String contact = payload.get("contact");
            String avatarUrl = payload.getOrDefault("avatarUrl", "");
            if (userId <= 0) {
                result.put("code", 400);
                result.put("msg", "参数非法");
                return result;
            }
            String normalizedContact = contact == null ? "" : contact.trim();
            String normalizedAvatar = avatarUrl == null ? "" : avatarUrl.trim();
            User updated = userService.updateProfile(userId, normalizedContact, normalizedAvatar);
            if (updated != null) {
                result.put("code", 200);
                result.put("msg", "资料更新成功");
                Map<String, Object> data = new HashMap<>();
                data.put("contact", updated.getContact());
                data.put("avatarUrl", updated.getAvatarUrl());
                result.put("data", data);
            } else {
                result.put("code", 400);
                result.put("msg", "资料更新失败");
            }
        } catch (NumberFormatException e) {
            result.put("code", 400);
            result.put("msg", "用户ID格式错误");
        }
        return result;
    }
}

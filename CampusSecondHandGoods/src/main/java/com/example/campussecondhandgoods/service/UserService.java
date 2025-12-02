package com.example.campussecondhandgoods.service;

import com.example.campussecondhandgoods.entity.User;
import com.example.campussecondhandgoods.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final String DEFAULT_AVATAR = "https://placehold.co/96x96?text=User";
    //自动注入userMapper
    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    public int register(User user) {
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            return -1;
        }
        if (user.getAvatarUrl() == null || user.getAvatarUrl().trim().isEmpty()) {
            user.setAvatarUrl(DEFAULT_AVATAR);
        }
        return userMapper.register(user);
    }

    public User getUserDetail(int id) {return userMapper.getUserDetail(id);}

    public User updateProfile(int id, String contact, String avatarUrl) {
        User user = new User();
        user.setId(id);
        user.setContact(contact == null ? "" : contact);
        String normalizedAvatar = avatarUrl == null || avatarUrl.trim().isEmpty() ? DEFAULT_AVATAR : avatarUrl.trim();
        user.setAvatarUrl(normalizedAvatar);
        int updated = userMapper.updateProfile(user);
        return updated > 0 ? user : null;
    }
}

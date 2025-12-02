package com.example.campussecondhandgoods.mapper;

import com.example.campussecondhandgoods.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User login(String username, String password);
    int register(User user);
    User getUserDetail(int id);
    User findByUsername(String username);
    int updateProfile(User user);
}


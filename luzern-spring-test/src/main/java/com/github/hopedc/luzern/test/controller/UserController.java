package com.github.hopedc.luzern.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.github.hopedc.luzern.test.vo.User;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户模块
 *
 * @author hopedc
 * @date 2017-03-03 10:11
 */
@Controller
@RequestMapping(path = "user")
public class UserController {

//    /**
//     * 登录
//     *
//     * @param username 用户名|必填
//     * @param password 密码
//     * @return 当前登录用户的基本信息
//     * @resp code 返回码(0000表示登录成功,其它表示失败)|string|必填
//     * @resp msg 登录信息|string
//     * @resp username 登录成功后返回的用户名|string
//     */
//    @ResponseBody
//    @PostMapping("login")
//    public Map<String, String> login(String username, @NotNull String password, @RequestParam("openId1") String openId) {
//        Map<String, String> model = new HashMap<>();
//        model.put("code", "0000");
//        model.put("msg", "登录成功");
//        model.put("username", username);
//        return model;
//    }


    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册后生成的用户的基本信息
     * @respbody {"id":"123","password":"123456","username":"admin"}
     * @title 注册
     * @resp score 分数
     */
    @ResponseBody
    @RequestMapping(value = "register", method = {RequestMethod.POST, RequestMethod.PUT})
    User register(User user) {
        user.setId(UUID.randomUUID().toString());
        return user;
    }

    public static void main(String[] args) {

    }
}

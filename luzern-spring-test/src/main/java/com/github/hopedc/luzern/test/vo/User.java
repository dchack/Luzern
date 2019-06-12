package com.github.hopedc.luzern.test.vo;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 用户
 *
 * @author hopedc
 * @date 2017-03-03 10:13
 */
public class User {

    /**
     * 用户ID
     */
    @NotBlank(message = "设备号不能为空")
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}

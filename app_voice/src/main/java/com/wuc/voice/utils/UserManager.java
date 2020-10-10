package com.wuc.voice.utils;

import com.wuc.voice.model.user.User;

/**
 * @author: wuchao
 * @date: 2019-09-19 17:54
 * @desciption: 单例管理登陆用户信息
 */
public class UserManager {

    private User mUser = null;

    private UserManager() {
    }

    public static UserManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 保存用户信息到内存
     *
     * @param user user
     */
    public void setUser(User user) {
        mUser = user;
        saveLocal(user);
    }

    /**
     * 保存到数据库，持久化用户信息
     *
     * @param user user
     */
    private void saveLocal(User user) {
    }

    /**
     * 删除用户信息
     */
    public void removeUser() {
        mUser = null;
        removeLocal();
    }

    /**
     * 从数据库中删除用户信息
     */
    private void removeLocal() {
    }

    /**
     * 判断是否登录过
     *
     * @return boolean
     */
    public boolean hasLogined() {
        return getUser() != null;
    }

    /**
     * 获取用户信息
     *
     * @return User
     */
    public User getUser() {
        return mUser;
    }

    /**
     * 从本地获取
     *
     * @return User
     */
    private User getLocal() {
        return null;
    }

    private static class Holder {
        private static final UserManager INSTANCE = new UserManager();
    }
}

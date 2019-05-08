package com.com.sms;

/**
 * Created by lenovo on 2018/8/14.
 */
public interface ISmsService {
    /**
     * 发送验证码
     * @param rand
     * @return
     */
    int sendRand(String phone, String rand);
}

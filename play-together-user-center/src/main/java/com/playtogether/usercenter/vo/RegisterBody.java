package com.playtogether.usercenter.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

/** 此类用于封装注册表单中的数据
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 16:11
 */
@Data
public class RegisterBody {
    /**
     * 用户昵称（唯一）
     */
    private String nickname;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 判断是否有null字段
     * @return 有null字段，返回true
     */
    public boolean haveEmptyField() {
        return StringUtils.isEmpty(this.nickname) || StringUtils.isEmpty(this.password)
                || StringUtils.isEmpty(this.phone) || StringUtils.isEmpty(this.verifyCode);
    }
}

package com.playtogether.usercenter.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
    @NotNull(message = "昵称不能为空")
    @Length(min = 3, max = 12, message = "昵称为3-12个字符")
    private String nickname;

    /**
     * 用户密码
     */
    @NotNull(message = "密码不能为空")
    @Length(min = 8, max = 16, message = "密码为8-16个字符")
    private String password;

    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /**
     * 验证码
     */
    @NotNull(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码为6位")
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

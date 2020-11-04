package com.playtogether.sms.mq;

import cn.hutool.json.JSONUtil;
import com.playtogether.sms.config.SmsProperties;
import com.playtogether.sms.utils.SmsUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author guanlibin
 * @version 1.0
 * @create 2020/9/25 18:45
 */
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private SmsUtils smsUtils;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(name = "pt.sms.exchange", type = "topic"),
            key = "sms.verify.code"
    ))
    public void listenVerificationCode(Map<String, String> msg) {
        if (msg == null || CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isEmpty(phone)) {
            return;
        }
        // 发送短信
        smsUtils.sendSms(phone,smsProperties.getSignName(),
                smsProperties.getVerifyCodeTemplate(), JSONUtil.toJsonStr(msg));
    }
}

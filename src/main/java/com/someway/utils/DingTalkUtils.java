package com.someway.utils;

import com.alibaba.fastjson2.JSON;
import com.someway.domain.DingTalkAlarmConfig;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * @Author lit
 * @Date 2024-07-06 16:51
 **/
@Slf4j
public class DingTalkUtils {

    /**
     * 发送钉钉告警消息
     */
    public static void doSend(String msgType, String channel, List<String> atList, Boolean isAtAll, String msgData) {
        DingTalkAlarmConfig content = new DingTalkAlarmConfig();
        content.setMsgtype(msgType);
        content.setText(new DingTalkAlarmConfig.Text(msgData));
        content.setAt(new DingTalkAlarmConfig.At(isAtAll, atList));
        String jsonString = JSON.toJSONString(content);
        String response = HttpClientUtils.postJSON(channel, jsonString);
        log.info("The DingTalk message has been sent successfully: {}", response);

    }


}

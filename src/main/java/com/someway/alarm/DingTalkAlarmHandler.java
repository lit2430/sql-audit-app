package com.someway.alarm;

import com.alibaba.fastjson2.JSONObject;
import com.someway.domain.DingTalkAlarmConfig;
import com.someway.utils.DingTalkUtils;

/**
 * @Author lit
 * @Date 2024-07-06 16:59
 **/
public class DingTalkAlarmHandler extends AbstractAlarmHandler {


    @Override
    public void invoke(String config, String msg) {
        DingTalkAlarmConfig dingTalk = JSONObject.parseObject(config, DingTalkAlarmConfig.class);
        String dingTalkUrl = dingTalk.getWebhook();
        String msgtype = dingTalk.getMsgtype();
        DingTalkUtils.doSend(msgtype, dingTalkUrl, dingTalk.getAt().getAtMobiles(), dingTalk.getAt().getIsAtAll(), msg);
    }


}

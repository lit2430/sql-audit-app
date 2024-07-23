package com.someway.funcation;

import cn.hutool.json.JSONUtil;
import com.someway.alarm.IAlarmHandler;
import com.someway.domain.DingTalkAlarmConfig;
import com.someway.template.TemplateConfig;
import com.someway.utils.CustFreemarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import java.util.Map;

/**
 * @Author lit
 * @Date 2024-07-07 15:09
 **/
@Slf4j
public class RecodeMapFunction implements MapFunction<String, String> {


    public IAlarmHandler alarmHandler;

    public DingTalkAlarmConfig alarm;


    public RecodeMapFunction(IAlarmHandler alarmHandler, DingTalkAlarmConfig alarm) {
        this.alarmHandler = alarmHandler;
        this.alarm = alarm;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String map(String s) throws Exception {
        Map<String, Object> recodeMap = JSONUtil.toBean(s, Map.class);
        String freemarker = CustFreemarkerUtils.replace("template", TemplateConfig.SQL_AUDIT_ALARM_TEMPLATE, recodeMap);
        log.info("The message content after build is:{}", freemarker);
        alarmHandler.invoke(JSONUtil.toJsonStr(alarm), freemarker);
        return null;
    }
}

package com.someway.alarm;

import java.io.Serializable;

/**
 * @Author lit
 * @Date 2024-07-06 09:04
 **/
public interface IAlarmHandler extends Serializable {


    /**
     * @param config 配置信息
     * @param msg    消息内容
     */
    void invoke(String config, String msg);


}

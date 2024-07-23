package com.someway.alarm;

import com.someway.enums.AlarmEnum;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author lit
 * @Date 2024-07-06 17:03
 **/
public class AlarmManager {

    private final ConcurrentHashMap<AlarmEnum, IAlarmHandler> alarmList = new ConcurrentHashMap<>();


    private static final AlarmManager holder = new AlarmManager();


    public void add() {
        register(new DingTalkAlarmHandler()); //钉钉告警
    }


    private void register(IAlarmHandler alarm) {
        alarmList.put(AlarmEnum.WEBHOOK, alarm);
    }


    public IAlarmHandler getAlarmContent(AlarmEnum type) {
        return alarmList.get(type);
    }


    public static AlarmManager getInstance() {
        return holder;
    }


}

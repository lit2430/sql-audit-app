package com.someway.enums;

/**
 * @Author lit
 * @Date 2024-07-07 17:05
 **/
public enum AlarmEnum {

    WEBHOOK(1, "WEBHOOK"),
    EMAIL(2, "EMAIL"),
    WECHAT(3, "WECHAT");


    private Integer code;

    private String name;


    AlarmEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }


    public static AlarmEnum getAlarm(String name) {
        for (AlarmEnum type : AlarmEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}

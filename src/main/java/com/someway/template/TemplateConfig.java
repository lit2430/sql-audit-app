package com.someway.template;

/**
 * @Author lit
 * @Date 2024-07-07 18:57
 **/
public class TemplateConfig {


    public static final String SQL_AUDIT_ALARM_TEMPLATE =
            "【MYSQL审计告警】\n" +
                    " --------------------------\n" +
                    "> 【操作类型】：" + "${operate}" + "\n" +
                    "> 【操作库表】：" + "${dbTable}" + "\n" +
                    "> 【DDL语句】：" + "${ddl}" + "\n" +
                    "> 【操作时间】：" + "${timestamp}" + "\n";
}

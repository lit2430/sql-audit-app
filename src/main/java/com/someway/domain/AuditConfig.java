package com.someway.domain;

import lombok.Data;

import java.util.List;

/**
 * @Author lit
 * @Date 2024-07-06 17:33
 **/
@Data
public class AuditConfig {


    private String auditName;

    private Job job;

    private Source source;

    private DingTalkAlarmConfig alarm;


    @Data
    public static class Job {
        private String startMode;
        private String timestamp;
        private String serverId;
    }

    @Data
    public static class Source {

        // mysql, oracle, postgresql
        private String sourceType;

        private String hostname;

        private Integer port;

        private String username;

        private String password;

        // white db list
        private List<String> databaseList;

        // white table list
        private List<String> tableList;

        // black db list
        private List<String> blackDatabaseList;

    }


}

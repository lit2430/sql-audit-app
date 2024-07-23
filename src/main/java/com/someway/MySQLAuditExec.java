package com.someway;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson2.JSONObject;
import com.someway.alarm.AlarmManager;
import com.someway.alarm.IAlarmHandler;
import com.someway.deserialize.MysqlSchemaDeserialization;
import com.someway.domain.AuditConfig;
import com.someway.domain.DingTalkAlarmConfig;
import com.someway.enums.AlarmEnum;
import com.someway.funcation.OperateFilterFunction;
import com.someway.funcation.RecodeMapFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cdc.connectors.mysql.source.MySqlSource;
import org.apache.flink.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * @Author lit
 * @Date 2024-07-07 17:22
 **/
@Slf4j
public class MySQLAuditExec extends SQLAuditExec {

    public static void main(String[] args) throws Exception {
        new MySQLAuditExec().run(args);
    }


    @Override
    protected void run(String[] args) throws Exception {
        String configStr = parseConfig(args);
        AuditConfig auditConfig = JSONObject.parseObject(configStr, AuditConfig.class);
        AuditConfig.Job job = auditConfig.getJob(); //job
        AuditConfig.Source source = auditConfig.getSource(); // source
        DingTalkAlarmConfig alarm = auditConfig.getAlarm(); //alarm
        AlarmManager.getInstance().add();
        IAlarmHandler alarmHandler = buildAlarmHandler(alarm);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置全局并行度
        env.setParallelism(1);
        // 设置时间语义为ProcessingTime
         env.getConfig().setAutoWatermarkInterval(0);
        // 每隔60s启动一个检查点
        env.enableCheckpointing(60000, CheckpointingMode.EXACTLY_ONCE);
        // checkpoint最小间隔
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
        // checkpoint超时时间
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 同一时间只允许一个checkpoint
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // Flink处理程序被cancel后，会保留Checkpoint数据
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        String startMode = job.getStartMode();
        StartupOptions startupOptions;
        if ("timestamp".equals(startMode)) {
            long timestamp = Long.parseLong(job.getTimestamp());
            startupOptions = getStartupOptions(startMode, timestamp);
        } else {
            startupOptions = getStartupOptions(startMode, null);
        }
        List<String> databaseList = source.getDatabaseList();
        List<String> tableList = source.getTableList();
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
                .hostname(source.getHostname())
                .port(source.getPort())
                .username(source.getUsername())
                .password(source.getPassword())
                .databaseList(CollectionUtil.isEmpty(databaseList) ? "" : String.valueOf(source.getDatabaseList()))
                .tableList(CollectionUtil.isEmpty(tableList) ? "" : String.valueOf(source.getTableList()))
                .startupOptions(startupOptions)  // 启动模式
                .includeSchemaChanges(true) //设置是否开启监听schema变更
                .debeziumProperties(getDebeziumProperties())  // 设置时间格式
                .serverId(job.getServerId().isEmpty() ? String.valueOf(new Random().nextInt(1000)) : job.getServerId())
                // 自定义反序列化
                .deserializer(new MysqlSchemaDeserialization(source.getBlackDatabaseList()))
                .build();
        DataStreamSource<String> binlogSourceDs = env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "MYSQL_BINLOG_SOURCE");
        binlogSourceDs.filter(new OperateFilterFunction()).map(new RecodeMapFunction(alarmHandler, alarm));

        env.execute(auditConfig.getAuditName());


    }


    private static String parseConfig(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Illegal configuration params.");
        }
        FileReader fileReader = new FileReader(args[0]);
        String configStr = fileReader.readString();
        if (configStr == null || configStr.isEmpty()) {
            throw new RuntimeException("The configuration file content is empty.");
        }
        log.info("The configuration parameters loaded is : {}", configStr);
        return configStr;
    }


    protected static IAlarmHandler buildAlarmHandler(DingTalkAlarmConfig alarm) {
        if (alarm == null) {
            return null;
        }
        AlarmEnum alarmEnum = AlarmEnum.getAlarm(alarm.getAlarmType());
        IAlarmHandler alarmContent = AlarmManager.getInstance().getAlarmContent(alarmEnum);
        if (alarmContent == null) {
            log.error("Not found alarm handler, current alarm type is: {}", alarm.getAlarmType());
            throw new RuntimeException("Not found alarm handler.");
        }
        return alarmContent;
    }


    private static StartupOptions getStartupOptions(String syncType, Long timeStamp) {
        StartupOptions startupOptions = null;
        switch (syncType) {
            case "initial":
                startupOptions = StartupOptions.initial();
                break;
            case "earliest":
                startupOptions = StartupOptions.earliest();
                break;
            case "latest":
                startupOptions = StartupOptions.latest();
                break;
            case "timestamp":
                startupOptions = StartupOptions.timestamp(timeStamp);
                break;
        }
        return startupOptions;
    }


    public static Properties getDebeziumProperties() {
        Properties properties = new Properties();
        properties.setProperty("converters", "dateConverters");

        properties.setProperty("dateConverters.type", "com.someway.convert.MySqlDateTimeConverter");
        properties.setProperty("dateConverters.format.date", "yyyy-MM-dd");
        properties.setProperty("dateConverters.format.time", "HH:mm:ss");
        properties.setProperty("dateConverters.format.datetime", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp.zone", "UTC+8");

        properties.setProperty("debezium.snapshot.locking.mode", "none");
        properties.setProperty("include.schema.changes", "true");
        properties.setProperty("bigint.unsigned.handling.mode", "long");
        properties.setProperty("decimal.handling.mode", "double");
        properties.setProperty("cdc.encoding", "UTF-8");
        return properties;
    }
}

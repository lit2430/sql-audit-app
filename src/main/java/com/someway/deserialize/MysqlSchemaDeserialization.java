package com.someway.deserialize;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.someway.constant.LogStructConstant;
import com.someway.utils.CustDateUtils;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class MysqlSchemaDeserialization implements DebeziumDeserializationSchema<String> {

    private static final long serialVersionUID = -7296371594028636881L;

    public static final String DDL_DROP = "DROP";

    public static final String DDL_TRUNCATE = "TRUNCATE";

    private final List<String> blackDbList;


    public MysqlSchemaDeserialization(List<String> blackDbList) {
        this.blackDbList = blackDbList;
    }

    /**
     * {
     * "db":""
     * "tablename":"",
     * befor:json
     * after:json
     * op
     * }
     */
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) {
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        //filter dml
        if (operation != null) {
            return;
        }
        Map<@Nullable String, @Nullable Object> itemMap = Maps.newHashMap();
        Struct value = (Struct) sourceRecord.value();
        Struct sourceStruct = value.getStruct(LogStructConstant.SOURCE);
        String database = sourceStruct.getString(LogStructConstant.DB);
        String table = sourceStruct.getString(LogStructConstant.TABLE);
        //filter black db list
        if (blackDbList.contains(database)) {
            return;
        }
        Long timestamp = sourceStruct.getInt64(LogStructConstant.TS_MS);
        itemMap.put(LogStructConstant.TIMESTAMP, CustDateUtils.tsToDateStr(timestamp, CustDateUtils.YYYY_MM_DD_HH_mm_SS));
        itemMap.put("dbTable", database + "." + table);
        String historyRecordItem = value.getString(LogStructConstant.HISTORY_RECORD);
        if (Objects.nonNull(historyRecordItem)) {
            JSONObject historyRecord = JSONObject.parseObject(historyRecordItem);
            String ddl = historyRecord.getString(LogStructConstant.DDL);
            itemMap.put(LogStructConstant.DDL, ddl);
            JSONArray tableChanges = historyRecord.getJSONArray(LogStructConstant.TABLE_CHANGES);
            if (CollectionUtil.isNotEmpty(tableChanges)) {
                JSONObject tableChange = (JSONObject) tableChanges.get(0);
                String operate = tableChange.getString(LogStructConstant.TYPE); //操作类型
                itemMap.put(LogStructConstant.OPERATE, operate);
            } else {
                String ucDdl = ddl.trim().toUpperCase();
                if (ucDdl.startsWith(DDL_DROP)) {
                    itemMap.put(LogStructConstant.OPERATE, DDL_DROP);
                } else if (ucDdl.startsWith(DDL_TRUNCATE)) {
                    itemMap.put(LogStructConstant.OPERATE, DDL_TRUNCATE);
                }
            }
        }
        collector.collect(JSONUtil.toJsonStr(itemMap));
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }


}

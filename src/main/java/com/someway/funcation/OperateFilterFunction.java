package com.someway.funcation;

import org.apache.flink.api.common.functions.FilterFunction;

/**
 * @Author lit
 * @Date 2024-07-07 18:37
 **/
public class OperateFilterFunction implements FilterFunction<String> {
    @Override
    public boolean filter(String s) throws Exception {
        //TODO customer filter condition
        return true;
    }
}

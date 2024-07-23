package com.someway.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author lit
 * @Date 2024-07-06 17:51
 **/
@Data
public abstract class AbsAlarmConfig implements Serializable {

    protected String alarmType;


}

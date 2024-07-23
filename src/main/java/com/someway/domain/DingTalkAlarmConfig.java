package com.someway.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author lit
 * @Date 2024-07-06 17:51
 **/
@Data
public class DingTalkAlarmConfig extends AbsAlarmConfig {




    private String webhook;

    private Text text;

    private String msgtype;

    private At at;


    @Data
    public static class Text implements Serializable {
        private String content;

        public Text(String content) {
            this.content = content;
        }
    }

    @Data
    public static class At implements Serializable {
        private Boolean isAtAll;
        private List<String> atMobiles;

        public At(Boolean isAtAll, List<String> atMobiles) {
            this.isAtAll = isAtAll;
            this.atMobiles = atMobiles;
        }
    }


}

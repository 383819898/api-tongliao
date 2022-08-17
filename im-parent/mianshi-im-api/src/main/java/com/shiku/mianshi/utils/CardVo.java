package com.shiku.mianshi.utils;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Data
public class CardVo {

    @JsonProperty("error_code")
    private String errorCode;


    private String reason;
    private ResultVo result;


}


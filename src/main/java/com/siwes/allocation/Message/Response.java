package com.siwes.allocation.Message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private Boolean success;
    private String code;
    private String message;

    private Object data;
    private Object error;
}

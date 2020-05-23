package com.ferapy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class SessionDTO {

    private String zoomId;
    private String listener;
    private String talker;
    private Boolean isVideoSession;
    private Boolean isTalkerAnonymous;

}

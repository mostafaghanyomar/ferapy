package com.ferapy.document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static java.util.Objects.isNull;

@Document
@Data
@Accessors(chain = true)
public class SessionDO {

    @Id
    @Setter
    @Getter
    private String id;

    private String zoomId;
    private String listener;
    private String talker;
    private Boolean isVideoSession;
    private Boolean isTalkerAnonymous;

    @Getter
    private Date created;
    @Getter
    private Date lastModified;

    public SessionDO audit() {
        boolean isNew = isNull(this.id);
        if (isNew) {
            this.created = new Date();
        }
        this.lastModified = new Date();
        return this;
    }

}

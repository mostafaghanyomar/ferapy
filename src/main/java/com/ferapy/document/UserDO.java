package com.ferapy.document;

import com.ferapy.document.constant.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;

@Document
@Data
@Accessors(chain = true)
public class UserDO {

    @Id
    @Setter
    @Getter
    private String id;
    @Indexed(unique=true)
    private String username;
    @Indexed(unique=true)
    private String email;
    private String password;
    private String firstname;
    private String lastname;

    private List<Role> authorities;

    private String sessionId;

    @Getter
    private Date created;
    @Getter
    private Date lastModified;

    public UserDO audit() {
        boolean isNew = isNull(this.id);
        if (isNew) {
            this.created = new Date();
        }
        this.lastModified = new Date();
        return this;
    }

}
package com.ylqq.document.pojo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

/**
 * @author ylqq
 */
@Data
@NonNull
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Document("user")
public class User implements Serializable {
    /**
     * 序列化id
     */
    public static final long serialVersionUID = 1L;
    @MongoId
    private Integer userid;
    private String loginName;
    private String password;
    private String userName;
    private String job;
    private String phone;
    private String email;
    private Integer instId;
    private Integer roleId;
    private Integer userStatus;
}

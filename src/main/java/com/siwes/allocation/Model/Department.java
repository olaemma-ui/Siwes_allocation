package com.siwes.allocation.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter @Setter
public class Department {
    @Id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String name;

    private Long totalStudent;

    private String deptCode;

    @Transient
    private String sessionId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp updatedAt;

    @ManyToOne
    @JsonIgnore
    private Session session;
}

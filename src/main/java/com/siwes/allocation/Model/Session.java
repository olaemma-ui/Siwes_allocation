package com.siwes.allocation.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Getter @Setter
public class Session {
    @Id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private Integer year;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String session;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp updatedAt;
}

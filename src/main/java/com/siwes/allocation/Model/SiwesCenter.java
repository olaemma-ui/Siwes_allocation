package com.siwes.allocation.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter @Setter
public class SiwesCenter {
    @Id
    private String id;

    private String name;

    private String description;

    private Long population;

    private String location;

    private String email;

    private String phone;

    private String industry;

//    @Lob
    @Column(columnDefinition ="LONGBLOB")
    private byte[] logo;

    private String fileName;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    @ManyToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Session session;
}

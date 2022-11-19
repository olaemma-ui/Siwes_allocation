package com.siwes.allocation.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Student {

    @Id
    private String matricNo;

    private String status;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public void setMatricNo(int no) {
        String year = String.valueOf(this.getSession().getYear());
        year = year.substring(year.length()-2, year.length());
        String deptCode = this.getDepartment().getDeptCode();
        String num = (no < 10) ? "000"+no :(no < 100) ?"00"+no :(no < 1000) ? "0"+no : ""+no;
        this.matricNo = year+"/"+deptCode+"/"+num;
    }

    @ManyToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Department department;

    @ManyToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Session session;

    @ManyToOne
    private SiwesCenter siwesCenter;
}

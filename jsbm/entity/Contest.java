package com.jsbm.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class Contest implements Serializable {

    private String contestName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signBeginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signEndTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date contestBeginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date contestEndTime;

    private String contestForm;

    private Integer teamNumber;
}

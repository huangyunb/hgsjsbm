package com.jsbm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Temporary{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String contestName;

    private String studentName;

    private String studentSex;

    private int studentId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signTime;

    private String signEmail;

    private String signPhone;

    private int status;
}

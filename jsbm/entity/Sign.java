package com.jsbm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Sign {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String contestName;

    private String studentName;

    private int studentId;

    private String studentSex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signTime;

    private String signEmail;

    private String signPhone;

}

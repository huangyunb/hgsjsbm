package com.jsbm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Teacher {

    private static final long serialVersionUID = 1L;

    private Integer teacherId;

    private String teacherName;

    private String teacherPassword;

    private String teacherSex;

    private String teacherCollege;

    private String teacherPhone;

    private String teacherEmail;

}

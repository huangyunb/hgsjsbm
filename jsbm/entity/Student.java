package com.jsbm.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Student implements Serializable {

    private Integer studentId;

    private String studentPassword;

    private String studentName;

    private String studentSex;

    private String studentIdCard;

    private String studentCollege;

    private String studentMajor;

    private String studentGrade;

    private String studentClass;

    private String studentPhone;

    private String studentEmail;

}
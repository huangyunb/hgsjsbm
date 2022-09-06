package com.jsbm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Team {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer studentId;

    private String competitionName;

    private String teamName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTeamTime;

    private int teamPassword;

    private Integer number;

}

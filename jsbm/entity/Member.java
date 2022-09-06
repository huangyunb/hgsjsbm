package com.jsbm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Member {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer teamId;//与团队id相同

    private Integer studentId;

    private String competition;

    private String teamName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date joinTeam;
}

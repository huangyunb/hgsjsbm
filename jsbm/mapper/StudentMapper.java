package com.jsbm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsbm.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    int addExcelStudent(Student student);
}

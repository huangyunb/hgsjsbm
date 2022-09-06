package com.jsbm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsbm.entity.Teacher;
import com.jsbm.mapper.TeahcerMapper;
import com.jsbm.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeahcerMapper, Teacher> implements TeacherService {
}

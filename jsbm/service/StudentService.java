package com.jsbm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsbm.entity.Student;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService extends IService<Student> {
    int addExcelStudent(MultipartFile file) throws Exception;
}

package com.jsbm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsbm.entity.Student;
import com.jsbm.mapper.StudentMapper;
import com.jsbm.service.StudentService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    public StudentService studentService;

    @Override
    public int addExcelStudent(MultipartFile file) throws Exception {
        int result = 0;

        //判断文本文件
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1 );
        InputStream ins = file.getInputStream();
        Workbook wb = null;

        if(suffix.equals("xlsx")){
            wb =new XSSFWorkbook(ins);
        }else{
            wb =new HSSFWorkbook(ins);
        }

        //获取excel表单
        Sheet sheet = wb.getSheetAt(0);

        if(null != sheet){
            for(int line =1; line <= sheet.getLastRowNum();line++){
                Student student =new Student();
                Row row = sheet.getRow(line);
                System.out.println(row);
                if(null == row){
                    continue;
                }
                if(1 != row.getCell(0).getCellType()){
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                }

                String id = row.getCell(0 ).getStringCellValue();
                int studentId;

                if (id != null && !id.equals("")){
                    studentId = new Integer(id);
                }else{
                    break;
                }

                System.out.println(studentId);
                row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);

                String studentPassword = row.getCell(1 ).getStringCellValue();
                row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);

                String studentName = row.getCell(2).getStringCellValue();
                row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);

                String studentSex = row.getCell(3).getStringCellValue();
                row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);

                String studentIdCard = row.getCell(4).getStringCellValue();
                row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);

                String studentCollege = row.getCell(5).getStringCellValue();
                row.getCell(6).setCellType(Cell.CELL_TYPE_STRING);

                String studentMajor = row.getCell(6).getStringCellValue();
                row.getCell(7).setCellType(Cell.CELL_TYPE_STRING);

                String studentGrade = row.getCell(7).getStringCellValue();
                row.getCell(8).setCellType(Cell.CELL_TYPE_STRING);

                String studentClass = row.getCell(8).getStringCellValue();

                student.setStudentId(studentId);
                student.setStudentPassword(studentPassword);
                student.setStudentName(studentName);
                student.setStudentSex(studentSex);
                student.setStudentIdCard(studentIdCard);
                student.setStudentCollege(studentCollege);
                student.setStudentCollege(studentCollege);
                student.setStudentMajor(studentMajor);
                student.setStudentGrade(studentGrade);
                student.setStudentClass(studentClass);

                studentService.save(student);
                result += 1;
            }
        }
        return result;
    }
}

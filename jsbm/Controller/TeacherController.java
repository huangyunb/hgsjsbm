package com.jsbm.Controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsbm.entity.*;
import com.jsbm.service.*;
import com.jsbm.utils.Time;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/teacherInfo")
public class TeacherController {

    @Autowired
    public TeacherService teacherService;

    @Autowired
    public StudentService studentService;

    @Autowired
    public ContestService contestService;

    @Autowired
    public TemporaryService temporaryService;

    @Autowired
    public TeamService teamService;

    @Autowired
    public MemberService memberService;

    @Autowired
    public SignService signService;

    @PostMapping("/login")
    public String login(int teacherId, String teacherPassword, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        //查询是否有此人
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getTeacherId,teacherId);
        queryWrapper.eq(Teacher::getTeacherPassword,teacherPassword);

        Teacher teacher = teacherService.getOne(queryWrapper);

        if(teacher == null){
            out.write("<script>alert('职工号或密码不正确');history.back();</script>");
            return "/teacherInfo/index";
        }else {
            HttpSession session = request.getSession();
            session.setAttribute("teacher",teacher);
            session.setAttribute("teacherId",teacherId);

            return "/teacherInfo/teacher";
        }

    }

    //查看所有竞赛
    @GetMapping("/showAllContest")
    public String showAllContest(HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        //构造条件构造器
        LambdaQueryWrapper<Contest> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过过滤条件
        queryWrapper1.orderByDesc(Contest::getSignEndTime);
        //执行查询
        List<Contest> contestList = contestService.list(queryWrapper1);
        session.setAttribute("contestList",contestList);
        return "/teacherInfo/allContest";
    }

    //查询竞赛（通过搜索框）
    @GetMapping("/searchContest")
    public String searchContest(String contestname, HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        //构造条件构造器
        LambdaQueryWrapper<Contest> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过过滤条件
        queryWrapper1.like(Contest::getContestName,contestname);
        queryWrapper1.orderByDesc(Contest::getSignEndTime);
        //执行查询
        List<Contest> contestList = contestService.list(queryWrapper1);
        session.setAttribute("contestList",contestList);
        return "/teacherInfo/allContest";
    }

    //查看具体竞赛
    @GetMapping("checkContest")
    public String checkContest(String contestname, HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        LambdaQueryWrapper<Contest> contestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contestLambdaQueryWrapper.eq(Contest::getContestName,contestname);
        Contest contest = contestService.getOne(contestLambdaQueryWrapper);
        session.setAttribute("contest",contest);

        return "/teacherInfo/editContest";
    }

    //修改竞赛
    @PostMapping("updateContest")
    public String updateContest(Contest contest, HttpServletRequest request, HttpServletResponse response)throws IOException{

        System.out.println(contest);
        HttpSession session = request.getSession();

        LambdaQueryWrapper<Contest> contestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contestLambdaQueryWrapper.eq(Contest::getContestName,contest.getContestName());
        contestService.update(contest,contestLambdaQueryWrapper);
        session.setAttribute("contest",contest);

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("<script>alert('修改成功')</script>");

        return "/teacherInfo/editContest";
    }

    //添加竞赛
    @PostMapping("saveContest")
    public String saveContest(Contest contest, HttpServletRequest request, HttpServletResponse response)throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LambdaQueryWrapper<Contest> contestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contestLambdaQueryWrapper.eq(Contest::getContestName,contest.getContestName());
        Contest contest1 = contestService.getOne(contestLambdaQueryWrapper);
        if (contest1 != null){
            out.write("<script>alert('该竞赛已经存在');history.back();</script>");
            return "/teacherInfo/loose";
        }else {
            System.out.println(contest);
            contestService.save(contest);
            HttpSession session = request.getSession();
            session.setAttribute("contest",contest);

            out.write("<script>alert('添加成功')</script>");
            return "/teacherInfo/editContest";
        }

    }

    //查看正在报名的竞赛
    @GetMapping("/nowSign")
    public String nowSign(HttpServletRequest request, HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        //构造条件构造器
        LambdaQueryWrapper<Contest> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过过滤条件
        queryWrapper1.eq(Contest::getContestForm,0);
        queryWrapper1.lt(Contest::getSignBeginTime,Time.getNow());
        queryWrapper1.gt(Contest::getSignEndTime,Time.getNow());
        //执行查询
        List<Contest> contestList = contestService.list(queryWrapper1);
        session.setAttribute("contestList",contestList);
        return "/teacherInfo/signing";
    }

    //搜索具体的正在报名的竞赛
    @GetMapping("/searchSigning")
    public String searchSigning(String contestname, HttpServletRequest request, HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        //构造条件构造器
        LambdaQueryWrapper<Contest> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过过滤条件
        queryWrapper1.eq(Contest::getContestForm,0);
        queryWrapper1.like(Contest::getContestName,contestname);
        queryWrapper1.lt(Contest::getSignBeginTime,Time.getNow());
        queryWrapper1.gt(Contest::getSignEndTime,Time.getNow());
        //执行查询
        List<Contest> contestList = contestService.list(queryWrapper1);
        session.setAttribute("contestList",contestList);
        return "/teacherInfo/signing";
    }

    //查看竞赛的所有报名
    @GetMapping("checkSigns")
    public String checkSigns(String contestname, HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        LambdaQueryWrapper<Sign> signLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signLambdaQueryWrapper.eq(Sign::getContestName,contestname);
        List<Sign> signList = signService.list(signLambdaQueryWrapper);
        session.setAttribute("signList",signList);
        session.setAttribute("contestName",contestname);
        return "/teacherInfo/signs";
    }

    //搜索报名的具体学生
    @GetMapping("checkSign")
    public String checkSign(int studentId, HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        String contestname = (String) session.getAttribute("contestName");
        LambdaQueryWrapper<Sign> signLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signLambdaQueryWrapper.eq(Sign::getContestName,contestname);
        signLambdaQueryWrapper.eq(Sign::getStudentId,studentId);
        List<Sign> signList = signService.list(signLambdaQueryWrapper);
        session.setAttribute("signList",signList);

        return "/teacherInfo/signs";
    }

    @GetMapping("examination")
    public String examination(String contestname, HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        LambdaQueryWrapper<Temporary> temporaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        temporaryLambdaQueryWrapper.eq(Temporary::getContestName,contestname);
        temporaryLambdaQueryWrapper.eq(Temporary::getStatus,1);
        List<Temporary> temporaryList = temporaryService.list(temporaryLambdaQueryWrapper);
        session.setAttribute("temporaryList",temporaryList);
        session.setAttribute("contestName",contestname);
        return "/teacherInfo/approval";
    }

    //审批通过（单人）
    @GetMapping("pass")
    public String pass(int id, HttpServletRequest request, HttpServletResponse response)throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        Temporary temporary = temporaryService.getById(id);
        Sign sign = new Sign();
        BeanUtils.copyProperties(temporary, sign, new String[]{"id","status"});
        signService.save(sign);
        temporaryService.removeById(id);
        out.write("<script>alert('审批通过')</script>");

        return "/teacherInfo/signing";
    }

    //审批通过（全部）
    @GetMapping("passAll")
    public String passAll( HttpServletRequest request, HttpServletResponse response)throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String contestname = (String) session.getAttribute("contestName");
        LambdaQueryWrapper<Contest> contestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contestLambdaQueryWrapper.eq(Contest::getContestName,contestname);
        Contest contest = contestService.getOne(contestLambdaQueryWrapper);

        LambdaQueryWrapper<Temporary> temporaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        temporaryLambdaQueryWrapper.eq(Temporary::getContestName,contest.getContestName());
        temporaryLambdaQueryWrapper.gt(Temporary::getSignTime,contest.getSignBeginTime());
        temporaryLambdaQueryWrapper.lt(Temporary::getSignTime,contest.getSignEndTime());
        temporaryLambdaQueryWrapper.eq(Temporary::getStatus,1);
        List<Temporary> temporaryList = temporaryService.list(temporaryLambdaQueryWrapper);

        for (Temporary temporary : temporaryList){
            Sign sign = new Sign();
            BeanUtils.copyProperties(temporary, sign, new String[]{"id","status"});
            signService.save(sign);
            temporaryService.removeById(temporary.getId());
        }

        session.setAttribute("temporaryList",temporaryList);
        out.write("<script>alert('全部通过')</script>");
        return "/teacherInfo/approval";
    }

    //添加教师
    @PostMapping("saveTeacher")
    public String saveTeacher(Teacher teacher, HttpServletRequest request, HttpServletResponse response)throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Teacher::getTeacherId,teacher.getTeacherId());
        Teacher newteacher = teacherService.getOne(teacherLambdaQueryWrapper);
        if (newteacher != null){
            out.write("<script>alert('该教师号已经存在已');history.back();</script>");
            return "/teacherInfo/loose";
        }else {
            teacherService.save(teacher);
            out.write("<script>alert('新增教师成功')</script>");
            return "/teacherInfo/addTeacher";
        }

    }

    //添加学生
    @PostMapping("saveStudent")
    public String saveStudent(Student student, HttpServletRequest request, HttpServletResponse response)throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.eq(Student::getStudentId,student.getStudentId());
        Student student1 = studentService.getOne(studentLambdaQueryWrapper);
        if (student1 != null){
            out.write("<script>alert('该学号已经存在已');history.back();</script>");
            return "/teacherInfo/loose";
        }else {
            studentService.save(student);
            out.write("<script>alert('新增学生成功')</script>");
            return "/teacherInfo/addStudents";
        }

    }

    //导入学生
    @PostMapping("saveStudents")
    public String saveStudents(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)throws IOException{

        System.out.println(file);

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int i = 0;

        try {
            i = studentService.addExcelStudent(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (i > 0){
            out.write("<script>alert('导入成功');history.back();</script>");
        }else{
            out.write("<script>alert('导入失败');history.back();</script>");
        }
        return "/teacherInfo/addStudents";

    }

    @GetMapping("showTeacher")
    public String showTeacher(
            HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        session.setAttribute("teacher",teacher);

        return "/teacherInfo/teacherInformation";
    }

    @PostMapping("editTeacherReady")
    public String editTeacherReady(HttpServletRequest request,
                           HttpServletResponse response){

        HttpSession session = request.getSession();
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        session.setAttribute("teacher",teacher);
        return "/teacherInfo/editTeacher";
    }

    @PostMapping("updateTeacher")
    public String updateTeacher(Teacher teacher, HttpServletRequest request,
                           HttpServletResponse response) throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Teacher newteacher = new Teacher();
        BeanUtils.copyProperties(teacher, newteacher, new String[]{"teacherPassword"});
        Teacher oldteacher = (Teacher) session.getAttribute("teacher");
        newteacher.setTeacherPassword(oldteacher.getTeacherPassword());
        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Teacher::getTeacherId,teacher.getTeacherId());
        teacherService.update(newteacher,teacherLambdaQueryWrapper);
        session.setAttribute("teacher",newteacher);
        out.write("<script>alert('个人信息修改成功')</script>");
        return "/teacherInfo/teacherInformation";
    }

    @GetMapping("getPassword")
    public String getPassword(HttpServletRequest request,
                                 HttpServletResponse response){
        HttpSession session = request.getSession();
        Teacher teacher =(Teacher) session.getAttribute("teacher");
        session.setAttribute("password",teacher.getTeacherPassword());

        return "/teacherInfo/editPassword";
    }

    @PostMapping("updatePassword")
    public String updatePassword (
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newPassword2") String newPassword2,
            HttpServletRequest request,HttpServletResponse response)throws IOException {

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        if (newPassword.equals(newPassword2)){
            HttpSession session = request.getSession();
            Teacher teacher =(Teacher) session.getAttribute("teacher");
            teacher.setTeacherPassword(newPassword);

            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.eq(Teacher::getTeacherId,teacher.getTeacherId());
            teacherService.update(teacher,teacherLambdaQueryWrapper);
            session.setAttribute("teacher",teacher);
            session.setAttribute("password",newPassword);
            out.write("<script>alert('密码修改成功')</script>");
            return "/teacherInfo/editPassword";

        }else {
            out.write("<script>alert('前后两次输入的密码不一致');history.back();</script>");
            return "/teacherInfo/editPassword";
        }


    }

}

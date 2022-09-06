package com.jsbm.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsbm.entity.*;
import com.jsbm.service.*;
import com.jsbm.utils.Time;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/userInfo")
public class StudentController {

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
    public String login(int studentId, String studentPassword, HttpServletRequest request, HttpServletResponse response)throws ParseException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        //查询是否有此人
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentId,studentId);
        queryWrapper.eq(Student::getStudentPassword,studentPassword);

        Student student = studentService.getOne(queryWrapper);

        if(student == null){
            out.write("<script>alert('学号或密码不正确');history.back();</script>");
            return "index";
        }else {
            HttpSession session = request.getSession();
            session.setAttribute("user",student);
            session.setAttribute("userId",studentId);

            return "student";
        }

    }

    //可报名的竞赛所有竞赛
    @GetMapping("/showContest")
    public String showContest(HttpServletRequest request, HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        //构造条件构造器
        LambdaQueryWrapper<Contest> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过过滤条件
        queryWrapper1.lt(Contest::getSignBeginTime,Time.getNow());
        queryWrapper1.gt(Contest::getSignEndTime,Time.getNow());
        //执行查询
        List<Contest> contestList = contestService.list(queryWrapper1);
        session.setAttribute("contestList",contestList);
        return "choose";
    }

    //搜索竞赛
    @GetMapping("/search")
    public String search( String contestname ,HttpServletRequest request, HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        //构造条件构造器
        LambdaQueryWrapper<Contest> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过过滤条件
        queryWrapper1.like(Contest::getContestName,contestname);
        queryWrapper1.lt(Contest::getSignBeginTime,Time.getNow());
        queryWrapper1.gt(Contest::getSignEndTime,Time.getNow());
        //执行查询
        List<Contest> contestList = contestService.list(queryWrapper1);
        session.setAttribute("contestList",contestList);
        return "choose";
    }

    //保存或提交报名信息
    @PostMapping("/tomporary")
    public String tomporary(@RequestParam("studentEmail") String studentEmail,
                            @RequestParam("studentPhone") String studentPhone,
                            @RequestParam("status") Integer status,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws ParseException, IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Temporary temporary = new Temporary();
        //直接传入的数据
        temporary.setSignEmail(studentEmail);
        temporary.setSignPhone(studentPhone);
        //从学生基本信息里拿的数据，报名时不可更改
        Student student = (Student) session.getAttribute("user");
        temporary.setStudentId(student.getStudentId());
        temporary.setStudentName(student.getStudentName());
        temporary.setStudentSex(student.getStudentSex());

        //从session中去找竞赛名
        temporary.setContestName((String) session.getAttribute("contestname"));
        //获取当前时间插入
        temporary.setSignTime(Time.getNow());
        //0表示保存， 1表示非提交
        temporary.setStatus(status);
        temporaryService.save(temporary);
        if (status == 0){
            out.write("<script>alert('保存成功')</script>");
        }else {
            Temporary temporary1 = (Temporary) session.getAttribute("temporary");
            temporaryService.removeById(temporary1.getId());
            out.write("<script>alert('提交成功')</script>");
        }
        return "choose";
    }

    //选择个人赛
    @GetMapping("chooseContest")
    public String chooseContest(String contestname,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws ParseException{

        LambdaQueryWrapper<Contest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contest::getContestName,contestname);
        //选择报名的竞赛
        Contest contest = contestService.getOne(queryWrapper);

        HttpSession session = request.getSession();
        Student student = (Student) session.getAttribute("user");

        //在暂存表中查询是否已经保存或提交
        LambdaQueryWrapper<Temporary> temporaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        temporaryLambdaQueryWrapper.eq(Temporary::getContestName,contestname);
        temporaryLambdaQueryWrapper.eq(Temporary::getStudentId,student.getStudentId());
        temporaryLambdaQueryWrapper.lt(Temporary::getSignTime,contest.getSignEndTime());
        temporaryLambdaQueryWrapper.gt(Temporary::getSignTime,contest.getSignBeginTime());

        Temporary temporary = temporaryService.getOne(temporaryLambdaQueryWrapper);

        //是否已经报名成功了
        LambdaQueryWrapper<Sign> signLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signLambdaQueryWrapper.eq(Sign::getContestName,contestname);
        signLambdaQueryWrapper.eq(Sign::getStudentId,student.getStudentId());
        signLambdaQueryWrapper.lt(Sign::getSignTime,contest.getSignEndTime());
        signLambdaQueryWrapper.gt(Sign::getSignTime,contest.getSignBeginTime());

        Sign sign = signService.getOne(signLambdaQueryWrapper);

        session.setAttribute("contestname",contest.getContestName());
        session.setAttribute("student",student);

        //暂存表中是否已有数据(可能已经报名了，暂未做完)
        if(temporary != null){
            session.setAttribute("temporary",temporary);
            return "checktemporary";
        }else if (sign != null){
            session.setAttribute("sign",sign);
            return "checksign";
        }else {
            System.out.println(123);
            return "temporary";
        }


    }

    //选择团队赛
    @GetMapping("chooseTeamContest")
    public String chooseTeamContest(String contestname,
                                    String status,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws ParseException, IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        LambdaQueryWrapper<Contest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contest::getContestName,contestname);
        //选择报名的竞赛的基本信息
        Contest contest = contestService.getOne(queryWrapper);
        session.setAttribute("contest",contest);
        //提取当前使用的学生信息
        Student student = (Student) session.getAttribute("user");
        System.out.println(student);
        //查询该次竞赛是否已经创建团队
        LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teamLambdaQueryWrapper.eq(Team::getCompetitionName,contestname);
        teamLambdaQueryWrapper.eq(Team::getStudentId,student.getStudentId());
        teamLambdaQueryWrapper.lt(Team::getCreateTeamTime,contest.getSignEndTime());
        teamLambdaQueryWrapper.gt(Team::getCreateTeamTime,contest.getSignBeginTime());

        Team team = teamService.getOne(teamLambdaQueryWrapper);
        if (team != null){
            out.write("<script>alert('您已创建团队')</script>");
            return "choose";
        }

        //查询该次竞赛是否已经加入团队
        LambdaQueryWrapper<Member> memberLambdaQueryWrapper = new LambdaQueryWrapper<>();
        memberLambdaQueryWrapper.eq(Member::getCompetition,contestname);
        memberLambdaQueryWrapper.eq(Member::getStudentId,student.getStudentId());
        memberLambdaQueryWrapper.lt(Member::getJoinTeam,contest.getSignEndTime());
        memberLambdaQueryWrapper.gt(Member::getJoinTeam,contest.getSignBeginTime());

        Member member = memberService.getOne(memberLambdaQueryWrapper);
        if (member != null){
            out.write("<script>alert('您已经加入团队')</script>");
            return "choose";
        }

        session.setAttribute("contestname",contest.getContestName());

        //判断是加入还是创建团队
        if(status.equals("create")){
            return "createTeam";
        }else {
            //先查出所有团队
            LambdaQueryWrapper<Team> queryWrapper1 = new LambdaQueryWrapper<>();

            queryWrapper1.eq(Team::getCompetitionName,contestname);
            queryWrapper1.lt(Team::getCreateTeamTime,contest.getSignEndTime());
            queryWrapper1.gt(Team::getCreateTeamTime,contest.getSignBeginTime());
            queryWrapper1.lt(Team::getNumber,contest.getTeamNumber());

            List<Team> teamList = teamService.list(queryWrapper1);
            session.setAttribute("teamList",teamList);

            return "joinTeam";
        }
    }

    //创建队伍
    @PostMapping("createTeam")
    public String createTeam(
            @RequestParam("contestName") String contestName,
            @RequestParam("teamName") String teamName,
            @RequestParam("teamPassword") int teamPassword,
            HttpServletRequest request,
            HttpServletResponse response)throws ParseException, IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        Team team =new Team();

        team.setStudentId((Integer) session.getAttribute("userId"));
        team.setCompetitionName(contestName);
        team.setTeamName(teamName);
        team.setCreateTeamTime(Time.getNow());
        team.setTeamPassword(teamPassword);
        team.setNumber(1);

        teamService.save(team);
        out.write("<script>alert('创建成功')</script>");
        return "choose";

    }

    //加入队伍准备（拿到团队id）
    @GetMapping("joinTeamReady")
    public String joinTeamReady(
            int id,
            HttpServletRequest request,
            HttpServletResponse response)throws ParseException ,IOException{

        HttpSession session = request.getSession();
        session.setAttribute("team",teamService.getById(id));

        return "writeTeamPassword";
    }


    //加入团队
    @PostMapping("joinTeam")
    public String joinTeam(
            String contestName,
            String teamName,
            int teamPassword,
            HttpServletRequest request,
            HttpServletResponse response)throws ParseException, IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Team team = (Team) session.getAttribute("team");

        if(teamPassword == team.getTeamPassword()){
            Member member =new Member();

            member.setTeamId(team.getId());
            member.setStudentId((Integer) session.getAttribute("userId"));
            member.setCompetition(contestName);
            member.setTeamName(teamName);
            member.setJoinTeam(Time.getNow());

            //团队人数+1
            team.setNumber(team.getNumber()+1);
            teamService.updateById(team);
            memberService.save(member);
            out.write("<script>alert('加入成功')</script>");
            return "choose";
        }else {
            out.write("<script>alert('验证码不正确');history.back();</script>");
            return "choose";
        }


    }

    //展示个人信息
    @GetMapping("showStudent")
    public String showStudent(
            HttpServletRequest request, HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        Student student =(Student) session.getAttribute("user");
        session.setAttribute("user",student);
        return "ownInformation";
    }

    @PostMapping("toupdate")
    public String toupdate(HttpServletRequest request,
            HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        Student student =(Student) session.getAttribute("user");
        session.setAttribute("user",student);
        return "editStudent";
    }

    //修改个人信息
    @PostMapping("updateStudent")
    public String updateStudent(@RequestParam("studentName") String studentName,@RequestParam("studentCollege") String studentCollege,
                                @RequestParam("studentMajor") String studentMajor,@RequestParam("studentGrade")String studentGrade,
                                @RequestParam("studentClass") String studentClass,@RequestParam("studentPhone") String studentPhone,
                                @RequestParam("studentEmail") String studentEmail,HttpServletResponse response, HttpSession session, HttpServletRequest request) throws IOException{

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        Student student = (Student) session.getAttribute("user");
        student.setStudentName(studentName);
        student.setStudentCollege(studentCollege);
        student.setStudentMajor(studentMajor);
        student.setStudentGrade(studentGrade);
        student.setStudentClass(studentClass);
        student.setStudentPhone(studentPhone);
        student.setStudentEmail(studentEmail);

        LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        studentLambdaQueryWrapper.eq(Student::getStudentId,student.getStudentId());
        studentService.update(student,studentLambdaQueryWrapper);
        session.setAttribute("user",student);
        out.write("<script>alert('修改成功')</script>");
        return "ownInformation";

    }

    //先拿到原来的密码
    @GetMapping("updatePassword")
    public String updatePassword(HttpServletRequest request,
                           HttpServletResponse response) throws ParseException{

        HttpSession session = request.getSession();
        Student student =(Student) session.getAttribute("user");
        session.setAttribute("password",student.getStudentPassword());
        return "editPassword";
    }

    @PostMapping("changePassword")
    public String changePassword (
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
            Student student =(Student) session.getAttribute("user");
            student.setStudentPassword(newPassword);

            LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentLambdaQueryWrapper.eq(Student::getStudentId,student.getStudentId());
            studentService.update(student,studentLambdaQueryWrapper);
            session.setAttribute("user",student);
            session.setAttribute("password",newPassword);
            out.write("<script>alert('密码修改成功')</script>");
            return "editPassword";

        }else {
            out.write("<script>alert('前后两次输入密码不一致');history.back();</script>");
            return "editPassword";
        }


    }


    //待审批
    @GetMapping("approvaling")
    public String approvaling(HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();
        LambdaQueryWrapper<Temporary> temporaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        temporaryLambdaQueryWrapper.eq(Temporary::getStudentId,session.getAttribute("userId"));
        temporaryLambdaQueryWrapper.eq(Temporary::getStatus,1);
        List<Temporary> temporaryList = temporaryService.list(temporaryLambdaQueryWrapper);
        session.setAttribute("temporaryList",temporaryList);

        return "waiteApproval";
    }


    //我的个人赛
    @GetMapping("personalcontest")
    public String personalcontest(HttpServletRequest request, HttpSession session){

        LambdaQueryWrapper<Sign> signLambdaQueryWrapper = new LambdaQueryWrapper<>();
        signLambdaQueryWrapper.eq(Sign::getStudentId,session.getAttribute("userId"));
        signLambdaQueryWrapper.orderByAsc(Sign::getSignTime);

        List<Sign> signList = signService.list(signLambdaQueryWrapper);
        session.setAttribute("signList",signList);

        return "personalContest";
    }

    //我的团队赛
    @GetMapping("teamcontest")
    public String teamcontest(HttpServletRequest request, HttpSession session){

        //先找出该用户加入的团队id集合，再加上所创建的团队
        LambdaQueryWrapper<Member> memberLambdaQueryWrapper = new LambdaQueryWrapper<>();
        memberLambdaQueryWrapper.eq(Member::getStudentId,session.getAttribute("userId"));

        List<Member> memberlist = memberService.list(memberLambdaQueryWrapper);
        List<Integer> ids = memberlist.stream().map(Member::getTeamId).collect(Collectors.toList());

        LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ids.isEmpty()){
            teamLambdaQueryWrapper.eq(Team::getStudentId,session.getAttribute("userId"));
            teamLambdaQueryWrapper.orderByAsc(Team::getCreateTeamTime);
        }else {
            teamLambdaQueryWrapper.eq(Team::getStudentId,session.getAttribute("userId")).or().in(Team::getId,ids);
            teamLambdaQueryWrapper.orderByAsc(Team::getCreateTeamTime);
        }


        List<Team> teamList = teamService.list(teamLambdaQueryWrapper);
        session.setAttribute("teamList",teamList);
        session.setAttribute("userId",session.getAttribute("userId"));
        return "teamContest";
    }

    //查看该团队的成员
    @GetMapping("checkmember")
    public String checkmember(int id ,HttpServletRequest request, HttpSession session){
        LambdaQueryWrapper<Member> memberLambdaQueryWrapper = new LambdaQueryWrapper<>();
        memberLambdaQueryWrapper.eq(Member::getTeamId,id);

        List<Member> memberlist = memberService.list(memberLambdaQueryWrapper);
        session.setAttribute("memberlist",memberlist);
        return "teamMembers";
    }

    //退出队伍
    @GetMapping("outTeam")
    public String outTeam(int id ,HttpServletRequest request, HttpSession session,HttpServletResponse response)throws IOException{
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LambdaQueryWrapper<Member> memberLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        memberLambdaQueryWrapper1.eq(Member::getTeamId,id);
        memberLambdaQueryWrapper1.eq(Member::getStudentId,session.getAttribute("userId"));
        memberService.remove(memberLambdaQueryWrapper1);

        Team team = teamService.getById(id);
        team.setNumber(team.getNumber()-1);
        teamService.updateById(team);
        teamcontest(request, session);
        out.write("<script>alert('成功退出')</script>");
        return "teamContest";

    }

    //踢出队伍
    @GetMapping("outOfTeam")
    public String outOfTeam(int id ,HttpServletRequest request, HttpSession session,HttpServletResponse response)throws IOException{
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        Member member = memberService.getById(id);

        Team team = teamService.getById(member.getTeamId());
        team.setNumber(team.getNumber()-1);
        teamService.updateById(team);
        teamcontest(request, session);

        memberService.removeById(id);
        out.write("<script>alert('已踢出队伍')</script>");
        return "teamContest";

    }

    //我的队伍
    @GetMapping("myTeam")
    public String myTeam(HttpServletRequest request, HttpSession session){

        LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teamLambdaQueryWrapper.eq(Team::getStudentId,session.getAttribute("userId"));
        teamLambdaQueryWrapper.orderByAsc(Team::getCreateTeamTime);

        List<Team> teamList = teamService.list(teamLambdaQueryWrapper);
        session.setAttribute("teamList",teamList);
        return "destoryTeam";

    }

    //解散队伍
    @GetMapping("dissolutionTeam")
    public String dissolutionTeam(int id ,HttpServletRequest request, HttpSession session,HttpServletResponse response)throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LambdaQueryWrapper<Member> memberLambdaQueryWrapper = new LambdaQueryWrapper<>();
        memberLambdaQueryWrapper.eq(Member::getTeamId,id);

        List<Member> memberList = memberService.list(memberLambdaQueryWrapper);

        if (memberList.isEmpty()){
            teamService.removeById(id);

            LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teamLambdaQueryWrapper.eq(Team::getStudentId,session.getAttribute("userId"));
            teamLambdaQueryWrapper.orderByAsc(Team::getCreateTeamTime);
            List<Team> teamList = teamService.list(teamLambdaQueryWrapper);
            session.setAttribute("teamList",teamList);
            out.write("<script>alert('队伍已解散')</script>");
            return "teamContest";
        }else {
            out.write("<script>alert('当前队伍还有成员');history.back();</script>");
            return "teamContest";
        }

    }
}

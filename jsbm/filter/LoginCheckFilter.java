package com.jsbm.filter;

import com.alibaba.fastjson.JSON;
import com.jsbm.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("utf-8");

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
                "/userInfo/login",
                "/teacherInfo/login",

                "/userInfo/pages/**",
                "/userInfo/img/**",
                "/userInfo/js/**",
                "/userInfo/mycss/**",
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3、如果不需要处理，则直接放行
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
        //4、判断学生登录状态，若已登录，则直接放行
        if (request.getSession().getAttribute("userId") != null){
            int userId = (int) request.getSession().getAttribute("userId");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        //5、判断教师登录状态，若已登录，则直接放行
        if (request.getSession().getAttribute("teacherId") != null){
            int teacherId = (int) request.getSession().getAttribute("teacherId");
            BaseContext.setCurrentId(teacherId);

            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录的结果，通过输出流方式向客户端页面响应数据
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("<script>alert('请先登录');history.back();</script>");
        System.out.println("拦截器失效");
        return;
    }


    public boolean check(String[] urls, String requestURI){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }

        }
        return false;
    }
}

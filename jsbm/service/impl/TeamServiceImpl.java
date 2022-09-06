package com.jsbm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsbm.entity.Team;
import com.jsbm.mapper.TeamMapper;
import com.jsbm.service.TeamService;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
}

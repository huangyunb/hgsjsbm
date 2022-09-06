package com.jsbm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsbm.entity.Contest;
import com.jsbm.mapper.ContestMapper;
import com.jsbm.service.ContestService;
import org.springframework.stereotype.Service;

@Service
public class ContestServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestService {
}

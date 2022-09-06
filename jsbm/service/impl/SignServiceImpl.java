package com.jsbm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsbm.entity.Sign;
import com.jsbm.mapper.SignMapper;
import com.jsbm.service.SignService;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign> implements SignService {
}

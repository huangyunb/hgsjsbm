package com.jsbm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsbm.entity.Temporary;
import com.jsbm.mapper.TemporaryMapper;
import com.jsbm.service.TemporaryService;
import org.springframework.stereotype.Service;

@Service
public class TemporaryServiceImpl extends ServiceImpl<TemporaryMapper, Temporary> implements TemporaryService {
}

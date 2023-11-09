package com.aiyichen.admindemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiyichen.admindemo.entity.Tag;
import com.aiyichen.admindemo.service.TagService;
import com.aiyichen.admindemo.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}





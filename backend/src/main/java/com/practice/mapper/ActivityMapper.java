package com.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.practice.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}

package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author cuiyue
 * @email 380920705@qq.coom
 * @date 2020-05-14 09:57:12
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

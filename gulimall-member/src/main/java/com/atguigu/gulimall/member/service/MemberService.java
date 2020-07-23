package com.atguigu.gulimall.member.service;

import com.atguigu.gulimall.member.exception.PhoneExsitException;
import com.atguigu.gulimall.member.exception.UsernameExsitException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author cuiyue
 * @email 380920705@qq.coom
 * @date 2020-05-14 09:57:12
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo memberRegistVo);

    void checkPhoneUnique(String phone) throws PhoneExsitException;

    void checkUsernameUnique(String username) throws UsernameExsitException;

    MemberEntity login(MemberLoginVo memberLoginVo);
}


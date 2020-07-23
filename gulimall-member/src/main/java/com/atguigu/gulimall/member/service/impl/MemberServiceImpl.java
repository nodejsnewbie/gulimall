package com.atguigu.gulimall.member.service.impl;

import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExsitException;
import com.atguigu.gulimall.member.exception.UsernameExsitException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo memberRegistVo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = new MemberEntity();

        //检查用户名和手机是否唯一
        checkPhoneUnique(memberRegistVo.getPhone());
        checkUsernameUnique(memberRegistVo.getUserName());

        //设置密码
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(memberRegistVo.getPassword());
        memberEntity.setPassword(encode);

        //设置会员等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setMobile(memberRegistVo.getPhone());
        memberEntity.setUsername(memberRegistVo.getUserName());

        //保存数据库
        memberDao.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExsitException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExsitException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExsitException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UsernameExsitException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        String loginacct = memberLoginVo.getLoginacct();
        String password = memberLoginVo.getPassword();
        // select * from ums_member where username=? or mobile=?
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if (memberEntity != null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, memberEntity.getPassword());
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }
        }
        return null;
    }

}
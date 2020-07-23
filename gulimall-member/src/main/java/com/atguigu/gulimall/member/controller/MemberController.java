package com.atguigu.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.gulimall.member.exception.PhoneExsitException;
import com.atguigu.gulimall.member.exception.UsernameExsitException;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 会员
 *
 * @author cuiyue
 * @email 380920705@qq.coom
 * @date 2020-05-14 09:57:12
 */
@RestController
@RequestMapping("member/member")
@RefreshScope
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Value("${gulimall.user.name}")
    private String name;

    @Value("${gulimall.user.age}")
    private Integer age;


    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo) {
        MemberEntity memberEntity = memberService.login(memberLoginVo);
        if (memberEntity == null) {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        } else {
            return R.ok();
        }
    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo memberRegistVo) {
        try {
            memberService.regist(memberRegistVo);
        } catch (PhoneExsitException e) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExsitException e) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    /**
     * 测试nacos的config
     */
    @RequestMapping("/config")
    public R testConfig() {
        return R.ok().put("name", name).put("age", age);
    }

    /**
     * 测试feign调用
     */
    @RequestMapping("/coupon")
    public R testCoupon() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R memberCoupon = couponFeignService.memberCoupon();
        return R.ok().put("member", memberEntity).put("coupons", memberCoupon.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

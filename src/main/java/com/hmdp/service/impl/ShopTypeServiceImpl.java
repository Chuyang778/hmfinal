package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        String key = RedisConstants.CACHE_TYPE_KEY;
        String typeJSON = stringRedisTemplate.opsForValue().get(key);
        //如果存在直接返回
        if (StrUtil.isNotBlank(typeJSON)) {
            return Result.ok(JSONUtil.toList(typeJSON,ShopType.class));
        }
        LambdaQueryWrapper<ShopType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ShopType::getSort);
        List<ShopType> shopTypeList = this.list(queryWrapper);
        if(shopTypeList == null){
            return Result.fail("给店铺类型不存在!");
        }
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shopTypeList));
        return Result.ok(shopTypeList);
    }
}

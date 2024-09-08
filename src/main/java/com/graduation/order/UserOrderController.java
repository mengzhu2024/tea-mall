package com.graduation.order;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.graduation.car.ShoppingCarDO;
import com.graduation.car.ShoppingCarMapper;
import com.graduation.common.LoginUtil;
import com.graduation.common.ObjectTransfer;
import com.graduation.common.Result;
import com.graduation.goods.GoodsDO;
import com.graduation.goods.GoodsMapper;
import com.graduation.user.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-order")
public class UserOrderController {

    @Resource
    private UserOrderMapper userOrderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ShoppingCarMapper shoppingCarMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;

    @ResponseBody
    @PostMapping("/query")
    public Result<List<UserOrderModel>> query(HttpServletRequest request, @RequestBody UserOrderQuery query) {
        UserDO loginer = LoginUtil.loginer(request);
        if (Objects.isNull(loginer)) {
            return new Result<>(new ArrayList<>());
        }
        if ("用户".equals(loginer.getRole())) {
            query.setUserId(loginer.getId());
        }
        return new Result<>(userOrderMapper.selectJoinList(UserOrderDO.class, getQueryWrapper(query)).stream()
                .map(e -> {
                    UserOrderModel model = ObjectTransfer.transfer(e, UserOrderModel.class);
                    model.setDetail(orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetailDO>().eq(OrderDetailDO::getOrderId, e.getId())));
                    return model;
                })
                .collect(Collectors.toList()));
    }

    @ResponseBody
    @PostMapping("/page")
    public Result<IPage<UserOrderModel>> page(HttpServletRequest request, @RequestBody UserOrderQuery query) {
        IPage<UserOrderDO> page = new Page<>(query.getCurrent(), 10);
        UserDO loginer = LoginUtil.loginer(request);
        if (Objects.isNull(loginer)) {
            return new Result<>(new Page<>());
        }
        if ("用户".equals(loginer.getRole())) {
            query.setUserId(loginer.getId());
        }
        userOrderMapper.selectJoinPage(page, UserOrderDO.class, getQueryWrapper(query));
        IPage<UserOrderModel> result = new Page<>();
        result.setPages((int) page.getPages());
        result.setCurrent((int) Math.min(page.getCurrent(), page.getPages()));
        result.setTotal((int) page.getTotal());
        Map<Integer, UserModel> userMap = userMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .map(e -> ObjectTransfer.transfer(e, UserModel.class))
                .collect(Collectors.toMap(UserModel::getId, Function.identity()));
        result.setRecords(page.getRecords().stream().map(e -> {
            UserOrderModel model = ObjectTransfer.transfer(e, UserOrderModel.class);
            model.setUser(userMap.get(e.getUserId()));
            model.setDetail(orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetailDO>().eq(OrderDetailDO::getOrderId, e.getId())));
            return model;
        }).collect(Collectors.toList()));
        return new Result<>(result);
    }

    @ResponseBody
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> create(HttpServletRequest request, @RequestBody UserOrderModel model) {
        UserDO loginer = LoginUtil.loginer(request);
        // 查询购物车数据
        List<ShoppingCarDO> shoppingCarList = shoppingCarMapper.selectList(new LambdaQueryWrapper<ShoppingCarDO>()
                .eq(ShoppingCarDO::getUserId, loginer.getId()));
        // 查询商品信息校验库存
        List<GoodsDO> goodsList = new ArrayList<>();
        List<OrderDetailDO> orderDetailList = new ArrayList<>();
        for (ShoppingCarDO shoppingCar: shoppingCarList) {
            GoodsDO goods = goodsMapper.selectById(shoppingCar.getGoodsId());
            goods.setStoreNum(goods.getStoreNum() - shoppingCar.getNum());
            if (goods.getStoreNum() < 0) {
                return Result.failed("【"+goods.getName()+"】库存不足");
            }
            goodsList.add(goods);
            OrderDetailDO orderDetail = new OrderDetailDO();
            orderDetail.setGoodsId(goods.getId());
            orderDetail.setPrice(goods.getPrice());
            orderDetail.setNum(shoppingCar.getNum());
            orderDetail.setGoodsName(goods.getName());
            orderDetail.setImgUrl(goods.getImgUrl());
            orderDetailList.add(orderDetail);
        }

        // 修改库存
        for (GoodsDO goods: goodsList) {
            goodsMapper.updateById(goods);
        }

        // 添加订单
        UserOrderDO userOrderDO = ObjectTransfer.transfer(model, UserOrderDO.class);
        userOrderDO.setUserId(loginer.getId());
        userOrderDO.setStatus("待支付");
        userOrderDO.setCreateTime(LocalDateTime.now());
        userOrderMapper.insert(userOrderDO);

        // 添加订单明细
        for (OrderDetailDO orderDetail: orderDetailList) {
            orderDetail.setOrderId(userOrderDO.getId());
            orderDetailMapper.insert(orderDetail);
        }

        // 清空购物车
        shoppingCarMapper.delete(new LambdaQueryWrapper<ShoppingCarDO>().eq(ShoppingCarDO::getUserId, loginer.getId()));
        return Result.success();
    }

    @ResponseBody
    @PostMapping("/update")
    public Result<?> update(@RequestBody UserOrderModel model) {
        UserOrderDO userOrderDO = ObjectTransfer.transfer(model, UserOrderDO.class);
        userOrderMapper.updateById(userOrderDO);
        return Result.success();
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public Result<?> delete(Integer id) {
        userOrderMapper.deleteById(id);
        return Result.success();
    }

    private MPJLambdaWrapper<UserOrderDO> getQueryWrapper(UserOrderQuery query) {
        return new MPJLambdaWrapper<UserOrderDO>()
                .selectAll(UserOrderDO.class)
                .eq(Objects.nonNull(query.getId()), UserOrderDO::getId, query.getId())
                .eq(Objects.nonNull(query.getUserId()), UserOrderDO::getUserId, query.getUserId())
                .like(StrUtil.isNotBlank(query.getPrice()), UserOrderDO::getPrice, query.getPrice())
                .like(StrUtil.isNotBlank(query.getPayType()), UserOrderDO::getPayType, query.getPayType())
                .like(StrUtil.isNotBlank(query.getReceiver()), UserOrderDO::getReceiver, query.getReceiver())
                .like(StrUtil.isNotBlank(query.getPhone()), UserOrderDO::getPhone, query.getPhone())
                .like(StrUtil.isNotBlank(query.getAddress()), UserOrderDO::getAddress, query.getAddress())
                .like(StrUtil.isNotBlank(query.getStatus()), UserOrderDO::getStatus, query.getStatus())
                ;
    }
}

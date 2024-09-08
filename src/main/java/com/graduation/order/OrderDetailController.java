package com.graduation.order;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.graduation.common.ObjectTransfer;
import com.graduation.common.Result;
import com.graduation.goods.GoodsMapper;
import com.graduation.goods.GoodsModel;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order-detail")
public class OrderDetailController {

    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private UserOrderMapper userOrderMapper;
    @Resource
    private GoodsMapper goodsMapper;

    @ResponseBody
    @PostMapping("/query")
    public Result<List<OrderDetailModel>> query(@RequestBody OrderDetailQuery query) {
        return new Result<>(orderDetailMapper.selectJoinList(OrderDetailDO.class, getQueryWrapper(query)).stream()
                .map(e -> ObjectTransfer.transfer(e, OrderDetailModel.class))
                .collect(Collectors.toList()));
    }

    @ResponseBody
    @PostMapping("/page")
    public Result<IPage<OrderDetailModel>> page(@RequestBody OrderDetailQuery query) {
        IPage<OrderDetailDO> page = new Page<>(query.getCurrent(), 10);
        orderDetailMapper.selectJoinPage(page, OrderDetailDO.class, getQueryWrapper(query));
        IPage<OrderDetailModel> result = new Page<>();
        result.setPages((int) page.getPages());
        result.setCurrent((int) Math.min(page.getCurrent(), page.getPages()));
        result.setTotal((int) page.getTotal());
        Map<Integer, UserOrderModel> userOrderMap = userOrderMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .map(e -> ObjectTransfer.transfer(e, UserOrderModel.class))
                .collect(Collectors.toMap(UserOrderModel::getId, Function.identity()));
        Map<Integer, GoodsModel> goodsMap = goodsMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .map(e -> ObjectTransfer.transfer(e, GoodsModel.class))
                .collect(Collectors.toMap(GoodsModel::getId, Function.identity()));
        result.setRecords(page.getRecords().stream().map(e -> {
            OrderDetailModel model = ObjectTransfer.transfer(e, OrderDetailModel.class);
            model.setOrder(userOrderMap.get(e.getOrderId()));
            model.setGoods(goodsMap.get(e.getGoodsId()));
            return model;
        }).collect(Collectors.toList()));
        return new Result<>(result);
    }

    @ResponseBody
    @PostMapping("/add")
    public Result<?> add(@RequestBody OrderDetailModel model) {
        OrderDetailDO orderDetailDO = ObjectTransfer.transfer(model, OrderDetailDO.class);
        orderDetailMapper.insert(orderDetailDO);
        return Result.success();
    }

    @ResponseBody
    @PostMapping("/update")
    public Result<?> update(@RequestBody OrderDetailModel model) {
        OrderDetailDO orderDetailDO = ObjectTransfer.transfer(model, OrderDetailDO.class);
        orderDetailMapper.updateById(orderDetailDO);
        return Result.success();
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public Result<?> delete(Integer id) {
        orderDetailMapper.deleteById(id);
        return Result.success();
    }

    private MPJLambdaWrapper<OrderDetailDO> getQueryWrapper(OrderDetailQuery query) {
        return new MPJLambdaWrapper<OrderDetailDO>()
                .selectAll(OrderDetailDO.class)
                .eq(Objects.nonNull(query.getId()), OrderDetailDO::getId, query.getId())
                .eq(Objects.nonNull(query.getOrderId()), OrderDetailDO::getOrderId, query.getOrderId())
                .eq(Objects.nonNull(query.getGoodsId()), OrderDetailDO::getGoodsId, query.getGoodsId())
                .like(StrUtil.isNotBlank(query.getGoodsName()), OrderDetailDO::getGoodsName, query.getGoodsName())
                .eq(Objects.nonNull(query.getNum()), OrderDetailDO::getNum, query.getNum())
                .like(StrUtil.isNotBlank(query.getPrice()), OrderDetailDO::getPrice, query.getPrice())
                ;
    }
}

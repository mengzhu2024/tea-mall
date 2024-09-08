package com.graduation.goods;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.graduation.common.ObjectTransfer;
import com.graduation.common.Result;
import com.graduation.user.UserDO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsTypeMapper goodsTypeMapper;

    @ResponseBody
    @PostMapping("/query")
    public Result<List<GoodsModel>> query(@RequestBody GoodsQuery query) {
        return new Result<>(goodsMapper.selectJoinList(GoodsDO.class, getQueryWrapper(query)).stream()
                .map(e -> ObjectTransfer.transfer(e, GoodsModel.class))
                .collect(Collectors.toList()));
    }

    @ResponseBody
    @PostMapping("/page")
    public Result<IPage<GoodsModel>> page(@RequestBody GoodsQuery query) {
        IPage<GoodsDO> page = new Page<>(query.getCurrent(), 10);
        goodsMapper.selectJoinPage(page, GoodsDO.class, getQueryWrapper(query));
        IPage<GoodsModel> result = new Page<>();
        result.setPages((int) page.getPages());
        result.setCurrent((int) Math.min(page.getCurrent(), page.getPages()));
        result.setTotal((int) page.getTotal());
        Map<Integer, GoodsTypeModel> goodsTypeMap = goodsTypeMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .map(e -> ObjectTransfer.transfer(e, GoodsTypeModel.class))
                .collect(Collectors.toMap(GoodsTypeModel::getId, Function.identity()));
        result.setRecords(page.getRecords().stream().map(e -> {
            GoodsModel model = ObjectTransfer.transfer(e, GoodsModel.class);
            model.setType(goodsTypeMap.get(e.getTypeId()));
            return model;
        }).collect(Collectors.toList()));
        return new Result<>(result);
    }

    @ResponseBody
    @PostMapping("/add")
    public Result<?> add(@RequestBody GoodsModel model) {
        GoodsDO goodsDO = ObjectTransfer.transfer(model, GoodsDO.class);
        goodsDO.setCreateTime(LocalDateTime.now());
        goodsMapper.insert(goodsDO);
        return Result.success();
    }

    @ResponseBody
    @PostMapping("/update")
    public Result<?> update(@RequestBody GoodsModel model) {
        GoodsDO goodsDO = ObjectTransfer.transfer(model, GoodsDO.class);
        goodsMapper.updateById(goodsDO);
        return Result.success();
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public Result<?> delete(Integer id) {
        goodsMapper.deleteById(id);
        return Result.success();
    }

    private MPJLambdaWrapper<GoodsDO> getQueryWrapper(GoodsQuery query) {
        return new MPJLambdaWrapper<GoodsDO>()
                .selectAll(GoodsDO.class)
                .eq(Objects.nonNull(query.getId()), GoodsDO::getId, query.getId())
                .like(StrUtil.isNotBlank(query.getName()), GoodsDO::getName, query.getName())
                .eq(Objects.nonNull(query.getTypeId()), GoodsDO::getTypeId, query.getTypeId())
                .like(StrUtil.isNotBlank(query.getImgUrl()), GoodsDO::getImgUrl, query.getImgUrl())
                .like(StrUtil.isNotBlank(query.getStatus()), GoodsDO::getStatus, query.getStatus())
                .eq(Objects.nonNull(query.getStoreNum()), GoodsDO::getStoreNum, query.getStoreNum())
                ;
    }
}

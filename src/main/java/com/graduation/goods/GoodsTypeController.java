package com.graduation.goods;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.graduation.common.ObjectTransfer;
import com.graduation.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goods-type")
public class GoodsTypeController {

    @Resource
    private GoodsTypeMapper goodsTypeMapper;

    @ResponseBody
    @PostMapping("/query")
    public Result<List<GoodsTypeModel>> query(@RequestBody GoodsTypeQuery query) {
        return new Result<>(goodsTypeMapper.selectJoinList(GoodsTypeDO.class, getQueryWrapper(query)).stream()
                .map(e -> ObjectTransfer.transfer(e, GoodsTypeModel.class))
                .collect(Collectors.toList()));
    }

    @ResponseBody
    @PostMapping("/page")
    public Result<IPage<GoodsTypeModel>> page(@RequestBody GoodsTypeQuery query) {
        IPage<GoodsTypeDO> page = new Page<>(query.getCurrent(), 10);
        goodsTypeMapper.selectJoinPage(page, GoodsTypeDO.class, getQueryWrapper(query));
        IPage<GoodsTypeModel> result = new Page<>();
        result.setPages((int) page.getPages());
        result.setCurrent((int) Math.min(page.getCurrent(), page.getPages()));
        result.setTotal((int) page.getTotal());
        result.setRecords(page.getRecords().stream().map(e -> {
            GoodsTypeModel model = ObjectTransfer.transfer(e, GoodsTypeModel.class);
            return model;
        }).collect(Collectors.toList()));
        return new Result<>(result);
    }

    @ResponseBody
    @PostMapping("/add")
    public Result<?> add(@RequestBody GoodsTypeModel model) {
        GoodsTypeDO goodsTypeDO = ObjectTransfer.transfer(model, GoodsTypeDO.class);
        goodsTypeMapper.insert(goodsTypeDO);
        return Result.success();
    }

    @ResponseBody
    @PostMapping("/update")
    public Result<?> update(@RequestBody GoodsTypeModel model) {
        GoodsTypeDO goodsTypeDO = ObjectTransfer.transfer(model, GoodsTypeDO.class);
        goodsTypeMapper.updateById(goodsTypeDO);
        return Result.success();
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public Result<?> delete(Integer id) {
        goodsTypeMapper.deleteById(id);
        return Result.success();
    }

    private MPJLambdaWrapper<GoodsTypeDO> getQueryWrapper(GoodsTypeQuery query) {
        return new MPJLambdaWrapper<GoodsTypeDO>()
                .selectAll(GoodsTypeDO.class)
                .eq(Objects.nonNull(query.getId()), GoodsTypeDO::getId, query.getId())
                .like(StrUtil.isNotBlank(query.getName()), GoodsTypeDO::getName, query.getName())
                ;
    }
}

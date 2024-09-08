package com.graduation.goods;

public class GoodsTypeQuery {

    // 查询页码
    private Integer current = 1;

    //id
    private Integer id;

    //分类名称
    private String name;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

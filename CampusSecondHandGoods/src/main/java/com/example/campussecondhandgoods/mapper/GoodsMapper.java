package com.example.campussecondhandgoods.mapper;

import com.example.campussecondhandgoods.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {
    List<Goods> listByCategory(int category);
    Goods detail(int id);
    int add(Goods goods);
    List<Goods> myGoods(int userId);
    List<Goods> listAll();
    int delete(Map<String,Integer> params);
    // 搜索商品
    List<Goods> search(String keyword);
    int update(Goods goods);
    int updateStatus(int id, int userId, int status);
    void incrementViewCount(int id);
}

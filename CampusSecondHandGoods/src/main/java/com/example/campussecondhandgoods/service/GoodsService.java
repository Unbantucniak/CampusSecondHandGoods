package com.example.campussecondhandgoods.service;

import com.example.campussecondhandgoods.entity.Goods;
import com.example.campussecondhandgoods.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Service
public class GoodsService {
    private static final String DEFAULT_GOODS_IMAGE = "https://picsum.photos/seed/campus-goods/600/400";
    @Autowired
    private GoodsMapper goodsMapper;

    //查分类
    public List<Goods> getGoodsByCategory(int category) {
        List<Goods> goods = goodsMapper.listByCategory(category);
        goods.forEach(this::ensureDefaultImage);
        return goods;
    }

    //查商品详情
    public Goods getGoodsDetail(int id) {
        // 增加浏览量
        goodsMapper.incrementViewCount(id);
        
        Goods goods = goodsMapper.detail(id);
        if (goods != null && (goods.getImgUrl() == null || goods.getImgUrl().trim().isEmpty())) {
            goods.setImgUrl(DEFAULT_GOODS_IMAGE);
        }
        return goods;
    }

    // 更新状态
    public int updateStatus(int id, int userId, int status) {
        return goodsMapper.updateStatus(id, userId, status);
    }

    //发布商品
    public int addGoods(Goods goods) {
        if (goods.getImgUrl() == null || goods.getImgUrl().trim().isEmpty()) {
            goods.setImgUrl(DEFAULT_GOODS_IMAGE);
        }
        return goodsMapper.add(goods);
    }

    //查我的发布
    public List<Goods> getMyGoods(int userId) {
        List<Goods> goods = goodsMapper.myGoods(userId);
        goods.forEach(this::ensureDefaultImage);
        return goods;
    }

    public List<Goods> getAllGoods() {
        List<Goods> goods = goodsMapper.listAll();
        goods.forEach(this::ensureDefaultImage);
        return goods;
    }

    //删除商品
    public int deleteGoods(Map<String, Integer> params) {
        return goodsMapper.delete(params);
    }

    //搜索商品
    public List<Goods> searchGoods(String keyword) {
        List<Goods> goods = goodsMapper.search(keyword);
        goods.forEach(this::ensureDefaultImage);
        return goods;
    }

    //更新商品
    public int updateGoods(Goods goods) {
        if (goods.getImgUrl() == null || goods.getImgUrl().trim().isEmpty()) {
            goods.setImgUrl(DEFAULT_GOODS_IMAGE);
        }
        return goodsMapper.update(goods);
    }

    private void ensureDefaultImage(Goods goods) {
        if (goods != null && (goods.getImgUrl() == null || goods.getImgUrl().trim().isEmpty())) {
            goods.setImgUrl(DEFAULT_GOODS_IMAGE);
        }
    }
}

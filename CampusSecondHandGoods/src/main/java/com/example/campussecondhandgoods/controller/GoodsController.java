package com.example.campussecondhandgoods.controller;

import com.example.campussecondhandgoods.entity.Goods;
import com.example.campussecondhandgoods.service.GoodsService;
import com.example.campussecondhandgoods.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserService userService;

    //查商品列表，URL传物品类型
    @GetMapping("/list")
    public Map<String,Object> getGoodsList(@RequestParam int category, @RequestParam(required = false) String sort){
        Map<String,Object> result=new HashMap<>();
        List<Goods> goodsList=applySort(goodsService.getGoodsByCategory(category), sort);
        result.put("code",200);
        result.put("msg","查询成功");
        result.put("data",goodsList);
        return result;
    }

    //查商品详情,URL传商品id
    @GetMapping("/detail")
    public Map<String,Object> getGoodsDetail(@RequestParam int id){
        Map<String,Object> result=new HashMap<>();
        Goods goods= goodsService.getGoodsDetail(id);
        result.put("code",200);
        result.put("msg","查询成功");
        result.put("data",goods);
        return result;
    }

    //发布商品,接受JSON格式的商品信息
    @PostMapping("/add")
    public Map<String,Object> addGoods(@RequestBody Goods goods){
        Map<String,Object> result=new HashMap<>();
        int count=goodsService.addGoods(goods);
        if(count>0){
            result.put("code",200);
            result.put("msg","发布成功");
        }
        else{
            result.put("code",400);{}
            result.put("msg","发布失败");
        }
        return result;
    }

    //更新商品
    @PostMapping("/update")
    public Map<String,Object> updateGoods(@RequestBody Goods goods){
        Map<String,Object> result=new HashMap<>();
        int count=goodsService.updateGoods(goods);
        if(count>0){
            result.put("code",200);
            result.put("msg","更新成功");
        }
        else{
            result.put("code",400);
            result.put("msg","更新失败");
        }
        return result;
    }

    //更新状态
    @PostMapping("/status")
    public Map<String,Object> updateStatus(@RequestBody Map<String, Integer> params){
        Map<String,Object> result=new HashMap<>();
        int id = params.get("id");
        int userId = params.get("userId");
        int status = params.get("status");
        
        int count = goodsService.updateStatus(id, userId, status);
        if(count>0){
            result.put("code",200);
            result.put("msg","状态更新成功");
        }
        else{
            result.put("code",400);
            result.put("msg","状态更新失败");
        }
        return result;
    }

    //查询我的商品
    @GetMapping("/my")
    public Map<String,Object> getMyGoods(@RequestParam int userId){
        Map<String,Object> result=new HashMap<>();
        List<Goods> myGoods=goodsService.getMyGoods(userId);
        result.put("code",200);
        result.put("msg","查询成功");
        result.put("data",myGoods);
        return result;
    }

    @GetMapping("/listAll")
    public Map<String,Object> getAllGoods(@RequestParam(required = false) String sort){
        Map<String,Object> result=new HashMap<>();
        List<Goods> allGoods=applySort(goodsService.getAllGoods(), sort);
        result.put("code",200);
        result.put("msg","查询成功");
        result.put("data",allGoods);
        return result;
    }

    //删除商品
    @DeleteMapping("/delete")
    public Map<String,Object> deleteGoods(@RequestParam int id,@RequestParam int userId){
        Map<String,Object> result=new HashMap<>();
        Map<String,Integer> params=new HashMap<>();
        params.put("id",id);
        params.put("userId",userId);
        int count=goodsService.deleteGoods(params);
        if(count>0){
            result.put("code",200);
            result.put("msg","删除成功");
        }
        else{
            result.put("code",400);
            result.put("msg","删除失败");
        }
        return result;
    }

    @GetMapping("/search")
    public Map<String,Object> searchGoods(@RequestParam String keyword, @RequestParam(required = false) String sort){
        Map<String,Object> result=new HashMap<>();
        List<Goods> goodsList=applySort(goodsService.searchGoods(keyword), sort);
        result.put("code",200);
        result.put("msg","查询成功");
        result.put("data",goodsList);
        return result;
    }

    private List<Goods> applySort(List<Goods> goods, String sort) {
        if (goods == null || sort == null) {
            return goods;
        }
        switch (sort) {
            case "priceAsc":
                goods.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "priceDesc":
                goods.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
            default:
                break;
        }
        return goods;
    }
}

package io.github.tesla.backend.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Maps;

@RestController
public class OrderController {

    static Map<String, String> nameMap = Maps.newHashMap();
    static Map<String, String> provincesMap = Maps.newHashMap();
    static Map<String, String> orderMap = Maps.newHashMap();

    static {
        nameMap.put("1", "张三");
        nameMap.put("2", "李四");
        nameMap.put("3", "王五");

        provincesMap.put("1", "上海");
        provincesMap.put("2", "北京");
        provincesMap.put("3", "河南");

        orderMap.put("1", "租房订单");
        orderMap.put("2", "买房订单");
        orderMap.put("3", "装修订单");

    }

    @PostMapping("/queryName/{cusid}")
    @ResponseBody
    public Map queryNameForPost(@PathVariable("cusid") String id) {
        Map<String, String> map = Maps.newHashMap();
        map.put("name", nameMap.get(id));
        return map;
    }

    @GetMapping("/queryName")
    @ResponseBody
    public Map queryNameForGet(@RequestParam("cusid") String id) {
        Map<String, String> map = Maps.newHashMap();
        map.put("name", nameMap.get(id));
        return map;
    }

    @RequestMapping("/queryProvinces")
    @ResponseBody
    public Map queryProvinces(@RequestParam("cusid") String id) {
        Map<String, String> map = Maps.newHashMap();
        map.put("provinces", provincesMap.get(id));
        return map;
    }

    @RequestMapping("/queryOrder")
    @ResponseBody
    public Map queryOrder(@RequestParam("cusid") String id) {
        Map<String, String> map = Maps.newHashMap();
        map.put("orderType", orderMap.get(id));
        map.put("orderTime", new Date().toString());
        return map;
    }

}
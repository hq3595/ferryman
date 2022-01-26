package com.dahua.springmvcdemo.controller;

import com.dahua.ferryman.client.FerrymanInvoker;
import com.dahua.ferryman.client.FerrymanService;
import com.dahua.ferryman.client.Protocol;
import com.dahua.springmvcdemo.pojo.TestEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/24 下午9:13
 */
@RestController
@FerrymanService(patternPath = "/test*", protocol = Protocol.HTTP, serviceId = "hello")
public class DemoController {

    private volatile int count;

    @FerrymanInvoker(path = "/testGet")
    @GetMapping("/testGet")
    public String testGet() {
        return "testGet";
    }

    @FerrymanInvoker(path = "/testPost")
    @PostMapping("/testPost")
    public String testPost() {
        count++;
        if(count >= 5) {
            System.err.println("<------ ferryman: ------>");
            count = 0;
        }
        throw new RuntimeException();
//        try{
//            Thread.sleep(5000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return "ferryman";
    }

    @FerrymanInvoker(path = "/testParam")
    @RequestMapping("/testParam")
    public String testParam(@RequestParam String name) {
        count++;
        if(count >= 1e5) {
            System.err.println("<------ testParam收到请求, name:" + name + " ------>");
            count = 0;
        }
        return name;
    }

    @FerrymanInvoker(path = "/testEntity")
    @RequestMapping("/testEntity")
    public String testEntity(@RequestBody TestEntity testEntity) {
        return "testEntity result :" + testEntity.getName() + testEntity.getAge();
    }

}

package org.erywim.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.erywim.entities.PayDTO;
import org.erywim.resp.ResultData;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Erywim 2024/7/31
 */
@RestController
@Slf4j
@RequestMapping("/order/pay")
public class OrderController {

    private static final String PAYMENT_SRV_URL = "http://localhost:8001/pay/";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/add")
    public ResultData<String> addOrder(PayDTO dto) {
        return restTemplate.postForObject(PAYMENT_SRV_URL + "add", dto, ResultData.class);
    }

    @GetMapping("get/{id}")
    public ResultData<PayDTO> getOrder(@PathVariable(name = "id") Integer id) {
        log.info("get order by id {}", id);
        return restTemplate.getForObject(PAYMENT_SRV_URL + "get/" + id, ResultData.class);
    }

    @GetMapping("getAll")
    public ResultData<List<PayDTO>> getAllOrder() {
        return restTemplate.getForObject(PAYMENT_SRV_URL + "getAll", ResultData.class);
    }

    @DeleteMapping("delete/{id}")
    public ResultData<String> deleteOrder(@PathVariable(name = "id") Integer id) {
        log.info("delete order by id {}", id);
        restTemplate.delete(PAYMENT_SRV_URL + "del/{id}",id);
        return ResultData.success("删除成功");
    }

    @PutMapping("update")
    public ResultData<String> updateOrder(@RequestBody PayDTO dto) {
        log.info("update order by id {}", dto.getId());
        restTemplate.put(PAYMENT_SRV_URL + "update", dto);
        return ResultData.success("修改成功");
    }
}

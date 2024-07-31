package org.erywim.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.erywim.entities.Pay;
import org.erywim.entities.PayDTO;
import org.erywim.resp.ResultData;
import org.erywim.service.PayService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Erywim 2024/7/31
 */
@Slf4j
@RestController
@RequestMapping("/pay")
@Tag(name = "支付微服务模块",description = "支付CRUD")
public class PayController {
    @Resource
    private PayService payService;

    @PostMapping("add")
    @Operation(summary = "新增",description = "新增支付流水方法,json串做参数")
    public ResultData<String> addPay(@RequestBody Pay pay) {
        log.info("add pay:{}", pay);
        int result = payService.add(pay);
        return ResultData.success(result > 0? "成功插入记录" : "数据插入失败");
    }

    @DeleteMapping("del/{id}")
    @Operation(summary = "删除",description = "删除支付流水方法")
    public ResultData<Integer> delPay(@PathVariable("id") Integer id) {
        log.info("del pay:{}", id);
        int delete = payService.delete(id);
        return ResultData.success(delete);
    }

    @PutMapping("update")
    @Operation(summary = "修改",description = "修改支付流水方法")
    public ResultData<String> updatePay(@RequestBody PayDTO param) {
        log.info("update param:{}", param);
        Pay pay = new Pay();
        BeanUtils.copyProperties(param,pay);
        int result = payService.update(pay);
        return ResultData.success(result > 0? "成功修改记录" : "数据修改失败");
    }

    @GetMapping("get/{id}")
    @Operation(summary = "按照ID查流水",description = "查询支付流水方法")
    public ResultData<Pay> getPay(@PathVariable("id") Integer id) {
        return ResultData.success(payService.getById(id));
    }

    @GetMapping("getAll")
    @Operation(summary = "查所有流水",description = "查询支付流水方法")
    public ResultData<List<Pay>> getAllPay() {
        return ResultData.success(payService.getAll());
    }
}

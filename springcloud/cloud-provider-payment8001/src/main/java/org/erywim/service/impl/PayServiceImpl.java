package org.erywim.service.impl;

import jakarta.annotation.Resource;
import org.erywim.entities.Pay;
import org.erywim.mapper.PayMapper;
import org.erywim.service.PayService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Erywim 2024/7/31
 */
@Service
public class PayServiceImpl implements PayService {
    @Resource
    private PayMapper payMapper;


    @Override
    public Pay getById(int id) {
        return payMapper.selectByPrimaryKey(id);
    }

    @Override
    public int add(Pay pay) {
        return payMapper.insertSelective(pay);
    }

    @Override
    public int update(Pay pay) {
        return payMapper.updateByPrimaryKeySelective(pay);
    }

    @Override
    public int delete(int id) {
        return payMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Pay> getAll() {
        return payMapper.selectAll();
    }
}

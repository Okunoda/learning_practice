package org.erywim.service;

import org.erywim.entities.Pay;

import java.util.List;

/**
 * @author Erywim 2024/7/31
 */
public interface PayService {
    Pay getById(int id);

    int add(Pay pay);

    int update(Pay pay);

    int delete(int id);

    List<Pay> getAll();
}

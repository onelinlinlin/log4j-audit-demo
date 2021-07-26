package com.lzp.service;

import com.lzp.mapper.TransferMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    @Autowired
    TransferMapper transferMapper;

    /*执行用户的转账操作*/
    public int addTransferRecord(String fromUsername, String toUsername, int account) {
        return transferMapper.addTransferRecord(fromUsername,toUsername,account);
    }

}

package com.js.api.service;

import com.js.api.dto.RedPacketDto;

import java.math.BigDecimal;

public interface IRedPacketService {
    //发红包
    String handout(RedPacketDto dto);
    //抢红包
    BigDecimal rob(Integer userId, String redId) throws Exception;
    //抢红包---分布式
    BigDecimal robLock(Integer userId, String redId)throws Exception;
}

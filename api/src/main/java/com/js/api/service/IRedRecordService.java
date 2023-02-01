package com.js.api.service;

import com.js.api.dto.RedPacketDto;

import java.math.BigDecimal;
import java.util.List;

public interface IRedRecordService {
    void recordRedPacket(RedPacketDto dto, String redId, List<Integer> redPackets);
    void recordRobRedPacket(Integer userId, String redId, BigDecimal amount)throws Exception;
}

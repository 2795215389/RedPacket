package com.js.main.provider.service;

import com.js.api.dto.RedPacketDto;
import com.js.api.model.RedDetail;
import com.js.api.model.RedRecord;
import com.js.api.model.RedRobRecord;
import com.js.api.service.IRedRecordService;
import com.js.main.provider.mapper.RedDetailMapper;
import com.js.main.provider.mapper.RedRecordMapper;
import com.js.main.provider.mapper.RedRobRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class RedRecordServiceImpl implements IRedRecordService {
    @Autowired
    RedRecordMapper redRecordMapper;
    @Autowired
    RedDetailMapper redDetailMapper;
    @Autowired
    RedRobRecordMapper redRobRecordMapper;

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void recordRedPacket(RedPacketDto dto, String redId, List<Integer> redPackets) {
        RedRecord redRecord=new RedRecord();
        redRecord.setRedPacket(redId);
        redRecord.setCreateTime(new Date());
        redRecord.setUserId(Integer.valueOf(dto.getUid()));
        redRecord.setTotal(dto.getPeople());
        redRecord.setAmount(new BigDecimal(dto.getMoney()));
        redRecordMapper.insertSelective(redRecord);

        //写入每条抢红包的记录
        for(Integer i:redPackets){
            RedDetail redDetail=new RedDetail();
            redDetail.setRecordId(Integer.valueOf(redRecord.getId()));
            redDetail.setCreateTime(new Date());
            redDetail.setAmount(new BigDecimal(i));
            redDetailMapper.insertSelective(redDetail);
        }
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void recordRobRedPacket(Integer userId, String redId, BigDecimal amount) throws Exception {
        RedRobRecord redRobRecord=new RedRobRecord();
        redRobRecord.setUserId(userId);
        redRobRecord.setRedPacket(redId);
        redRobRecord.setRobTime(new Date());
        redRobRecord.setAmount(amount);
        redRobRecordMapper.insertSelective(redRobRecord);
    }
}

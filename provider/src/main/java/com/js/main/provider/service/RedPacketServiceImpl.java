package com.js.main.provider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.js.api.dto.RedPacketDto;
import com.js.api.service.IRedPacketService;
import com.js.api.service.IRedRecordService;
import com.js.api.util.RedPacketUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;


//dubbo的管理的服务层注解
@Service(
        version ="1.0.0",
        interfaceName ="com.js.api.service.IRedPacketService",
        interfaceClass = com.js.api.service.IRedPacketService.class)
public class RedPacketServiceImpl implements IRedPacketService {
    private static final Logger log= LoggerFactory.getLogger(RedPacketServiceImpl.class);
    private static final String keyPrefix="redis:redPacket";
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private IRedRecordService recordService;

    /**
     * 发红包，返回生成的红包缓存redId用于之后的抢红包，并使用redRecordService把数据异步写入数据库
     * @param dto
     * @return
     */
    @SneakyThrows
    @Override
    public String handout(RedPacketDto dto) {
        Integer people=dto.getPeople();
        Integer money=dto.getMoney();
        if(people>0 && money>0){
            List<Integer> redPackets=RedPacketUtil.handOut(money,people);

            String timeStamp= String.valueOf(System.nanoTime());
            String redId=new StringBuffer(keyPrefix).append(dto.getUid()).append(":").append(timeStamp).toString().trim();
            String redTotalKey=redId+":total";
            redisTemplate.opsForList().leftPushAll(redId,redPackets.toArray());
            redisTemplate.opsForValue().set(redTotalKey,people);
            recordService.recordRedPacket(dto,redId,redPackets);

            return redId;
        }else{
            log.info("参数不合法！");
            throw new Exception("非法的参数！");
        }
    }

    @Override
    public BigDecimal rob(Integer userId, String redId) throws Exception {
        final String redRobKey=redId+":rob";
        final String redTotalKey=redId+":total";
        ValueOperations valueOperations=redisTemplate.opsForValue();
        //判断用户是否已经抢过红包，抢过直接返回即可
        Object robValue=valueOperations.get(redRobKey);
        if(robValue!=null){
            return new BigDecimal(robValue.toString());
        }
        //是否能抢红包，即红包有剩余
        if(click(redId)){
            Object value=redisTemplate.opsForList().rightPop(redId);
            BigDecimal money=new BigDecimal(value.toString());
            if(value!=null){
                valueOperations.decrement(redTotalKey);
                valueOperations.setIfAbsent(redRobKey,value);
                recordService.recordRobRedPacket(userId,redId,money);
                log.info("当前用户抢到红包了:userId={} key={} 金额={}分",userId,redId,money);
                return money.divide(new BigDecimal(100));
            }
        }
        return null;
    }

    @Override
    public BigDecimal robLock(Integer userId, String redId) throws Exception {
        final String lockName = "redisLock" + redId + userId;
        final String redRobKey = redId + ":rob";
        final String redTotalKey = redId + ":total";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断用户是否已经抢过红包，抢过直接返回即可
        Object robValue = valueOperations.get(redRobKey);
        if (robValue != null) {
            return new BigDecimal(robValue.toString());
        }
        //是否能抢红包，即红包有剩余
        if (click(redId)) {
            try {
                boolean lock=valueOperations.setIfAbsent(lockName,redId,24L,TimeUnit.HOURS);
                if(lock){
                    Object value = redisTemplate.opsForList().rightPop(redId);
                    BigDecimal money = new BigDecimal(value.toString());
                    if (value != null) {
                        valueOperations.decrement(redTotalKey);
                        valueOperations.setIfAbsent(redRobKey, value);
                        recordService.recordRobRedPacket(userId, redId, money);
                        log.info("当前用户抢到红包了:userId={} key={} 金额={}分", userId, redId, money);
                        return money.divide(new BigDecimal(100));
                    }
                }
            } catch (Exception e) {
                log.info("系统发生异常-抢红包-加分布式锁失败！{}", e.fillInStackTrace());
                throw new Exception("系统发生异常-抢红包-加分布式锁失败");
            }
        }
        return null;
    }


    public boolean click(String redId){
        ValueOperations valueOperations=redisTemplate.opsForValue();
        final String redTotalKey=redId+":total";
        Object total=valueOperations.get(redTotalKey);
        if(total!=null && Integer.valueOf(total.toString())>0){
            return true;
        }
        return false;
    }
}

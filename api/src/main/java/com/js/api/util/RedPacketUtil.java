package com.js.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedPacketUtil {

    /**
     *
     * @param money 发的红包总金额
     * @param people 发的红包总个数
     * @return  每个红包金额，储存到链表中
     * 二倍均值法：每次生成范围[1,当前剩余钱数/当前剩余人数*2)的随机数
     * 最后出循环使得小红包之和恰好等于红包总金额
     */
    public static List<Integer> handOut(Integer money,Integer people){
        List<Integer>redPackets=new ArrayList<Integer>();
        Random random=new Random();
        if(money>0 && people>0){
            Integer restMoney=money;
            Integer restPeople=people;
            while(restPeople>1){
                Integer t=random.nextInt(restMoney/restPeople*2)+1;
                redPackets.add(t);
                restMoney-=t;
                restPeople--;
            }
            redPackets.add(restMoney);
        }

        return redPackets;
    }
}

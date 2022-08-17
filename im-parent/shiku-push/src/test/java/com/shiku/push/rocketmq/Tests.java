package com.shiku.push.rocketmq;

import cn.xyz.mianshi.vo.RedPacket;
import org.junit.Test;

public class Tests {

    @Test
    public void test1(){


        RedPacket packet = new RedPacket();
        packet.setMoney(1.0);
        int redPacketMax = 179000;

        System.out.println(packet.getMoney() / packet.getCount());
        System.out.println(redPacketMax/100 < packet.getMoney());
        System.out.println(packet.getMoney() / packet.getCount() > redPacketMax/100);
        if (redPacketMax/100 < packet.getMoney()
                || packet.getMoney() / packet.getCount() > redPacketMax/100){

            System.out.println("=============");
        }else{
            System.out.println("============222=");
        }



    }
}

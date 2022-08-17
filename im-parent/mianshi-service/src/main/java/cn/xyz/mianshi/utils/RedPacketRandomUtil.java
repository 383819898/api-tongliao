package cn.xyz.mianshi.utils;

import cn.xyz.mianshi.vo.ClientConfig;

import java.util.ArrayList;
import java.util.List;

public class RedPacketRandomUtil {
//	private static final float MINMONEY = 0.01f;
//	private static final float MAXMONEY = 200.00f;

	private static float randomRedPacket(float money,float mins,float maxs,int count)
	{
		if(count==1)
		{
			return (float)(Math.round(money*100))/100;
		}
		if(mins == maxs)
		{
			return mins;//如果最大值和最小值一样，就返回mins
		}
		float max = maxs>money?money:maxs;
		float one = ((float)Math.random()*(max-mins)+mins);
		one = (float)(Math.round(one*100))/100;
		float moneyOther = money - one;

		if (moneyOther == 0 || one == 0){
			return randomRedPacket(money,mins,maxs,count);
		}

		if(isRight(moneyOther,count-1))
		{
			return one;
		}
		else{
			//重新分配
			float avg = moneyOther / (count-1);
			ClientConfig clientConfig = SKBeanUtils.getAdminManager().getClientConfig();
			float MINMONEY = clientConfig.getRedPacketMin() / 100;
			float MAXMONEY = clientConfig.getRedPacketMax() / 100;
			if(avg<MINMONEY)
			{
				return randomRedPacket(money,mins,one,count);
			}else if(avg>MAXMONEY)
			{
				return randomRedPacket(money,one,maxs,count);
			}
		}
		return one;
	}

	private static final float TIMES = 2.1f;

	public static  Float splitRedPackets(float money,int count)
	{
		if(!isRight(money,count))
		{
			return null;
		}
		List<Float> list = new ArrayList<Float>();
		float max = (float)(money*TIMES/count);

		ClientConfig clientConfig = SKBeanUtils.getAdminManager().getClientConfig();
		float MINMONEY = clientConfig.getRedPacketMin() / 100;
		float MAXMONEY = clientConfig.getRedPacketMax() / 100;
		max = max>MAXMONEY?MAXMONEY:max;

		float one = randomRedPacket(money,MINMONEY,max,count);

		return one;
	}


	private static  boolean isRight(float money,int count)
	{
		ClientConfig clientConfig = SKBeanUtils.getAdminManager().getClientConfig();
		float MINMONEY = clientConfig.getRedPacketMin() / 100;
		float MAXMONEY = clientConfig.getRedPacketMax() / 100;
		double avg = money/count;
		if(avg<MINMONEY){
			return false;
		}
		else if(avg>MAXMONEY)
		{
			return false;
		}
		return true;
	}
}

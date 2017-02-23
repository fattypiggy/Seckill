package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by williamjing on 2017/2/23.
 */
public class SeckillServiceImpl implements SeckillService {

    private SeckillDao seckillDao;

    private SuccessKilledDao successKilledDao;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //盐值用来混淆MD5
    private final String slat = "!@#$EDTYFHJVJH()U)jkhdaskjhncz2139BJKBKJHU-9q";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null){
            return new Exposer(false,seckillId);
        }
        Date now = new Date();
        Date start = seckill.getStartTime();
        Date end = seckill.getEndTime();
        if(start.getTime() > now.getTime()
                || now.getTime() > end.getTime()){
            return new Exposer(false,now.getTime(),start.getTime(),end.getTime());
        }
        //转换特定字符过程，不可逆。
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    @Override
    public SeckillExecution seckillExecute(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
        if (md5 == null || md5 != getMD5(seckillId)){
            throw new SeckillException("Data was overwrited");
        }

        try {
            //减库存
            Date now = new Date();
            int reduceCount = seckillDao.reduceNumber(seckillId,now);
            if(reduceCount <= 0){
                //秒杀结束
                throw new SeckillCloseException("Seckill is closed");
            }else{
                //唯一SeckillId,userPhone
                int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
                if(insertCount <= 0){
                    //重复秒杀价
                    throw new RepeatKillException("Seckill repeated");
                }else{
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e){
            logger.error("Seckill inner error");
            throw new SeckillException("Seckill inner error" + e.getMessage());
        }
    }
}

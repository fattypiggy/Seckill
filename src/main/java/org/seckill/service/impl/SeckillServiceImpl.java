package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by williamjing on 2017/2/23.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //盐值用来混淆MD5
    private final String slat = "!@#$EDTYFHJVJH()U)jkhdaskjhncz2139BJKBKJHU-9q";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.setSeckill(seckill);
            }
        }
        Date now = new Date();
        Date start = seckill.getStartTime();
        Date end = seckill.getEndTime();
        if (start.getTime() > now.getTime()
                || now.getTime() > end.getTime()) {
            return new Exposer(false, now.getTime(), start.getTime(), end.getTime());
        }
        //转换特定字符过程，不可逆。
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事物声明的优点
     * 1：开发团队达成一致约定，明确事物变成风格
     * 2：能保证事物处理时间尽可能短，b不要穿插网络操作
     * 3：不是所有方法都需要事务管理
     */
    public SeckillExecution seckillExecute(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("Data was overwrited");
        }

        /**
         * 这里做了一个优化
         * 以前执行顺序   减库存->insert->commit(rollback)
         * 优化后执行顺序 insert->减库存->commit(rollback)
         * 这样做的好处是能首先过滤一次重复秒杀 后面能更快地释放行级锁 能抗住更高的并发
         */
        try {
            //唯一SeckillId,userPhone
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀价
                throw new RepeatKillException("Seckill repeated");
            } else {
                //减库存
                Date now = new Date();
                int reduceCount = seckillDao.reduceNumber(seckillId, now);
                if (reduceCount <= 0) {
                    //秒杀结束
                    throw new SeckillCloseException("Seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Seckill inner error");
            throw new SeckillException("Seckill inner error" + e.getMessage());
        }
    }
}

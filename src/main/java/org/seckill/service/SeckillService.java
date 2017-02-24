package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站在"使用者"角度来设计接口
 * Created by williamjing on 2017/2/23.
 */
public interface SeckillService {
    /**
     * 获取所有秒杀产品
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据Id获取秒杀
     *
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 暴露秒杀URL 否则显示时间
     *
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws RepeatKillException   重复秒杀异常
     * @throws SeckillCloseException 秒杀关闭异常
     * @throws SeckillException      秒杀错误异常
     */
    SeckillExecution seckillExecute(long seckillId, long userPhone, String md5)
            throws RepeatKillException, SeckillCloseException, SeckillException;
}

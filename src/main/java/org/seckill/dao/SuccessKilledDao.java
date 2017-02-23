package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by williamjing on 2017/2/20.
 */
public interface SuccessKilledDao {
    /**
     * 插入秒杀数据，可过滤重复
     *
     * @param seckillId
     * @param phone
     * @return 插入结果行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("phone") long phone);

    /**
     * 根据Id查询SuccessKilled并携带秒杀对象实体
     *
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("phone") long phone);
}

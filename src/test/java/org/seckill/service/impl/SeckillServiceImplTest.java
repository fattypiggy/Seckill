package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by williamjing on 2017/2/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("seckillList={}", seckillList);
        //15:12:55.575 [main] INFO  o.s.s.impl.SeckillServiceImplTest -
        // seckillList=[
        // Seckill{seckillId=1000, name='1000元秒杀iphone6', number=100, startTime=Fri Jan 01 14:00:00 CST 2016, endTime=Sat Jan 02 14:00:00 CST 2016, createTime=Tue Feb 21 07:12:44 CST 2017},
        // Seckill{seckillId=1001, name='800元秒杀ipad', number=200, startTime=Fri Jan 01 14:00:00 CST 2016, endTime=Sat Jan 02 14:00:00 CST 2016, createTime=Tue Feb 21 07:12:44 CST 2017},
        // Seckill{seckillId=1002, name='6600元秒杀mac book pro', number=300, startTime=Fri Jan 01 14:00:00 CST 2016, endTime=Sat Jan 02 14:00:00 CST 2016, createTime=Tue Feb 21 07:12:44 CST 2017},
        // Seckill{seckillId=1003, name='7000元秒杀iMac', number=400, startTime=Fri Jan 01 14:00:00 CST 2016, endTime=Sat Jan 02 14:00:00 CST 2016, createTime=Tue Feb 21 07:12:44 CST 2017}]

    }

    @Test
    public void getSeckillById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getSeckillById(id);
        logger.info("seckill={}", seckill);
        //15:14:51.799 [main] INFO  o.s.s.impl.SeckillServiceImplTest -
        // seckill=Seckill{seckillId=1000, name='1000元秒杀iphone6', number=100, startTime=Fri Jan 01 14:00:00 CST 2016, endTime=Sat Jan 02 14:00:00 CST 2016, createTime=Tue Feb 21 07:12:44 CST 2017}

    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        //15:24:32.712 [main] INFO  o.s.s.impl.SeckillServiceImplTest -
        //exposer=Exposer{exposed=true, md5='e5e1cbbb9d6cefdfd0b89b7086feea7e', seckillId=1000, now=0, start=0, end=0}

    }

    @Test
    public void seckillExecute() throws Exception {
        long id = 1000;
        long phone = 13122090180L;
        String md5 = "e5e1cbbb9d6cefdfd0b89b7086feea7e";

        SeckillExecution execution = seckillService.seckillExecute(id, phone, md5);
        logger.info("Seckill={}", execution);
        //15:29:00.850 [main] INFO  o.s.s.impl.SeckillServiceImplTest -
        // Seckill=
        // SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功',
        // successKilled=SuccessKilled{seckillId=1000, phone=13122090180, state=0, createTime=Sat Feb 25 05:29:00 CST 2017}}

    }


    /*
        集成测试 整体逻辑

     */
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1000;
        long phone = 13122090181L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            try {
                SeckillExecution execution = seckillService.seckillExecute(id, phone, exposer.getMd5());
                logger.info("result={}", execution);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.warn("exposer={}", exposer);
        }
        //15:37:28.690 [main] INFO  o.s.s.impl.SeckillServiceImplTest - result=
        // SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功',
        // successKilled=SuccessKilled{seckillId=1000, phone=13122090181, state=0, createTime=Sat Feb 25 05:37:28 CST 2017}}

        //第二次点击会提示重复秒杀
        //15:38:05.222 [main] ERROR o.s.s.impl.SeckillServiceImplTest - Seckill repeated

    }

}
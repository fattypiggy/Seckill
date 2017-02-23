package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by williamjing on 2017/2/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long id = 1001;
        long number = 13122090178L;

        int num = successKilledDao.insertSuccessKilled(id, number);
        System.out.println("updateCount=" + num);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id = 1001;
        long number = 13122090178L;

        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, number);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}
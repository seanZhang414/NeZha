package cn.com.duiba.tuia.engine.activity.test.base;

import cn.com.duiba.tuia.engine.activity.web.Application;
import cn.com.duiba.tuia.exception.TuiaRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * BaseJunit4Test
 *
 * @author leiliang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = Application.class)
public class BaseJunit4Test {

    @Test
    public void testBase() throws TuiaRuntimeException {
        Assert.assertTrue(true);
    }
}

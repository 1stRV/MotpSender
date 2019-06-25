package ru.x5.motpsender;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("unit")
public abstract class MotpApplicationTests {

    @MockBean
    RedisConnectionFactory redisConnectionFactory;

    protected static final String UTF8 = "UTF-8";

    protected MotpApplicationTests() {
    }
}

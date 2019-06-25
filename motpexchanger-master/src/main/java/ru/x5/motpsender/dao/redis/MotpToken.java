package ru.x5.motpsender.dao.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash("motpToken")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class MotpToken {
    @Id
    private String inn;
    private String token;
    private Integer lifetime;
    private Date tokenDate;
}

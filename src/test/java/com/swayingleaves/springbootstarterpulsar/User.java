package com.swayingleaves.springbootstarterpulsar;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhenglin
 * @apiNote
 * @since 2020/9/12 9:15 上午
 */
@Data
@Builder
public class User {
    private String name;
    private Integer age;
}

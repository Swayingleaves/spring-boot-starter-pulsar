package com.swayingleaves.springbootstarterpulsar;

import com.swayingleaves.springbootstarterpulsar.pulsar.PulsarTemplate;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringBootStarterPulsarApplicationTests {

    @Autowired
    PulsarTemplate pulsarTemplate;

    @Test
    void sendStrMsg() throws PulsarClientException {
        pulsarTemplate.sendStringMsg("topic","str");
    }

    @Test
    void sendJsonMsg() throws PulsarClientException {
        User user = User.builder()
                .age(18)
                .name("Jack")
                .build();
        pulsarTemplate.sendJsonMsg("topic",user);
    }

    @Test
    void sendJsonArrayMsg() throws PulsarClientException {
        User jack = User.builder()
                .age(18)
                .name("Jack")
                .build();
        User xiao = User.builder()
                .age(20)
                .name("xiao")
                .build();
        List<User> users = new ArrayList<>(2);
        users.add(xiao);
        users.add(jack);
        pulsarTemplate.sendJsonArrayMsg("topic",users);
    }

}

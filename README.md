<div align='center'><h1>spring-pulsar工具</h1></div>

> 目前没有上传到中央仓库，大家就pull下来看看实现就好了

* pom引入依赖
```xml
<dependency>
    <groupId>com.swayingleaves</groupId>
    <artifactId>spring-boot-starter-pulsar</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```
* application.yml 配置相关属性
```yaml
pulsar:
  # 服务器地址
  serviceUrl: pulsar://127.0.0.1:6650
  # io线程数默认10
  ioThreads: 10
  # listener线程数默认10
  listenerThreads: 10
  # JSON/JSONArray格式数据的生产者的topic，多个使用英文逗号分隔
  jsonProducers: topic1-json
  # string格式数据的生产者的topic，多个使用英文逗号分隔
  stringProducers: topic2-str
  # 消费者topic，多个使用英文逗号分隔
  consumers: topic1-json,topic2-str
```
* 生产者使用示例
```java
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
```
* 消费者使用示例
```java
@Service
@Slf4j
public class ReceiveDataService {

    @PulsarConsumer(topic ="#consumers[0]",clazz = Serialization.JSON)
    public void receive(JSONObject msg){
        log.info("receive msg:{}",msg);
    }
    
    @PulsarConsumer(topic ="#consumers[1]",clazz = Serialization.STRING)
    public void receive(String msg){
        log.info("receive msg:{}",msg);
    }
    
    @PulsarConsumer(topic ="#consumers[2]",clazz = Serialization.JSON_ARRAY)
    public void receive(JSONArray msg){
        log.info("receive msg:{}",msg);
    }

}
```

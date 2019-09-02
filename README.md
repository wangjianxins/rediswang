# RedisWang

## 这是一款简单高效的redis-java客户端


目前是单机版本的redis连接，后续支持集群版本的连接

目前支持的简单操作：set,get,del,setnx,hyperloglog...

持续更新中...


## 使用说明
    
### maven
    <groupId>com.github.wangjianxins</groupId>
    <artifactId>rediswang-spring-auto-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>

### 配置文件：
    redis:
        address: XX
        enbale: true
        port: XX

### 依赖注入
    @Autowired
    private RedisWangClient redisWangClient;
    
    #使用如下：
     ...
        int result = redisWangClient.incr("user");
        return result;
     ...


## redis服务交互说明：

    1、当我们set wang（key） redis(value) 这个命令时候,需要传送给redis服务器指令为：
    *3\r\n$3\r\nSET\r\n$4\r\n\wang\r\n$5\r\nredis 
    其中\r\n表示换行，RESP规定的，格式化后如下：
     
    *3 (可以理解为总共有三个param)
    $3 (表示'set'字节个数)
    set 
    $4 (表示'wang'的字节个数) 
    wang
    $5 (表示'redis'的字节个数)
    redis

    2、返回结果：当set wang(key) redis(value)后如果成功返回 +OK
    其中 '+' 也是redis的RESP协议规定的，redis服务返回都有如下这些(都是在返回数据中的第一个字节哦)：
    
    + (表示状态恢复 例如set后返回:'+OK')
    - (表示一些错误命令之类的)
    : (incr需要返回整数的，incr操作也是类似++的操作，和incr类似的操作都会返回 ':1' 或者 ':操作的数字个数' )
    $ (一般是返回字符串例如get操作返回string字符串)
    * (多条string mget操作返回， *3 返回三条string结果)
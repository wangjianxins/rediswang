package com.wang.redis.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/**
 * @Description：
 * 有关热key和大key的策略
 * 热key是什么呢：例如微博某个爆炸新闻，无数人来点击查看，他就是个超级热key,对于中小规模情况下，如果没有及时发现热key，会导致redis不稳定，
 * 在集群环境下导致，某些节点qps过高，单机的redis可能直接宕机，影响所有的业务。
 *
 * 如何发现热key呢？基于不同的环境有不同的方案:
 *  1、在redis用命令做monitor,这个命令在高流量下会导致阻塞，例如keys之类的命令一样,所有在高请求的环境不适合
 *  2、客户端拦截，这个可能是最常见的方法，但是一般的redis-java客户端没有提供类似方法 ，所以必须修改源代码，代码入侵较大，还需要测试稳定
 *  3、redis机器做查找，在redis服务器上做一些脚本，直接在本机拦截查找，优点是，本机操作延迟很低，效率高。缺点是，在集群多节点情况下，没法统计所有，还需要开发
 *  4、综上rediswang选择了第二种客户端拦截
 *
 * @author Jianxin Wang
 * @date 2019-09-17
 */
@Aspect
@Component
public class HotKeyIntercepter {

    @Pointcut("@annotation(com.wang.redis.aop.annotation.RedisKey)")
    public void annotationPointCut(){}

    @After("annotationPointCut()")
    public void after(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String name = signature.getName();

        Object[] o = joinPoint.getArgs();
        String value = (String) o[0];

        if(name.equals("get")){
            //统计这里的value字段

        }


    }
}

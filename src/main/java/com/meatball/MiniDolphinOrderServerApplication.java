/**
 * Project Name:MiniDolphinAuthenticateServerApplication.java
 * File Name:MiniDolphinAuthenticateServerApplication.java
 * Date:2018/10/11 上午11:21
 * Copyright (c) 2018, zhang.xiangyu@foxmail.com All Rights Reserved.
 */
package com.meatball;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Title: MiniDolphinAuthenticateServerApplication.java
 * @Description: todo(权限认证系统)
 * @Author: 張翔宇
 * @Date: 2018/10/11 上午11:21
 * @Version: V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableDistributedTransaction
public class MiniDolphinOrderServerApplication {

    /**
     * @Title: main
     * @Description: todo()
     * @Params: [args]
     * @Return: void    返回类型
     * @Author: 張翔宇
     * @Date: 2018/10/11 上午11:21
     */
    public static void main(String[] args) {
        SpringApplication.run(MiniDolphinOrderServerApplication.class, args);
    }
}

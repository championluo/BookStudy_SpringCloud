package com.bookstudy.licenseservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.DriverManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LicenseServiceApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/eagle_eye_dev",
                            "postgres", "root");

//            Statement statement = c.createStatement();
//            boolean execute = statement.("select * from public.licenses");


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    @Test
    public void testRedis(){

        redisTemplate.opsForValue().set("abc","123");
        System.out.println(redisTemplate.opsForValue().get("abc"));
    }

}

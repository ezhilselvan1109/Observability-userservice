package com.chella.userservice;

import com.chella.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DBTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testQuery() {
        try {
            userRepository.existsByEmail("test@test.com");
            System.out.println("SUCCESSFULLY QUERIED!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

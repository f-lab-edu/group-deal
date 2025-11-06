package com.app.groupdeal;

import com.app.groupdeal.infrastructure.user.JpaUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class GroupDealApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupDealApplication.class, args);
    }

}

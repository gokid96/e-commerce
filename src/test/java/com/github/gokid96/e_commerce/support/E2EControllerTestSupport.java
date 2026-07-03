package com.github.gokid96.e_commerce.support;

import com.github.gokid96.e_commerce.support.database.DatabaseCleaner;
import com.github.gokid96.e_commerce.support.database.RedisCacheCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

public abstract class E2EControllerTestSupport extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private RedisCacheCleaner redisCacheCleaner;

    protected RestTestClient client;

    @BeforeEach
    void setUpClient() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
        redisCacheCleaner.clean();
    }
}

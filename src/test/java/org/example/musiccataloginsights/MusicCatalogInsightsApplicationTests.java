package org.example.musiccataloginsights;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "groq.api.key=test-api-key"
})
class MusicCatalogInsightsApplicationTests {


    @Test
    void contextLoads() {
    }


}

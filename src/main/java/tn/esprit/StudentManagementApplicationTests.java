package tn.esprit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StudentManagementApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }
    
    @Test
    void simpleTest() {
        int result = 2 + 2;
        assertTrue(result == 4, "Basic math should work");
    }
}
package io.zipcoder.persistenceapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeAttributesTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetEmployeeAttributes() throws Exception {
        // Create employee
        String employeeJson = "{" +
                "\"employeeNumber\":\"E1100\"," +
                "\"firstName\":\"Attr\"," +
                "\"lastName\":\"Test\"," +
                "\"title\":\"QA\"," +
                "\"phoneNumber\":\"5555555555\"," +
                "\"email\":\"attr.test@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"departmentNumber\":42}";
        String response = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long employeeId = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Get employee attributes
        mockMvc.perform(get("/API/employee/" + employeeId + "/attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Attr"))
                .andExpect(jsonPath("$.lastName").value("Test"))
                .andExpect(jsonPath("$.title").value("QA"))
                .andExpect(jsonPath("$.departmentNumber").value(42));
    }
}

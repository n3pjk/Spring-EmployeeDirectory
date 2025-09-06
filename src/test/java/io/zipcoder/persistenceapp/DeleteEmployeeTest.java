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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteEmployeeTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        // Create employee
        String employeeJson = "{" +
                "\"employeeNumber\":\"E600\"," +
                "\"firstName\":\"Delete\"," +
                "\"lastName\":\"Me\"," +
                "\"title\":\"Temp\"," +
                "\"phoneNumber\":\"1231231234\"," +
                "\"email\":\"delete.me@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String response = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long employeeId = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Delete employee
        mockMvc.perform(delete("/API/employee?ids=" + employeeId))
                .andExpect(status().isNoContent());

        // Verify employee is deleted
        mockMvc.perform(get("/API/employee/" + employeeId + "/attributes"))
                .andExpect(status().isNotFound());
    }
}

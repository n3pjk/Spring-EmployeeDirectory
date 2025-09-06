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

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteMultipleEmployeesTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testDeleteMultipleEmployees() throws Exception {
        // Create two employees
        String emp1Json = "{" +
                "\"employeeNumber\":\"E700\"," +
                "\"firstName\":\"Emp1\"," +
                "\"lastName\":\"Delete\"," +
                "\"title\":\"Temp\"," +
                "\"phoneNumber\":\"1111111111\"," +
                "\"email\":\"emp1.delete@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String emp2Json = "{" +
                "\"employeeNumber\":\"E701\"," +
                "\"firstName\":\"Emp2\"," +
                "\"lastName\":\"Delete\"," +
                "\"title\":\"Temp\"," +
                "\"phoneNumber\":\"2222222222\"," +
                "\"email\":\"emp2.delete@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String resp1 = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emp1Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String resp2 = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emp2Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id1 = com.jayway.jsonpath.JsonPath.read(resp1, "$.id");
        Long id2 = com.jayway.jsonpath.JsonPath.read(resp2, "$.id");

        // Delete both employees
        mockMvc.perform(delete("/API/employee?ids=" + id1 + "," + id2))
                .andExpect(status().isNoContent());

        // Verify both are deleted
        mockMvc.perform(get("/API/employee/" + id1 + "/attributes"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/API/employee/" + id2 + "/attributes"))
                .andExpect(status().isNotFound());
    }
}

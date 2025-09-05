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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeManagerUpdateTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUpdateEmployeeManager() throws Exception {
        // First, create an employee
        String employeeJson = "{" +
                "\"employeeNumber\":\"E002\"," +
                "\"firstName\":\"Jane\"," +
                "\"lastName\":\"Smith\"," +
                "\"title\":\"QA\"," +
                "\"phoneNumber\":\"5555555555\"," +
                "\"email\":\"jane.smith@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String managerJson = "{" +
                "\"employeeNumber\":\"E003\"," +
                "\"firstName\":\"Bob\"," +
                "\"lastName\":\"Manager\"," +
                "\"title\":\"Manager\"," +
                "\"phoneNumber\":\"1111111111\"," +
                "\"email\":\"bob.manager@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";

        // Create manager
        String managerResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long managerId = com.jayway.jsonpath.JsonPath.read(managerResponse, "$.id");

        // Create employee
        String employeeResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long employeeId = com.jayway.jsonpath.JsonPath.read(employeeResponse, "$.id");

        // Update employee's manager
        mockMvc.perform(put("/API/employee/" + employeeId + "/manager?managerId=" + managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(managerId));
    }
}

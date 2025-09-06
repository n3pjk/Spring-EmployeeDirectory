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
public class EmployeeByManagerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetEmployeesByManager() throws Exception {
        // Create manager
        String managerJson = "{" +
                "\"employeeNumber\":\"E100\"," +
                "\"firstName\":\"Manager\"," +
                "\"lastName\":\"One\"," +
                "\"title\":\"Manager\"," +
                "\"phoneNumber\":\"9999999999\"," +
                "\"email\":\"manager.one@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String managerResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long managerId = com.jayway.jsonpath.JsonPath.read(managerResponse, "$.id");

        // Create employee under manager
        String employeeJson = "{" +
                "\"employeeNumber\":\"E101\"," +
                "\"firstName\":\"Emp\"," +
                "\"lastName\":\"Under\"," +
                "\"title\":\"Staff\"," +
                "\"phoneNumber\":\"8888888888\"," +
                "\"email\":\"emp.under@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"managerId\":" + managerId + "}";
        mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated());

        // Get employees by manager
        mockMvc.perform(get("/API/employee/manager/" + managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Emp"))
                .andExpect(jsonPath("$[0].managerId").value(managerId));
    }
}

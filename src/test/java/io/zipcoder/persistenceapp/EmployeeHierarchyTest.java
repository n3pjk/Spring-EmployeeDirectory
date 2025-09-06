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
public class EmployeeHierarchyTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetReportingHierarchy() throws Exception {
        // Create top manager
        String topManagerJson = "{" +
                "\"employeeNumber\":\"E200\"," +
                "\"firstName\":\"Top\"," +
                "\"lastName\":\"Manager\"," +
                "\"title\":\"CEO\"," +
                "\"phoneNumber\":\"7777777777\"," +
                "\"email\":\"top.manager@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String topManagerResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(topManagerJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long topManagerId = com.jayway.jsonpath.JsonPath.read(topManagerResponse, "$.id");

        // Create middle manager
        String middleManagerJson = "{" +
                "\"employeeNumber\":\"E201\"," +
                "\"firstName\":\"Middle\"," +
                "\"lastName\":\"Manager\"," +
                "\"title\":\"Manager\"," +
                "\"phoneNumber\":\"6666666666\"," +
                "\"email\":\"middle.manager@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"managerId\":" + topManagerId + "}";
        String middleManagerResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(middleManagerJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long middleManagerId = com.jayway.jsonpath.JsonPath.read(middleManagerResponse, "$.id");

        // Create employee under middle manager
        String employeeJson = "{" +
                "\"employeeNumber\":\"E202\"," +
                "\"firstName\":\"Worker\"," +
                "\"lastName\":\"Bee\"," +
                "\"title\":\"Staff\"," +
                "\"phoneNumber\":\"5555555555\"," +
                "\"email\":\"worker.bee@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"managerId\":" + middleManagerId + "}";
        String employeeResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long employeeId = com.jayway.jsonpath.JsonPath.read(employeeResponse, "$.id");

        // Get reporting hierarchy for employee
        mockMvc.perform(get("/API/employee/" + employeeId + "/hierarchy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Middle"))
                .andExpect(jsonPath("$[1].firstName").value("Top"));
    }
}

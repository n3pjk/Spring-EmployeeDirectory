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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteDirectReportsTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testDeleteDirectReportsAndReassign() throws Exception {
        // Create manager
        String managerJson = "{" +
                "\"employeeNumber\":\"E1000\"," +
                "\"firstName\":\"Manager\"," +
                "\"lastName\":\"Direct\"," +
                "\"title\":\"Boss\"," +
                "\"phoneNumber\":\"1111111111\"," +
                "\"email\":\"manager.direct@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String managerResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long managerId = com.jayway.jsonpath.JsonPath.read(managerResponse, "$.id");

        // Create direct report
        String directJson = "{" +
                "\"employeeNumber\":\"E1001\"," +
                "\"firstName\":\"Direct\"," +
                "\"lastName\":\"Report\"," +
                "\"title\":\"Staff\"," +
                "\"phoneNumber\":\"2222222222\"," +
                "\"email\":\"direct.report@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"managerId\":" + managerId + "}";
        String directResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(directJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long directId = com.jayway.jsonpath.JsonPath.read(directResponse, "$.id");

        // Create indirect report
        String indirectJson = "{" +
                "\"employeeNumber\":\"E1002\"," +
                "\"firstName\":\"Indirect\"," +
                "\"lastName\":\"Report\"," +
                "\"title\":\"Staff\"," +
                "\"phoneNumber\":\"3333333333\"," +
                "\"email\":\"indirect.report@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"managerId\":" + directId + "}";
        String indirectResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(indirectJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long indirectId = com.jayway.jsonpath.JsonPath.read(indirectResponse, "$.id");

        // Delete all direct reports to manager
        mockMvc.perform(delete("/API/employee/manager/" + managerId + "/direct-reports"))
                .andExpect(status().isNoContent());

        // Verify direct report is deleted
        mockMvc.perform(get("/API/employee/" + directId + "/attributes"))
                .andExpect(status().isNotFound());

        // Verify indirect report is reassigned to manager
        mockMvc.perform(get("/API/employee/" + indirectId + "/attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(managerId));
    }
}

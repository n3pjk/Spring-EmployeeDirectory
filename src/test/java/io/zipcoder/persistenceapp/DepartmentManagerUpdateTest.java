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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DepartmentManagerUpdateTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUpdateDepartmentManager() throws Exception {
        // Create department
        String departmentJson = "{" +
                "\"departmentName\":\"HR\"}";
        String deptResponse = mockMvc.perform(post("/API/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(departmentJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long departmentNumber = com.jayway.jsonpath.JsonPath.read(deptResponse, "$.departmentNumber");

        // Create manager
        String managerJson = "{" +
                "\"employeeNumber\":\"E005\"," +
                "\"firstName\":\"Sam\"," +
                "\"lastName\":\"Boss\"," +
                "\"title\":\"Dept Manager\"," +
                "\"phoneNumber\":\"4444444444\"," +
                "\"email\":\"sam.boss@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String managerResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long managerId = com.jayway.jsonpath.JsonPath.read(managerResponse, "$.id");

        // Update department manager
        mockMvc.perform(put("/API/department/" + departmentNumber + "/manager?managerId=" + managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(managerId));
    }
}

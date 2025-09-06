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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MergeDepartmentsTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testMergeDepartments() throws Exception {
        // Create department A
        String deptAJson = "{" +
                "\"departmentName\":\"Alpha\"}";
        String deptAResponse = mockMvc.perform(post("/API/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deptAJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long deptANumber = com.jayway.jsonpath.JsonPath.read(deptAResponse, "$.departmentNumber");

        // Create department B
        String deptBJson = "{" +
                "\"departmentName\":\"Beta\"}";
        String deptBResponse = mockMvc.perform(post("/API/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deptBJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long deptBNumber = com.jayway.jsonpath.JsonPath.read(deptBResponse, "$.departmentNumber");

        // Create manager for A
        String managerAJson = "{" +
                "\"employeeNumber\":\"E1200\"," +
                "\"firstName\":\"Alpha\"," +
                "\"lastName\":\"Boss\"," +
                "\"title\":\"Mgr\"," +
                "\"phoneNumber\":\"1111111111\"," +
                "\"email\":\"alpha.boss@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String managerAResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerAJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long managerAId = com.jayway.jsonpath.JsonPath.read(managerAResponse, "$.id");
        // Set manager for A
        mockMvc.perform(put("/API/department/" + deptANumber + "/manager?managerId=" + managerAId))
                .andExpect(status().isOk());

        // Create manager for B
        String managerBJson = "{" +
                "\"employeeNumber\":\"E1201\"," +
                "\"firstName\":\"Beta\"," +
                "\"lastName\":\"Boss\"," +
                "\"title\":\"Mgr\"," +
                "\"phoneNumber\":\"2222222222\"," +
                "\"email\":\"beta.boss@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String managerBResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerBJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long managerBId = com.jayway.jsonpath.JsonPath.read(managerBResponse, "$.id");
        // Set manager for B
        mockMvc.perform(put("/API/department/" + deptBNumber + "/manager?managerId=" + managerBId))
                .andExpect(status().isOk());

        // Create employee in B
        String empBJson = "{" +
                "\"employeeNumber\":\"E1202\"," +
                "\"firstName\":\"Beta\"," +
                "\"lastName\":\"Emp\"," +
                "\"title\":\"Staff\"," +
                "\"phoneNumber\":\"3333333333\"," +
                "\"email\":\"beta.emp@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"departmentNumber\":" + deptBNumber + "}";
        String empBResponse = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(empBJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long empBId = com.jayway.jsonpath.JsonPath.read(empBResponse, "$.id");

        // Merge departments
        mockMvc.perform(put("/API/department/merge?deptA=Alpha&deptB=Beta"))
                .andExpect(status().isOk());

        // Check manager of B now reports to manager of A and is in department A
        mockMvc.perform(get("/API/employee/" + managerBId + "/attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerId").value(managerAId))
                .andExpect(jsonPath("$.departmentNumber").value(deptANumber));
        // Check employee in B is now in department A
        mockMvc.perform(get("/API/employee/" + empBId + "/attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentNumber").value(deptANumber));
    }
}

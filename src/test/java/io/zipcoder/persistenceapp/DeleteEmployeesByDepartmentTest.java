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

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteEmployeesByDepartmentTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testDeleteEmployeesByDepartment() throws Exception {
        // Create department
        String departmentJson = "{" +
                "\"departmentName\":\"Ops\"}";
        String deptResponse = mockMvc.perform(post("/API/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(departmentJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long departmentNumber = com.jayway.jsonpath.JsonPath.read(deptResponse, "$.departmentNumber");

        // Create two employees in department
        String emp1Json = "{" +
                "\"employeeNumber\":\"E800\"," +
                "\"firstName\":\"Emp1\"," +
                "\"lastName\":\"Ops\"," +
                "\"title\":\"Ops Staff\"," +
                "\"phoneNumber\":\"1111111111\"," +
                "\"email\":\"emp1.ops@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"departmentNumber\":" + departmentNumber + "}";
        String emp2Json = "{" +
                "\"employeeNumber\":\"E801\"," +
                "\"firstName\":\"Emp2\"," +
                "\"lastName\":\"Ops\"," +
                "\"title\":\"Ops Staff\"," +
                "\"phoneNumber\":\"2222222222\"," +
                "\"email\":\"emp2.ops@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"departmentNumber\":" + departmentNumber + "}";
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

        // Delete all employees in department
        mockMvc.perform(delete("/API/employee/department/" + departmentNumber))
                .andExpect(status().isNoContent());

        // Verify both are deleted
        mockMvc.perform(get("/API/employee/" + id1 + "/attributes"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/API/employee/" + id2 + "/attributes"))
                .andExpect(status().isNotFound());
    }
}

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
public class EmployeesByDepartmentTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetEmployeesByDepartment() throws Exception {
        // Create department
        String departmentJson = "{" +
                "\"departmentName\":\"IT\"}";
        String deptResponse = mockMvc.perform(post("/API/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(departmentJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long departmentNumber = com.jayway.jsonpath.JsonPath.read(deptResponse, "$.departmentNumber");

        // Create employee in department
        String employeeJson = "{" +
                "\"employeeNumber\":\"E400\"," +
                "\"firstName\":\"Dept\"," +
                "\"lastName\":\"Member\"," +
                "\"title\":\"IT Staff\"," +
                "\"phoneNumber\":\"5555555555\"," +
                "\"email\":\"dept.member@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"," +
                "\"departmentNumber\":" + departmentNumber + "}";
        mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated());

        // Get employees by department
        mockMvc.perform(get("/API/employee/department/" + departmentNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Dept"))
                .andExpect(jsonPath("$[0].departmentNumber").value(departmentNumber));
    }
}

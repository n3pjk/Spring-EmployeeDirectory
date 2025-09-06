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
public class EmployeeUpdateFieldsTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUpdateEmployeeFields() throws Exception {
        // Create employee
        String employeeJson = "{" +
                "\"employeeNumber\":\"E004\"," +
                "\"firstName\":\"Alice\"," +
                "\"lastName\":\"Brown\"," +
                "\"title\":\"Analyst\"," +
                "\"phoneNumber\":\"2222222222\"," +
                "\"email\":\"alice.brown@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        String response = mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long employeeId = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Update fields (not managerId)
        String updateJson = "{" +
                "\"employeeNumber\":\"E004X\"," +
                "\"firstName\":\"Alicia\"," +
                "\"lastName\":\"Brownson\"," +
                "\"title\":\"Senior Analyst\"," +
                "\"phoneNumber\":\"3333333333\"," +
                "\"email\":\"alicia.brownson@example.com\"," +
                "\"hireDate\":\"2025-10-01T00:00:00.000+00:00\"," +
                "\"departmentNumber\":10}";

        mockMvc.perform(put("/API/employee/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.lastName").value("Brownson"))
                .andExpect(jsonPath("$.employeeNumber").value("E004X"))
                .andExpect(jsonPath("$.title").value("Senior Analyst"))
                .andExpect(jsonPath("$.phoneNumber").value("3333333333"))
                .andExpect(jsonPath("$.email").value("alicia.brownson@example.com"))
                .andExpect(jsonPath("$.departmentNumber").value(10));
    }
}

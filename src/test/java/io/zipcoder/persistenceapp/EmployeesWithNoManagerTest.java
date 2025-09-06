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
public class EmployeesWithNoManagerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetEmployeesWithNoManager() throws Exception {
        // Create employee with no manager
        String employeeJson = "{" +
                "\"employeeNumber\":\"E300\"," +
                "\"firstName\":\"Solo\"," +
                "\"lastName\":\"Worker\"," +
                "\"title\":\"Lone Wolf\"," +
                "\"phoneNumber\":\"1231231234\"," +
                "\"email\":\"solo.worker@example.com\"," +
                "\"hireDate\":\"2025-09-05T00:00:00.000+00:00\"}";
        mockMvc.perform(post("/API/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isCreated());

        // Get employees with no manager
        mockMvc.perform(get("/API/employee/no-manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Solo"))
                .andExpect(jsonPath("$[0].managerId").doesNotExist());
    }
}

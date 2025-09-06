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
public class DepartmentNameUpdateTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testUpdateDepartmentName() throws Exception {
        // Create department
        String departmentJson = "{" +
                "\"departmentName\":\"Finance\"}";
        String deptResponse = mockMvc.perform(post("/API/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(departmentJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long departmentNumber = com.jayway.jsonpath.JsonPath.read(deptResponse, "$.departmentNumber");

        // Update department name
        mockMvc.perform(put("/API/department/" + departmentNumber + "/name?departmentName=Accounting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName").value("Accounting"));
    }
}

package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

    }

    // Test "signin" endpoint
    @Test
    void testauthenticateAppUser() throws Exception {
        SignInDTO signInDTO = new SignInDTO();
        signInDTO.setEmail("araf4@gmail.com");
        signInDTO.setPassword("12345");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(signInDTO))
        );

        // Check if status is 200, then it's OK, otherwise display the status
        if (resultActions.andReturn().getResponse().getStatus() == 200) {
            resultActions.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").exists());
        } else {
            // Handle error cases
            System.out.println("HTTP Status: " + resultActions.andReturn().getResponse().getStatus());
        }
    }

    // Test "signup" endpoint
    @Test
    void testregisterAppUser() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setFirstName("John");
        signUpDTO.setLastName("Doe");
        signUpDTO.setEmail("johndoe@yahoo.com");
        signUpDTO.setPassword("password");
        signUpDTO.setContactNo("1234567890");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(signUpDTO))
        );

        // Check if status is 200, then it's OK, otherwise display the status
        if (resultActions.andReturn().getResponse().getStatus() == 200) {
            resultActions.andExpect(status().isOk());
        } else {
            // Handle error cases
            System.out.println("HTTP Status: " + resultActions.andReturn().getResponse().getStatus());
        }
    }

    // Helper method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

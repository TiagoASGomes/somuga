package org.somuga.testUtils;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class Utils {

    public static String postRequest(String path, ResultMatcher status, String body, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(post(path)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String postRequestWithUser(String path, ResultMatcher status, String body, MockMvc mockMvc, RequestPostProcessor user) throws Exception {
        return mockMvc.perform(post(path)
                        .with(csrf())
                        .with(user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String getRequest(String path, ResultMatcher status, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(path)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String putRequest(String path, ResultMatcher status, String body, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(put(path)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String patchRequest(String path, ResultMatcher status, String body, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(patch(path)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String patchRequestWithUser(String path, ResultMatcher status, String body, MockMvc mockMvc, RequestPostProcessor user) throws Exception {
        return mockMvc.perform(patch(path)
                        .with(csrf())
                        .with(user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String deleteRequest(String path, ResultMatcher status, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(delete(path)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }

    public static String deleteRequestWithUser(String path, ResultMatcher status, MockMvc mockMvc, RequestPostProcessor user) throws Exception {
        return mockMvc.perform(delete(path)
                        .with(csrf())
                        .with(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse().getContentAsString();
    }


}

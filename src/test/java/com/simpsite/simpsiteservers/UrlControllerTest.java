package com.simpsite.simpsiteservers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testShortenUrl() throws Exception{
        String longUrl = "http://example.com/fghjygtgfcvbjku789";
        MvcResult mvcResult = this.mockMvc.perform(post("/encode")
                        .content(longUrl)
                        .contentType(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Shortened URL: " +responseContent);
    }

    @Test
    public void testGetLongUrl()throws Exception {
        String shortUrl = "996444dc";
        MvcResult mvcResult=this.mockMvc.perform(get("/"+shortUrl))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Long URL: " +responseContent);
    }
}

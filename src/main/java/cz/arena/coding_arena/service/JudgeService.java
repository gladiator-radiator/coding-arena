package cz.arena.coding_arena.service;

import cz.arena.coding_arena.dto.JudgeRequest;
import cz.arena.coding_arena.dto.JudgeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JudgeService {

    private final RestTemplate restTemplate;

    @Value("${judge0.api.url}")
    private String judgeUrl;

    public JudgeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends the code to run in Judge0 and waits for an answer
     */
    public JudgeResponse submitCode(String sourceCode, Integer languageId, String input, String expectedOutput) {

        JudgeRequest request = new JudgeRequest(sourceCode, languageId, input, expectedOutput);

        // runs code and sends answer
        String url = judgeUrl + "/submissions?wait=true";

        try {
            // sends POST request and automatically maps the answer to JudgeResponse Object
            return restTemplate.postForObject(url, request, JudgeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Judge0 API: " + e.getMessage());
        }
    }
}
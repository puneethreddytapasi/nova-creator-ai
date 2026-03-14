package com.example.novacreator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.SdkBytes;

import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.util.Base64;

@Service
public class NovaService {

    private final BedrockRuntimeClient client;

    public NovaService() {

        client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    // TEXT PROMPT GENERATION
    public String generate(String prompt) {

        ContentBlock content = ContentBlock.builder()
                .text("Generate 5 short Instagram captions with emojis and hashtags for: "
                        + prompt +
                        ". Return only captions numbered 1 to 5.")
                .build();

        Message message = Message.builder()
                .role("user")
                .content(content)
                .build();

        ConverseRequest request = ConverseRequest.builder()
                .modelId("amazon.nova-lite-v1:0")
                .messages(message)
                .build();

        ConverseResponse response = client.converse(request);

        return response.output()
                .message()
                .content()
                .get(0)
                .text();
    }

    // IMAGE CAPTION GENERATION
    public String generateFromImage(MultipartFile file) throws Exception {

        byte[] bytes = file.getBytes();

        String base64 = Base64.getEncoder().encodeToString(bytes);

        String requestBody = """
        {
          "messages":[
            {
              "role":"user",
              "content":[
                {
                  "image":{
                    "format":"png",
                    "source":{
                      "bytes":"%s"
                    }
                  }
                },
                {
                  "text":"Generate an Instagram caption with emojis and hashtags for this image"
                }
              ]
            }
          ]
        }
        """.formatted(base64);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("amazon.nova-lite-v1:0")
                .body(SdkBytes.fromUtf8String(requestBody))
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        return response.body().asUtf8String();
    }
}
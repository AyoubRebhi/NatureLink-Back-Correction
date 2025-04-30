package com.example.naturelink.Config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FlaskStarter {

    private Process nameApiProcess;
    private Process chatbotApiProcess;
    private Process imageGenApiProcess;
    private Process itineraryApiProcess;
    private Process recommendationApiProcess; // New process for recommendation API
    private Process faqChatbotApiProcess;

    @PostConstruct
    public void startFlaskServers() {
        try {
            ProcessBuilder nameApi = new ProcessBuilder("python", "src/main/python-ml/name_api.py");
            nameApi.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            nameApi.redirectError(ProcessBuilder.Redirect.INHERIT);
            nameApiProcess = nameApi.start();
            System.out.println("✅ name_api.py started on port 5001");

            ProcessBuilder chatbotApi = new ProcessBuilder("python", "src/main/python-ml/chatbot_api.py");
            chatbotApi.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            chatbotApi.redirectError(ProcessBuilder.Redirect.INHERIT);
            chatbotApiProcess = chatbotApi.start();
            System.out.println("✅ chatbot_api.py started on port 5002");

            ProcessBuilder imageGenApi = new ProcessBuilder("python", "src/main/python-ml/image_gen_api.py");
            imageGenApi.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            imageGenApi.redirectError(ProcessBuilder.Redirect.INHERIT);
            imageGenApiProcess = imageGenApi.start();
            System.out.println("✅ image_gen_api.py started on port 5003");

            ProcessBuilder itineraryApi = new ProcessBuilder("python", "src/main/naturelink-ai/jiji.py");
            itineraryApi.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            itineraryApi.redirectError(ProcessBuilder.Redirect.INHERIT);
            itineraryApiProcess = itineraryApi.start();
            System.out.println("✅ jiji.py started on port 5010");

            ProcessBuilder recommendationApi = new ProcessBuilder("python", "src/main/python-ml/app.py");
            recommendationApi.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            recommendationApi.redirectError(ProcessBuilder.Redirect.INHERIT);
            recommendationApiProcess = recommendationApi.start();
            System.out.println("✅ recommendation_api.py started on port 5007");


            ProcessBuilder faqChatbotApi = new ProcessBuilder("python", "src/main/python-ml/chatbot.py");
            faqChatbotApi.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            faqChatbotApi.redirectError(ProcessBuilder.Redirect.INHERIT);
            faqChatbotApiProcess = faqChatbotApi.start();
            System.out.println("✅ faq_chatbot_api.py started on port 5020");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopFlaskServers() {
        destroyFlaskProcess(nameApiProcess, "name_api");
        destroyFlaskProcess(chatbotApiProcess, "chatbot_api");
        destroyFlaskProcess(imageGenApiProcess, "image_gen_api");
        destroyFlaskProcess(itineraryApiProcess, "jiji_api");
        destroyFlaskProcess(recommendationApiProcess, "recommendation_api");
        destroyFlaskProcess(faqChatbotApiProcess, "faq_chatbot_api");

    }

    private void destroyFlaskProcess(Process process, String name) {
        if (process != null && process.isAlive()) {
            process.destroy();
            try {
                boolean exited = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
                if (!exited) {
                    process.destroyForcibly();
                    System.out.println("⚠️ " + name + " did not stop gracefully. Forced shutdown.");
                } else {
                    System.out.println("🛑 " + name + " stopped gracefully.");
                }
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
                System.out.println("⚠️ " + name + " shutdown interrupted. Forced shutdown.");
            }
        }
    }
}
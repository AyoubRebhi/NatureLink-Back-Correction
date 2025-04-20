package com.example.naturelink.Config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FlaskStarter {

    private Process nameApiProcess;
    private Process chatbotApiProcess;

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopFlaskServers() {
        destroyFlaskProcess(nameApiProcess, "name_api");
        destroyFlaskProcess(chatbotApiProcess, "chatbot_api");
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

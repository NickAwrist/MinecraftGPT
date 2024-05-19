package nicholas.minecraftgpt;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.SystemMessage;
import com.theokanning.openai.service.OpenAiService;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenAI {

    private final String apiKey;

    public OpenAI(JavaPlugin plugin){
        apiKey = plugin.getConfig().getString("OPENAI_API_KEY");
    }

    public String queryGPT(String prompt){

        prompt = "You are a Minecraft teacher and are teaching the user how to play Minecraft. In a short and concise manner, answer this prompt:"+prompt;

        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new SystemMessage(prompt);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(50)
                .build();
        ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);

        return chatCompletion.getChoices().get(0).getMessage().getContent();

    }

}

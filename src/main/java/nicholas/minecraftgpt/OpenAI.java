package nicholas.minecraftgpt;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.SystemMessage;
import com.theokanning.openai.service.OpenAiService;
import org.bukkit.entity.Player;
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

        boolean enhancement_enabled = MinecraftGPT.getPlugin().getConfig().getBoolean("prompt_enhancement_enabled");

        if(enhancement_enabled){
            String template = MinecraftGPT.getPlugin().getConfig().getString("prompt_enhancement");
            prompt = template.replace("{PROMPT}", prompt);
        }else{
            prompt = "In a very short and concise manner, respond to this prompt: "+prompt;
        }

        int maxTokens = MinecraftGPT.getPlugin().getConfig().getInt("model.max_tokens");
        String model = MinecraftGPT.getPlugin().getConfig().getString("model.name");

        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new SystemMessage(prompt);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .n(1)
                .maxTokens(maxTokens)
                .build();
        ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);

        return chatCompletion.getChoices().get(0).getMessage().getContent();
    }

}

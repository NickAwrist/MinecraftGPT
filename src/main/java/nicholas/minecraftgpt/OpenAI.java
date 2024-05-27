package nicholas.minecraftgpt;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.SystemMessage;
import com.theokanning.openai.service.OpenAiService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.*;

public class OpenAI {

    // Flag to determine if the rate limiter timer is running
    private boolean rateLimiterRunning = false;
    private final boolean rateLimiterEnabled = MinecraftGPT.getPlugin().getConfig().getBoolean("rate_limiter.enabled");

    private final String apiKey;

    // HashMap to store UUID and their respective rateLimiter
    private final HashMap<UUID, RateLimiter> rateLimiters = new HashMap<>();

    // Model constants
    private final String model = MinecraftGPT.getPlugin().getConfig().getString("model.name");
    private final double temperature = MinecraftGPT.getPlugin().getConfig().getDouble("model.temperature");
    private final int maxTokens = MinecraftGPT.getPlugin().getConfig().getInt("model.max_tokens");
    private final double topP = MinecraftGPT.getPlugin().getConfig().getDouble("model.top_p");
    private final double frequencyPenalty = MinecraftGPT.getPlugin().getConfig().getDouble("model.frequency_penalty");
    private final double presencePenalty = MinecraftGPT.getPlugin().getConfig().getDouble("model.presence_penalty");


    private final boolean enhancement_enabled = MinecraftGPT.getPlugin().getConfig().getBoolean("prompt_enhancement_enabled");
    private final String enhancement_template = MinecraftGPT.getPlugin().getConfig().getString("prompt_enhancement");



    public OpenAI(JavaPlugin plugin){
        apiKey = plugin.getConfig().getString("OPENAI_API_KEY");

        Bukkit.getLogger().info("OpenAI Initialized");
    }

    // Method to query the GPT model
    public String queryGPT(String prompt){

        // Check if prompt enhancement is enabled, if so, edit the prompt
        if(enhancement_enabled){

            try {
                prompt = enhancement_template.replace("{PROMPT}", prompt);
            }catch (NullPointerException e){
                Bukkit.getLogger().warning("MISSING {PROMPT} IN PROMPT_ENHANCEMENT CONFIGURATION");
                return "Configuration Error. Please contact the server administrator.";
            }

        }else{
            prompt = "In a very short and concise manner, respond to this prompt: "+prompt;
        }

        // Create the OpenAiService
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new SystemMessage(prompt);
        messages.add(systemMessage);

        // Create the request
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .topP(topP)
                .frequencyPenalty(frequencyPenalty)
                .presencePenalty(presencePenalty)
                .messages(messages)
                .n(1)
                .build();
        ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);

        // Return the response
        return chatCompletion.getChoices().get(0).getMessage().getContent();
    }

    // Determines if a player's rateLimiter is rate limited
    public boolean isRateLimited(UUID player){
        return rateLimiters.containsKey(player) && rateLimiters.get(player).isRateLimited();
    }

    // Increments the number of requests a player has made. Happens when a player makes a request
    public void incrementRequests(UUID player){
        RateLimiter rateLimiter;
        if(rateLimiters.containsKey(player)){
            rateLimiter = rateLimiters.get(player);
        }else{
            rateLimiter = new RateLimiter(player);
            rateLimiters.put(player, rateLimiter);
        }

        rateLimiter.incrementRequests();

        if(!rateLimiterRunning && rateLimiterEnabled){
            rateLimiterTimer(MinecraftGPT.getPlugin());
            rateLimiterRunning = true;
        }
    }

    // Decrements the number of requests all players have made. Happens every minute
    public void decrementCooldowns(){

        // For each rateLimiter, decrement the cooldown
        for(RateLimiter rateLimiter: rateLimiters.values()){
            rateLimiter.decrementCooldown();

            // If the cooldown is less than or equal to 0, remove the rateLimiter. This will reset the number of requests
            if(rateLimiter.getCooldown() <= 0){
                rateLimiters.remove(rateLimiter.getPlayer());
            }

        }
    }

    // Timer that will decrement the cooldowns of all rateLimiters
    private void rateLimiterTimer(JavaPlugin plugin){

        rateLimiterRunning = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                decrementCooldowns();

                if(rateLimiters.isEmpty()){
                    rateLimiterRunning = false;
                    cancel();
                }

            }
        }.runTaskTimer(plugin, 0, 1200L);
    }

    public int getPlayerCooldown(UUID player){
        return rateLimiters.get(player).getCooldown();
    }

    public int getPlayerRequests(UUID player){
        return rateLimiters.get(player).getRequests();
    }

    public String getModel(){
        return model;
    }

}
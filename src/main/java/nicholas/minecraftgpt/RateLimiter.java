package nicholas.minecraftgpt;

import java.util.UUID;

public class RateLimiter {

    private final int maxRequests = MinecraftGPT.getPlugin().getConfig().getInt("rate_limiter.max_requests");
    private int requests;
    private int cooldown = MinecraftGPT.getPlugin().getConfig().getInt("rate_limiter.cooldown");;
    private final UUID player;

    public RateLimiter(UUID player){
        this.player = player;
        this.requests = 0;
    }

    public UUID getPlayer(){
        return player;
    }

    // Get number of current requests
    public int getRequests(){
        return requests;
    }

    // Increase number of current requests
    public void incrementRequests(){
        requests++;
    }

    // Gets the current cooldown time
    public int getCooldown(){
        return cooldown;
    }

    // Decreases the cooldown time, happens every minute
    public void decrementCooldown(){
        cooldown--;
    }

    // Determines if a rateLimiter is rate limited
    public boolean isRateLimited(){
        return requests >= maxRequests;
    }
}

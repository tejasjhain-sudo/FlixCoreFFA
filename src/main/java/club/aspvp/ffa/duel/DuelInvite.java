package club.aspvp.ffa.duel;

import java.util.UUID;

public record DuelInvite(
        UUID senderUUID,
        String senderName,
        UUID targetUUID,
        String targetName,
        String arenaId,
        String kitName,
        long timestamp
) {
    public boolean isExpired(long timeoutSeconds) {
        return (System.currentTimeMillis() - timestamp) > (timeoutSeconds * 1000L);
    }
}

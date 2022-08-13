package me.braunly.localstyledchat.config.data;

import io.github.ladysnake.pal.*;
import me.braunly.localstyledchat.StyledChatMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class LocalChatAbility {
    private static final Identifier id = new Identifier(StyledChatMod.MOD_ID, "in_local_chat");
    private static final PlayerAbility IN_LOCAL_CHAT = Pal.registerAbility(id, SimpleAbilityTracker::new);
    private static final AbilitySource abilitySource = Pal.getAbilitySource(id);

    public static void init() {
        PlayerAbilityEnableCallback.EVENT.register((player, ability, abilitySource) -> {
            if (ability == IN_LOCAL_CHAT) {
                return !IN_LOCAL_CHAT.isEnabledFor(player);
            }
            return true;
        });
    }

    public static void set(PlayerEntity player, boolean state) {
        if (state) {
            abilitySource.grantTo(player, IN_LOCAL_CHAT);
        } else {
            abilitySource.revokeFrom(player, IN_LOCAL_CHAT);
        }
    }

    public static boolean get(PlayerEntity player) {
        return IN_LOCAL_CHAT.isEnabledFor(player);
    }
}
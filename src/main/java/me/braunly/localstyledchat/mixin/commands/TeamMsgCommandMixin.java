package me.braunly.localstyledchat.mixin.commands;

import me.braunly.localstyledchat.StyledChatUtils;
import me.braunly.localstyledchat.config.ConfigManager;
import me.braunly.localstyledchat.ducks.ExtSentMessage;
import me.braunly.localstyledchat.ducks.ExtSignedMessage;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(method = "method_44148", at = @At(value = "HEAD"))
    private static void styledChat_formatOgText(List list, Entity entity, MessageType.Parameters parameters, MessageType.Parameters parameters2, ServerCommandSource serverCommandSource, SignedMessage signedMessage, CallbackInfo ci) {
        var input = ((ExtSignedMessage) (Object) signedMessage).styledChat_getArg("base_input");

        if (input == null) {
            input = StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) signedMessage).styledChat_getOriginal());
            ((ExtSignedMessage) (Object) signedMessage).styledChat_setArg("base_input", input);
        }

        StyledChatUtils.modifyForSending(signedMessage, serverCommandSource, MessageType.TEAM_MSG_COMMAND_INCOMING);

        var config = ConfigManager.getConfig();
        serverCommandSource.sendFeedback(config.getTeamChatSent(((Team) entity.getScoreboardTeam()).getFormattedName(), entity.getDisplayName(), input, serverCommandSource), false);
    }

    @Redirect(method = "method_44148", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_replaceForSelf(ServerPlayerEntity instance, SentMessage message, boolean bl, MessageType.Parameters parameters) {
        if (!ExtSentMessage.getWrapped(message).canVerifyFrom(instance.getUuid())) {
            instance.sendChatMessage(message, bl, parameters);
        }
    }
}

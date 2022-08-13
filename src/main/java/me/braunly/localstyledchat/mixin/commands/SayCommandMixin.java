package me.braunly.localstyledchat.mixin.commands;

import me.braunly.localstyledchat.StyledChatUtils;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SayCommand.class)
public class SayCommandMixin {
    @Inject(method = "method_43657", at = @At("HEAD"))
    private static void styledChat_formatText(PlayerManager playerManager, ServerCommandSource serverCommandSource, SignedMessage signedMessage, CallbackInfo ci) {
        StyledChatUtils.modifyForSending(signedMessage, serverCommandSource, MessageType.SAY_COMMAND);
    }
}

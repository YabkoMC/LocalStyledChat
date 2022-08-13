package me.braunly.localstyledchat.mixin;

import me.braunly.localstyledchat.StyledChatMod;
import me.braunly.localstyledchat.ducks.ExtSentMessage;
import me.braunly.localstyledchat.ducks.ExtSignedMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SentMessage.Profileless.class)
public abstract class SentMessageProfilelessMixin implements ExtSentMessage {
    @Shadow @Final private SignedMessage message;

    @Redirect(method = "method_45095", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType$Parameters;toSerialized(Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/network/message/MessageType$Serialized;"))
    private MessageType.Serialized styledChat_replaceSerialized(MessageType.Parameters instance, DynamicRegistryManager registryManager) {
        var override = ((ExtSignedMessage) (Object) this.message).styledChat_getArg("override");
        if (override != null) {
            var reg = registryManager.get(Registry.MESSAGE_TYPE_KEY);
            return new MessageType.Serialized(reg.getRawId(reg.get(StyledChatMod.MESSAGE_TYPE_ID)), override, null);
        } else {
            return instance.toSerialized(registryManager);
        }
    }

    @Override
    public SignedMessage styledChat_getMessage() {
        return this.message;
    }
}

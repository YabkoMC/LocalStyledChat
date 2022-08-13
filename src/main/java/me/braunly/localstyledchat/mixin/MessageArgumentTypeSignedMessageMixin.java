package me.braunly.localstyledchat.mixin;


import me.braunly.localstyledchat.ducks.ExtPlayNetworkHandler;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(MessageArgumentType.SignedMessage.class)
public class MessageArgumentTypeSignedMessageMixin {
    @Redirect(method = "method_45069", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private MessageDecorator styledChat_returnCached(MinecraftServer instance) {
        return (player, message) -> {
            if (player != null) {
                var cached = ((ExtPlayNetworkHandler) player.networkHandler).styledChat_getLastCached();
                return CompletableFuture.completedFuture(cached != null ? cached : message);
            }
            return CompletableFuture.completedFuture(message);
        };
    }

    /*@Redirect(method = "decorate", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenAccept(Ljava/util/function/Consumer;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> styledChat_replaceFormatting(CompletableFuture<FilteredMessage<SignedMessage>> instance, Consumer<FilteredMessage<SignedMessage>> action) {
        return instance.thenAccept((x) -> {
            ((ExtSignedMessage) (Object) x.raw()).styledChat_setOriginal(this.plain);

            if (x.raw() != x.filtered()) {
                ((ExtSignedMessage) (Object) x.filtered()).styledChat_setOriginal(this.plain);
            }
            action.accept(x);
        });
    }*/
}

package me.braunly.localstyledchat.mixin;

import me.braunly.localstyledchat.StyledChatUtils;
import me.braunly.localstyledchat.ducks.ExtSignedMessage;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(SignedMessage.class)
public class SignedMessageMixin implements ExtSignedMessage {
    /*@Unique
    private String styledChat_original;*/

    @Shadow @Final private MessageBody signedBody;
    @Unique
    private Map<String, Text> styledChat_args = new HashMap<>();

    /*@Override
    public void styledChat_setOriginal(String message) {
        this.styledChat_original = message;
    }*/

    @Override
    public void styledChat_setArg(String name, Text arg) {
        this.styledChat_args.put(name, arg);
    }

    @Override
    public String styledChat_getOriginal() {
        return this.signedBody.content().plain();
    }

    @Override
    public Text styledChat_getArg(String name) {
        return this.styledChat_args.getOrDefault(name, StyledChatUtils.EMPTY_TEXT);
    }

    /*@Inject(method = "<init>(Lnet/minecraft/network/message/MessageHeader;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/network/message/MessageBody;Ljava/util/Optional;)V", at = @At("RETURN"))
    private static void styledChat_setOriginal(MessageHeader messageHeader, MessageSignatureData messageSignatureData, MessageBody messageBody, Optional<Text> optional, CallbackInfo ci) {
        ((ExtSignedMessage) this).styledChat_setOriginal(text.getString());
    }*/
}

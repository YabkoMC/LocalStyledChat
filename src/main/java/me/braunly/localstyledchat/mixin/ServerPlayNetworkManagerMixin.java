package me.braunly.localstyledchat.mixin;


import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import me.braunly.localstyledchat.StyledChatEvents;
import me.braunly.localstyledchat.StyledChatUtils;
import me.braunly.localstyledchat.config.Config;
import me.braunly.localstyledchat.config.ConfigManager;
import me.braunly.localstyledchat.config.data.LocalChatAbility;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Function;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkManagerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text styledChat_replaceDisconnectMessage(Text text) {
        return ConfigManager.getConfig().getLeft(this.player);
    }

    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void styledChat_replaceChatMessage(PlayerManager playerManager, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType playerMessageType, UUID sender, TextStream.Message message) {
        var handlers = StyledChatUtils.getHandlers(this.player);
        Config config = ConfigManager.getConfig();
        var emotes = StyledChatUtils.getEmotes(this.player);

        String rawMessage =  message.getRaw();
        String filteredMessage = message.getFiltered();

        boolean isInLocalChat = config.configData.enableLocalChat && LocalChatAbility.get(player);

        // You might say, that it's useless and you would be kinda right
        // However in case of other mods or vanilla implementing these, it should work without any modifications!
        if (rawMessage.equals(filteredMessage)) {
            rawMessage = StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.invoker().onPreMessage(message.getRaw(), player, false);

            rawMessage = StyledChatUtils.formatMessage(rawMessage, handlers);

            Text rawText = null;
            if (isInLocalChat) {
                rawText = config.getLocalChat(this.player,
                        StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                        ? PlaceholderAPI.parsePredefinedText(TextParser.parse(rawMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                        : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getRaw()), StyledChatUtils.EMOTE_PATTERN, emotes),
                                player, false)
                );
            } else {
                rawText = config.getChat(this.player,
                        StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                        ? PlaceholderAPI.parsePredefinedText(TextParser.parse(rawMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                        : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getRaw()), StyledChatUtils.EMOTE_PATTERN, emotes),
                                player, false)
                );
            }

            if (rawText != null ) {
                Text finalRawText = rawText;
                if (isInLocalChat) {
                    sendToLocalChat(playerManager, serverMessage,
                            (receiver) -> StyledChatEvents.MESSAGE_TO_SEND.invoker().onMessageTo(finalRawText, this.player, receiver, false),
                            playerMessageType,
                            sender,
                            config.configData.localChatRadius
                    );
                } else {
                    playerManager.broadcast(finalRawText, (receiver) -> StyledChatEvents.MESSAGE_TO_SEND.invoker().onMessageTo(finalRawText, this.player, receiver, false), playerMessageType, sender);
                }
            }
        } else {
            rawMessage = StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.invoker().onPreMessage(message.getRaw(), player, false);
            filteredMessage = StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.invoker().onPreMessage(message.getFiltered(), player, true);

            rawMessage = StyledChatUtils.formatMessage(rawMessage, handlers);
            filteredMessage = StyledChatUtils.formatMessage(filteredMessage, handlers);

            Text rawText = null;
            Text filteredText = null;
            if (isInLocalChat) {
                rawText = config.getLocalChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(rawMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getRaw()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, false)
                );
                filteredText = config.getLocalChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(filteredMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getFiltered()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, true)
                );
            } else {
                rawText = config.getChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(rawMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getRaw()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, false)
                );
                filteredText = config.getChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(filteredMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getFiltered()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, true)
                );
            }

            if (rawText != null && filteredText != null) {
                Text finalFilteredText = filteredText;
                Text finalRawText = rawText;
                if (isInLocalChat) {
                    sendToLocalChat(playerManager, finalRawText,
                            (receiver) -> {
                                var filtered = this.player.shouldFilterMessagesSentTo(receiver);
                                return StyledChatEvents.MESSAGE_TO_SEND.invoker().onMessageTo(filtered ? finalFilteredText : finalRawText, this.player, receiver, filtered);
                            },
                            playerMessageType,
                            sender,
                            config.configData.localChatRadius
                    );
                } else {
                    playerManager.broadcast(finalRawText, (receiver) -> {
                        var filtered = this.player.shouldFilterMessagesSentTo(receiver);
                        return StyledChatEvents.MESSAGE_TO_SEND.invoker().onMessageTo(filtered ? finalFilteredText : finalRawText, this.player, receiver, filtered);

                    }, playerMessageType, sender);
                }
            }
        }
    }

    public void sendToLocalChat(PlayerManager playerManager, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType type, UUID sender, int localChatRange) {
        playerManager.getServer().sendSystemMessage(serverMessage, sender);

        for (ServerPlayerEntity serverPlayerEntity : playerManager.getPlayerList()) {
            if (serverPlayerEntity.squaredDistanceTo(player) <= localChatRange) {
                Text text = playerMessageFactory.apply(serverPlayerEntity);
                if (text != null) {
                    serverPlayerEntity.sendMessage(text, type, sender);
                }
            }
        }
    }
}

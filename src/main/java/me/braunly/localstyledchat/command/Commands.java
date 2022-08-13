package me.braunly.localstyledchat.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.braunly.localstyledchat.StyledChatMod;
import me.braunly.localstyledchat.StyledChatUtils;
import me.braunly.localstyledchat.config.ConfigManager;
import me.braunly.localstyledchat.config.data.LocalChatAbility;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
                literal("global").executes(Commands::global)
        );
        dispatcher.register(
                literal("local").executes(Commands::local)
        );

        dispatcher.register(
                literal("localstyledchat")
                        .requires(Permissions.require("localstyledchat.main", true))
                        .executes(Commands::about)

                        .then(literal("reload")
                                .requires(Permissions.require("localstyledchat.reload", 3))
                                .executes(Commands::reloadConfig)
                        )
        );

        dispatcher.register(
                literal("tellform")
                        .requires(Permissions.require("localstyledchat.tellform", 2))

                        .then(argument("targets", EntityArgumentType.players())
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes((context) -> {
                                                    int i = 0;
                                                    Text parsed;

                                                    var ctx = context.getSource().getPlayer() != null ? PlaceholderContext.of(context.getSource().getPlayer()) : PlaceholderContext.of(context.getSource().getServer());

                                                    parsed = StyledChatUtils.formatFor(ctx, context.getArgument("message", String.class));

                                                    for (var player : EntityArgumentType.getPlayers(context, "targets")) {
                                                        player.sendMessage(parsed);
                                                    }

                                                    return i;
                                                }
                                        )
                                )
                        )
        );

    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(Text.literal("Reloaded config!"), false);

            for (var player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                StyledChatUtils.sendAutocompliton(player);
            }
        } else {
            context.getSource().sendError(Text.literal("Error occurred while reloading config! Check console for more information!").formatted(Formatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Local Styled Chat")
                .formatted(Formatting.YELLOW)
                .append(Text.literal(" - " + StyledChatMod.VERSION)
                        .formatted(Formatting.WHITE)
                ), false);

        return 1;
    }

    private static int global(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity sender = context.getSource().getPlayer();
        LocalChatAbility.set(sender, false);
        sender.sendMessage(Text.literal("")
                .append(Text.literal("You connected to ").styled(s -> s.withColor(Formatting.WHITE)))
                .append(Text.literal("GLOBAL").styled(style -> style.withColor(Formatting.WHITE)).styled(style -> style.withBold(true)))
                .append(Text.literal(" channel")), false);
        return 0;
    }

    private static int local(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        LocalChatAbility.set(sender, true);
        sender.sendMessage(Text.literal("")
                .append(Text.literal("You connected to ").styled(s -> s.withColor(Formatting.WHITE)))
                .append(Text.literal("LOCAL").styled(style -> style.withColor(Formatting.YELLOW)).styled(style -> style.withBold(true)))
                .append(Text.literal(" channel")), false);
        return 0;
    }
}

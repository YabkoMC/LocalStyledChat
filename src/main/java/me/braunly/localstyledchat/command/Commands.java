package me.braunly.localstyledchat.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.braunly.localstyledchat.StyledChatMod;
import me.braunly.localstyledchat.StyledChatUtils;
import me.braunly.localstyledchat.config.ConfigManager;
import me.braunly.localstyledchat.config.data.LocalChatAbility;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {

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
                                                        try {
                                                            parsed = StyledChatUtils.formatFor(context.getSource().getPlayer(), context.getArgument("message", String.class));
                                                        } catch (Exception e) {
                                                            parsed = StyledChatUtils.formatFor(context.getSource().getServer(), context.getArgument("message", String.class));

                                                        }

                                                        for (var player : EntityArgumentType.getPlayers(context, "targets")) {
                                                            player.sendSystemMessage(parsed, Util.NIL_UUID);
                                                        }

                                                        return i;
                                                    }
                                            )
                                    )
                            )
            );
        });
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(new LiteralText("Reloaded config!"), false);
        } else {
            context.getSource().sendError(new LiteralText("Error occurred while reloading config! Check console for more information!").formatted(Formatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(new LiteralText("Local Styled Chat")
                .formatted(Formatting.YELLOW)
                .append(new LiteralText(" - " + StyledChatMod.VERSION)
                        .formatted(Formatting.WHITE)
                ), false);

        return 1;
    }

    private static int global(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        LocalChatAbility.set(sender, false);
        sender.sendMessage(new LiteralText("")
                .append(new LiteralText("You connected to ").styled(s -> s.withColor(Formatting.WHITE)))
                .append(new LiteralText("GLOBAL").styled(style -> style.withColor(Formatting.WHITE)).styled(style -> style.withBold(true)))
                .append(new LiteralText(" channel")), false);
        return 0;
    }

    private static int local(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        LocalChatAbility.set(sender, true);
        sender.sendMessage(new LiteralText("")
                .append(new LiteralText("You connected to ").styled(s -> s.withColor(Formatting.WHITE)))
                .append(new LiteralText("LOCAL").styled(style -> style.withColor(Formatting.YELLOW)).styled(style -> style.withBold(true)))
                .append(new LiteralText(" channel")), false);
        return 0;
    }
}

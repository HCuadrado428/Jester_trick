package com.jester.jestertricks.command;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.network.PassivesSyncPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * /jester add &lt;jugadores&gt;      — los convierte en Jester
 * /jester remove &lt;nombre&gt;      — le quita el poder (aunque no esté conectado)
 * /jester list                  — dice quién es Jester
 * /jester clear                 — nadie es Jester (todos pueden usar los objetos)
 * Solo para operadores. Se guarda en jestertricks-server.toml al momento, sin reiniciar.
 */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID)
public final class JesterCommand {

    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("jester")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(JesterCommand::add)))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, b) -> SharedSuggestionProvider.suggest(names(), b))
                                .executes(JesterCommand::remove)))
                .then(Commands.literal("list").executes(JesterCommand::list))
                .then(Commands.literal("clear").executes(JesterCommand::clear)));
    }

    private static List<String> names() {
        return new ArrayList<>(JesterConfig.JESTER_NAMES.get());
    }

    private static void save(CommandSourceStack src, List<String> names) {
        JesterConfig.JESTER_NAMES.set(names);
        JesterConfig.JESTER_NAMES.save();
        // el botón de pasivas y los permisos se actualizan al momento para todos
        for (ServerPlayer p : src.getServer().getPlayerList().getPlayers()) {
            PassivesSyncPacket.sendTo(p);
        }
    }

    private static int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "players");
        List<String> list = names();
        int added = 0;
        for (ServerPlayer p : targets) {
            String n = p.getGameProfile().getName();
            if (list.stream().noneMatch(x -> x.equalsIgnoreCase(n))) {
                list.add(n);
                added++;
                p.displayClientMessage(Component.translatable("commands.jestertricks.you_are_jester")
                        .withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC), false);
            }
        }
        save(ctx.getSource(), list);
        int count = added;
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.jestertricks.added", count, String.join(", ", list)), true);
        return added;
    }

    private static int remove(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        List<String> list = names();
        boolean removed = list.removeIf(x -> x.equalsIgnoreCase(name));
        if (!removed) {
            ctx.getSource().sendFailure(Component.translatable("commands.jestertricks.not_found", name));
            return 0;
        }
        save(ctx.getSource(), list);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.jestertricks.removed", name), true);
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        List<String> list = names();
        Component msg = list.isEmpty()
                ? Component.translatable("commands.jestertricks.list_empty")
                : Component.translatable("commands.jestertricks.list", String.join(", ", list));
        ctx.getSource().sendSuccess(() -> msg, false);
        return list.size();
    }

    private static int clear(CommandContext<CommandSourceStack> ctx) {
        save(ctx.getSource(), new ArrayList<>());
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.jestertricks.cleared"), true);
        return 1;
    }

    private JesterCommand() {}
}

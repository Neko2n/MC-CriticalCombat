package dev.nekotune.critical_combat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CCCommands {

    public static final Command<CommandSourceStack> CMD_LOG_HELD_WEAPON_TYPE = new Command<>() {
        @Override
        public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            final CommandSourceStack source = context.getSource();
            final ServerPlayer player = source.getPlayerOrException();
            final WeaponType type = WeaponTypes.get(player.getMainHandItem().getItem());
            source.sendSuccess(() -> Component.literal("Held weapon type: " + type), false);
            return Command.SINGLE_SUCCESS;
        }
    };

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher,
            final CommandBuildContext context) {
        final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("critical_combat")
                .then(Commands.literal("log")
                        .then(Commands.literal("hand").executes(CMD_LOG_HELD_WEAPON_TYPE).build()).build());

        dispatcher.register(builder);
    }

    private CCCommands() {
    }
}

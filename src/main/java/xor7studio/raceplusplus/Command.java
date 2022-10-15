package xor7studio.raceplusplus;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.util.Xor7IO;

import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    public static void initCommand(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {dispatcher
            .register(literal("run")
            .requires(source -> source.hasPermissionLevel(4))
            .executes(context -> {
                ArgonLibrary.server=context.getSource().getServer();
                Xor7IO.println("command run.");
                return 1;
            }));
        });
    }
}

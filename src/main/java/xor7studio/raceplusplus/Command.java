package xor7studio.raceplusplus;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.Text;
import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.argonlibrary.SingleScoreboard;
import xor7studio.util.Xor7IO;

import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    public static void initCommand(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {dispatcher
            .register(literal("start")
            .requires(source -> source.hasPermissionLevel(4))
            .executes(context -> {
                ArgonLibrary.server=context.getSource().getServer();
                if(!GameRule.getInstance().map3D.start())
                    context.getSource()
                            .getPlayer()
                            .sendMessage(Text.of("无法启动游戏:已经有一个在运行的游戏"),false);
                Xor7IO.println("command run.");
                return 1;
            }));
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {dispatcher
                .register(literal("test")
                        .executes(context -> {
                            ArgonLibrary.server=context.getSource().getServer();
                            SingleScoreboard singleScoreboard=new SingleScoreboard(ArgonLibrary.server.getPlayerManager().getPlayer("MC_XiaoHei"),"aaaa");
                            Xor7IO.println("command run.");
                            return 1;
                        }));
        });
    }
}

package xor7studio.raceplusplus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.util.Xor7IO;
import xor7studio.util.Xor7Runnable;

public class Raceplusplus implements ModInitializer {
    public static final String id="Race Plus Plus";
    @Override
    public void onInitialize() {
        Xor7IO.modId=id;
        new Xor7Runnable(){
            @Override
            public void run() {
                MinecraftServer server=ArgonLibrary.server;
                if(server!=null && server.getPlayerManager()!=null){
                    GameRule.init();
                    Command.initCommand();
                    Config.getInstance().toml.getString("a");
                    Xor7IO.println("Race Plus Plus Mod加载完成.");
                    this.stop();
                }
            }
        }.start(100);
    }
}

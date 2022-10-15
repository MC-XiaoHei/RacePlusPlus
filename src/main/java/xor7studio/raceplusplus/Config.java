package xor7studio.raceplusplus;

import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.util.Xor7Toml;
import xor7studio.util.Xor7IO;

public class Config {
    Xor7Toml toml;
    private static final Config INSTANCE = new Config();
    public static Config getInstance(){return INSTANCE;}
    private Config(){
        Xor7IO.modId=Raceplusplus.id;
        toml=new Xor7Toml("power.toml", ArgonLibrary.server.getRunDirectory().getPath());
        Xor7IO.println("Config loaded");
    }
}

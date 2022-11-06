package xor7studio.raceplusplus;

import com.moandjiezana.toml.Toml;
import xor7studio.util.Xor7IO;
import xor7studio.util.Xor7Toml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {
    Xor7Toml toml;
    private static final Config INSTANCE = new Config();
    public static Config getInstance(){return INSTANCE;}
    public Set<PowerBlock> powerBlocks=new HashSet<>();
    public Set<EffectBlock> effectBlocks=new HashSet<>();
    private Config(){
        Xor7IO.modId=Raceplusplus.id;
        toml=new Xor7Toml(".\\config","rpp-func-block.toml");
    }
    public void load(){
        List<Toml> powerBlocks=toml.getList("registry.powerBlocks"),
                   effectBlocks=toml.getList("registry.effectBlocks");
        Xor7IO.println("Config loaded");
    }

    public static class PowerBlock {
        public PowerBlock(double xPower, double yPower, String block){
            this.xPower=xPower;
            this.yPower=yPower;
            this.block=block;
        }
        public Double xPower,yPower;
        public String block;
    }

    public static class EffectBlock {
        public EffectBlock(int level,int duration, String effect, String block){
            this.level=level;
            this.duration=duration;
            this.effect=effect;
            this.block=block;
        }
        public int level,duration;
        public String block, effect;
    }
}

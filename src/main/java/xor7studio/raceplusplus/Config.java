package xor7studio.raceplusplus;

import com.moandjiezana.toml.Toml;
import xor7studio.util.Xor7File;
import xor7studio.util.Xor7IO;
import xor7studio.util.Xor7Toml;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {
    Xor7Toml func_block,map_cfg;
    private static final Config INSTANCE = new Config();
    public static Config getInstance(){return INSTANCE;}
    public Set<PowerBlock> powerBlocks=new HashSet<>();
    public Set<EffectBlock> effectBlocks=new HashSet<>();
    private Config(){
        Xor7IO.modId=Raceplusplus.id;
        func_block =new Xor7Toml(
                "."+ File.separator+"config"+ File.separator+"rpp",
                "func-block.toml");
        map_cfg =new Xor7Toml(
                "."+ File.separator+"world",
                "map-cfg.toml");
    }
    public File getMapData(){
        return new Xor7File("."+ File.separator+"world","rpp.data").file;
    }
    public void loadAll(){
        List<String> powerBlocksToml= func_block.getList("registry.powerBlocks"),
                   effectBlocksToml= func_block.getList("registry.effectBlocks");
        for(String key:powerBlocksToml)
            powerBlocks.add(func_block.getTable(key).to(PowerBlock.class));
        for(String key:effectBlocksToml)
            effectBlocks.add(func_block.getTable(key).to(EffectBlock.class));
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

package xor7studio.raceplusplus;

import xor7studio.util.Xor7File;
import xor7studio.util.Xor7IO;
import xor7studio.util.Xor7Toml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    public List<String> getScoreboardData(){
        File file=new Xor7File("."+ File.separator+"config"+ File.separator+"rpp","scoreboard.data").file;
        BufferedReader reader = null;
        List<String> res=new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String readStr;
            while ((readStr = reader.readLine()) != null)
                res.add(readStr);
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return res;
    }
    public void loadAll(){
        List<String> powerBlocksToml= func_block.getList("registry.powerBlocks"),
                   effectBlocksToml= func_block.getList("registry.effectBlocks");
        if(powerBlocksToml!=null)
            for(String key:powerBlocksToml)
                powerBlocks.add(func_block.getTable(key).to(PowerBlock.class));
        if(powerBlocksToml!=null)
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

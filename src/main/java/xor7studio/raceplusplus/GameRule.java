package xor7studio.raceplusplus;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.argonlibrary.SingleScoreboard;
import xor7studio.util.Xor7IO;
import xor7studio.util.Xor7Runnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRule {
    private static final GameRule Instance=new GameRule();
    public GameRule() {
    }
    public static GameRule getInstance(){return Instance;}
    public String scoreboardName ="Race Plus Plus";
    public List<String> scoreboardData=Config.getInstance().getScoreboardData();
    public Map3D map3D=new Map3D();
    public int scoreboardRankSize =1, scoreboardRankBasic=1;
    public Map<String, SingleScoreboard> infoScoreboards=new HashMap<>();
    public Map<String,Long> lastVelocityGiven=new HashMap<>();
    public void updateScoreboard(@NotNull ServerPlayerEntity player){
        infoScoreboards.put(player.getName().asString(),
                new SingleScoreboard(player, scoreboardName,scoreboardData.size()+1));
        SingleScoreboard scoreboard=infoScoreboards.get(player.getName().asString());
        PlayerInfo info=map3D.getPlayerInfo(player.getName().asString());
        Time gt=map3D.getGameTime();
        for(int i =0; i< scoreboardData.size(); i++){
            scoreboard.setLine(i+1,scoreboardData.get(scoreboardData.size()-i-1)
                    .replace("${player.name}",player.getName().asString())
                    .replace("${player.pos}",String.valueOf(info.pos))
                    .replace("${player.round}",String.valueOf(info.round))
                    .replace("${player.rank}",String.valueOf(info.rank))
                    .replace("${game.time.sec}",String.valueOf(gt.second))
                    .replace("${game.time.min}",String.valueOf(gt.minute))
                    .replace("${rank.list.start}","")
                    .replace("${rank.list.end}","")
//                    .replace("${rank.list.data1}",info.scoreboardRank.getOrDefault(scoreboardRankBasic+1,"1"))
//                    .replace("${rank.list.data2}",info.scoreboardRank.getOrDefault(scoreboardRankBasic+2,"2"))
//                    .replace("${rank.list.data3}",info.scoreboardRank.getOrDefault(scoreboardRankBasic+3,"3"))
//                    .replace("${rank.list.data4}",info.scoreboardRank.getOrDefault(scoreboardRankBasic+4,"4"))
//                    .replace("${rank.list.data5}",info.scoreboardRank.getOrDefault(scoreboardRankBasic+5,"5"))
            );
            for(Integer key:info.scoreboardRank.keySet())
                scoreboard.setLine(key,info.scoreboardRank.get(key));
        }
    }
    @Contract("_, _ -> new")
    public @NotNull Vec2f parseDirection(float direction, double length){
        double x,z,delta=direction*MathHelper.PI/180.00;
        double cos= MathHelper.cos((float) delta);
        double sin= MathHelper.sin((float) delta);
        z= length*cos;
        x= -length*sin;
        return new Vec2f((float) x, (float) z);
    }
    public void giveVelocity(@NotNull PlayerEntity player, @NotNull Config.PowerBlock powerBlock, float direction){
        if(lastVelocityGiven.containsKey(player.getName().asString()) &&
                System.currentTimeMillis()-lastVelocityGiven.get(player.getName().asString())>50 ||
                !lastVelocityGiven.containsKey(player.getName().asString())){
            player.velocityModified=true;
            Vec2f vec2f=parseDirection(direction, powerBlock.xPower);
            player.takeKnockback(1,-vec2f.x,-vec2f.y);
            player.setVelocity(player.getVelocity().add(0, powerBlock.yPower*0.1,0));
            lastVelocityGiven.put(player.getName().asString(),System.currentTimeMillis());
        }
    }
    public void check(BlockPos blockPos,ServerPlayerEntity player){
        String data = ArgonLibrary.server
                .getOverworld()
                .getBlockState(blockPos)
                        .getBlock()
                        .getName()
                        .getString();
        execute(data,player);
    }
    public StatusEffectInstance getEffect(StatusEffect effect,int duration,int level){return new StatusEffectInstance(effect,duration,level,false,false,false);}
    public void execute(String name,ServerPlayerEntity player){
        for(Config.PowerBlock block:Config.getInstance().powerBlocks)
            if(block.block.equals(name))
                giveVelocity(player, block, player.getYaw());
        for(Config.EffectBlock block:Config.getInstance().effectBlocks)
            if(block.block.equals(name))
                player.addStatusEffect(getEffect((StatusEffect) ArgonLibrary.getFromRegistry(block.effect,Registry.STATUS_EFFECT), block.duration,block.level));
    }
    public void init(){
        Config.getInstance().loadAll();
        Xor7IO.println("GameRule Running.");
        for(String s:scoreboardData)
            if(s.contains("${rank.list.start}"))
                scoreboardRankBasic=scoreboardData.indexOf(s);
        for(int i=scoreboardRankBasic;i<scoreboardData.size();i++)
            if(scoreboardData.get(i).contains("${rank.list.end}"))
                scoreboardRankSize =i-scoreboardRankBasic;
        new Xor7Runnable(){
            @Override
            public void run() {
                if(!ArgonLibrary.server.isRunning()) this.stop();
                List<ServerPlayerEntity> players=new CopyOnWriteArrayList<>(ArgonLibrary.server.getPlayerManager().getPlayerList());
                for (ServerPlayerEntity player : players) {
                    map3D.update(player);
                    map3D.updateRank();
                    updateScoreboard(player);
                    player.sendMessage(Text.of("pos:"+map3D.getPlayerInfo(player.getName().asString()).pos),true);
                    check(player.getBlockPos().add(0,-1,0),player);
                    check(player.getBlockPos().add(0,-2,0),player);
                }
            }
        }.start(50);
    }
}

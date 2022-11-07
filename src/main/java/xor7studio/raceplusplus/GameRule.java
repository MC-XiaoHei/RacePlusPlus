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
    public static GameRule getInstance(){return Instance;}
    public final String SCOREBOARD_NAME="Race Plus Plus";
    public final String SCOREBOARD_EG="";
    public Map3D map3D=new Map3D(".\\world","rpp.data");
    public Map<String, SingleScoreboard> infoScoreboards=new HashMap<>();
    public void updateScoreboard(ServerPlayerEntity player){
        if(!infoScoreboards.containsKey(player.getUuidAsString()))
            infoScoreboards.put(player.getUuidAsString(),
                    new SingleScoreboard(player,SCOREBOARD_NAME));
//        infoScoreboards.
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
        player.velocityModified=true;
        Vec2f vec2f=parseDirection(direction, powerBlock.xPower);
        player.takeKnockback(1,-vec2f.x,-vec2f.y);

        player.setVelocity(player.getVelocity().add(0, powerBlock.yPower*0.1,0));
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
    public StatusEffectInstance getEffect(StatusEffect effect,int duration){return new StatusEffectInstance(effect,duration,0,false,false,false);}
    public void execute(String name,ServerPlayerEntity player){
        for(Config.PowerBlock block:Config.getInstance().powerBlocks)
            if(block.block.equals(name))
                giveVelocity(player, block, player.getYaw());
        for(Config.EffectBlock block:Config.getInstance().effectBlocks)
            if(block.block.equals(name))
                player.addStatusEffect(getEffect((StatusEffect) ArgonLibrary.getFromRegistry(block.effect,Registry.STATUS_EFFECT), block.duration));
    }
    public void init(){
        Config.getInstance().load();
        Xor7IO.println("GameRule Running.");
        new Xor7Runnable(){
            @Override
            public void run() {
                if(!ArgonLibrary.server.isRunning()) this.stop();
                List<ServerPlayerEntity> players=new CopyOnWriteArrayList<>(ArgonLibrary.server.getPlayerManager().getPlayerList());
                for (ServerPlayerEntity player : players) {
                    map3D.update(player);
                    player.sendMessage(Text.of("pos:"+map3D.getPlayerInfo(player.getUuidAsString()).pos),true);
                    check(player.getBlockPos().add(0,-1,0),player);
                    check(player.getBlockPos().add(0,-2,0),player);
                }
            }
        }.start(10);
    }
}

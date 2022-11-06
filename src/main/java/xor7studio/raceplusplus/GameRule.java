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
    public static final String SCOREBOARD_NAME="Race Plus Plus";
    public static final String SCOREBOARD_EG="";
    public static Map3D map3D=new Map3D(".\\world","rpp.data");
    public static Map<String, SingleScoreboard> infoScoreboards=new HashMap<>();
    public static Vec2f parseDirection(float direction,double length){
        double x,z,delta=direction*MathHelper.PI/180.00;
        double cos= MathHelper.cos((float) delta);
        double sin= MathHelper.sin((float) delta);
        z= length*cos;
        x= -length*sin;
        return new Vec2f((float) x, (float) z);
    }
    public static void giveVelocity(@NotNull PlayerEntity player, @NotNull Config.PowerBlock powerBlock, float direction){
        player.velocityModified=true;
        Vec2f vec2f=parseDirection(direction, powerBlock.xPower);
        player.takeKnockback(1,-vec2f.x,-vec2f.y);

        player.setVelocity(player.getVelocity().add(0, powerBlock.yPower*0.1,0));
    }
    public static void check(BlockPos blockPos,PlayerEntity player){
        String data = ArgonLibrary.server
                .getOverworld()
                .getBlockState(blockPos)
                        .getBlock()
                        .getName()
                        .getString();
//        if (jumpPowerBlock.block.equals(data)) {
//            giveVelocity(player, jumpPowerBlock, player.getYaw());
//        }else if(bigJumpPowerBlock.block.equals(data)){
//            giveVelocity(player, bigJumpPowerBlock, player.getYaw());
//        }else if() {
//
//        }
    }
    public static StatusEffectInstance getEffect(StatusEffect effect,int duration){return new StatusEffectInstance(effect,duration,0,false,false,false);}
    public static void execute(String name,ServerPlayerEntity player){
        for(Config.PowerBlock block:Config.getInstance().powerBlocks)
            if(block.block.equals(name))
                giveVelocity(player, block, player.getYaw());
        for(Config.EffectBlock block:Config.getInstance().effectBlocks)
            if(block.block.equals(name))
                player.addStatusEffect(getEffect((StatusEffect) ArgonLibrary.getFromRegistry(block.effect,Registry.STATUS_EFFECT), block.duration));
    }
    public static void init(){
        Config.getInstance().load();

        Xor7IO.println("GameRule Running.");
        new Xor7Runnable(){
            @Override
            public void run() {
                if(!ArgonLibrary.server.isRunning()) this.stop();
                List<ServerPlayerEntity> players=new CopyOnWriteArrayList<>(ArgonLibrary.server.getPlayerManager().getPlayerList());
                for (PlayerEntity player : players) {
                    map3D.update(player);

                    player.sendMessage(Text.of("pos:"+map3D.getPlayerInfo(player.getUuidAsString()).pos),true);
                    check(player.getBlockPos().add(0,-1,0),player);
                    check(player.getBlockPos().add(0,-2,0),player);
                }
            }
        }.start(10);
    }
}

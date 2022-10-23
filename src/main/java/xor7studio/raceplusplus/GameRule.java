package xor7studio.raceplusplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;
import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.util.Xor7IO;
import xor7studio.util.Xor7Runnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRule {
    public static Map3D map3D=new Map3D(".","test.txt");
    public static Power JumpPower=new Power(0.5,0.1);
    public static Vec2f parseDirection(float direction,double length){
        double x,z,delta=direction*MathHelper.PI/180.00;
        double cos= MathHelper.cos((float) delta);
        double sin= MathHelper.sin((float) delta);
        z= length*cos;
        x= -length*sin;
        return new Vec2f((float) x, (float) z);
    }
    public static void giveVelocity(@NotNull PlayerEntity player, @NotNull Power power, float direction){
        player.velocityModified=true;
        Vec2f vec2f=parseDirection(direction,power.xPower);
        player.takeKnockback(1,-vec2f.x,-vec2f.y);
        player.setVelocity(player.getVelocity().add(0,power.yPower*0.1,0));
    }
    public static void check(BlockPos blockPos,PlayerEntity player){
        String data = ArgonLibrary.server
                .getOverworld()
                .getBlockState(blockPos)
                        .getBlock()
                        .getName()
                        .getString();
        Xor7IO.println(data);
        switch (data) {

            case "Block of Diamond" -> giveVelocity(player, JumpPower,player.getYaw());
        }
    }
    public static void init(){
        Xor7IO.println("GameRule Running.");
        new Xor7Runnable(){
            @Override
            public void run() {
                if(!ArgonLibrary.server.isRunning()) this.stop();
                List<ServerPlayerEntity> players=new CopyOnWriteArrayList<>(ArgonLibrary.server.getPlayerManager().getPlayerList());
                for (PlayerEntity player : players) {
                    Xor7IO.println(String.valueOf(map3D.inWhichSection(1,player.getPos())));
                    check(player.getBlockPos().add(0,-1,0),player);
                }
            }
        }.start(1);
    }
    public static class Power{
        public Power(double xPower,double yPower){
            this.xPower=xPower;
            this.yPower=yPower;
        }
        public Double xPower,yPower;
    }
}

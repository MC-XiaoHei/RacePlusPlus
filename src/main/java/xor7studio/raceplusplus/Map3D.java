package xor7studio.raceplusplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import xor7studio.argonlibrary.ArgonLibrary;
import xor7studio.util.Xor7File;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map3D {
    private final Map<Integer, Line3D> sections;
    private Map<String,PlayerInfo> playersInfo;
    public int roundSectionNum,roundNum,mapLength;
    public long startTime=0;
    private boolean runFlag=false;
    public Map3D(String path,String filename){
        sections=new HashMap<>();
        playersInfo=new HashMap<>();
        sections.put(0,new Line3D(new Vec3d(0,0,0),new Vec3d(0,0,0),0));
        try {
            Scanner scanner = new Scanner(new Xor7File(path,filename).file);
            roundNum=scanner.nextInt();
            roundSectionNum=scanner.nextInt();
            scanner.nextLine();
            Vec3d lastPoint=readPoint(new Scanner(scanner.nextLine()));
            double lastPreSum=0;
            int j=1;
            for(int i=0;i<roundNum;i++){
                for(int k=0;k<roundSectionNum;k++) {
                    Vec3d p=readPoint(new Scanner(scanner.nextLine()));
                    Line3D sec=new Line3D(lastPoint,p,lastPreSum);
                    sections.put(j,sec);
                    lastPreSum=sec.preSum;
                    lastPoint=p;
                    j++;
                }
                scanner.close();
                if(i<roundNum-1){
                    scanner = new Scanner(new Xor7File(path,filename).file);
                    scanner.nextInt();
                    scanner.nextInt();
                    scanner.nextLine();
                    Vec3d p=readPoint(new Scanner(scanner.nextLine()));
                    Line3D sec=new Line3D(lastPoint,p,lastPreSum);
                    sections.put(j,sec);
                    lastPreSum=sec.preSum;
                    lastPoint=p;
                    j++;
                }
            }
            mapLength= (int) lastPreSum+1;
            scanner.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public boolean start(){
        if(runFlag) return false;
        runFlag=true;
        startTime=System.currentTimeMillis();
        playersInfo=new HashMap<>();
        List<ServerPlayerEntity> players=new CopyOnWriteArrayList<>(ArgonLibrary.server.getPlayerManager().getPlayerList());
        Vec3d p=sections.get(1).begin;
        for (PlayerEntity player : players)
            player.teleport(p.x,p.y,p.z);
        return true;
    }
    public Time getTimeDifference(long begin,long end){
        Time res=new Time();
        long difference=end-begin;
        res.second=difference/1000.00;
        res.minute=((int)res.second)/60;
        res.second-=res.minute*60;
        return res;
    }
    public Time getGameTime(){
        return getGameTime(System.currentTimeMillis());
    }
    public Time getGameTime(long time){
        return getTimeDifference(startTime,System.currentTimeMillis());
    }
    public void update(@NotNull PlayerEntity player){
        String uuid=player.getUuidAsString();
        if(!playersInfo.containsKey(uuid))
            playersInfo.put(uuid,new PlayerInfo());
        PlayerInfo info=new PlayerInfo();
        if(info.pos==-1) {
            info.complete=true;
            return;
        }
        info.section=inWhichSection(getPlayerInfo(uuid).section,player.getPos());
        info.pos=getPos(player, info.section);
        if(info.pos==-1) return;
        info.round=info.pos/roundSectionNum;
        for(int i=1;i<=info.pos;i++)
            if(!info.arriveTime.containsKey(i))
                info.arriveTime.put(i,System.currentTimeMillis());
        for(int i=info.pos+1;i<=mapLength;i++)
            info.arriveTime.remove(i);
        playersInfo.replace(uuid,info);
    }
    public PlayerInfo getPlayerInfo(String uuid){
        return playersInfo.get(uuid);
    }
    public Vec3d readPoint(@NotNull Scanner scanner){
        return new Vec3d(scanner.nextDouble(),scanner.nextDouble(),scanner.nextDouble());
    }
    public Line3D getSection(int num) {
        return sections.get(num);
    }
    public boolean inSection(int num,Vec3d p){
        try{
            return getSection(num).getPos(p)>=0 &&
                    getSection(num+1).getPos(p)<0;
        }catch (NullPointerException e){
            return true;
        }
    }
    public int inWhichSection(int basic, Vec3d p){
        for(int i=0;i<16384;){
            basic+=i;
            if(basic>=1 && inSection(basic,p)) return basic;
            i=i>0?-1-i:1-i;
        }return -1;
    }
    public int getPos(@NotNull PlayerEntity player,int sec){
        return getPos(player.getPos(),sec);
    }
    public int getPos(@NotNull Vec3d p,int sec){
        Line3D section=getSection(sec);
        try {
            return (int) (section.getPos(p)+section.preSum-section.length);
        }catch (NullPointerException e){
            return -1;
        }
    }
}

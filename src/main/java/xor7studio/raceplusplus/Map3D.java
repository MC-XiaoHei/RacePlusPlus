package xor7studio.raceplusplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import xor7studio.argonlibrary.ArgonLibrary;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map3D {
    private final Map<Integer, Line3D> sections;
    public Map<String,PlayerInfo> playersInfo;
    public Map<Integer,String> ranks;
    public int roundSectionNum,roundNum,mapLength;
    public long startTime=0;
    private boolean runFlag=false;
    public Map3D(){
        sections=new HashMap<>();
        playersInfo=new TreeMap<>();
        sections.put(0,new Line3D(new Vec3d(0,0,0),new Vec3d(0,0,0),0));
        try {
            Scanner scanner = new Scanner(Config.getInstance().getMapData());
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
                    scanner = new Scanner(Config.getInstance().getMapData());
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
    public void updateRank(){
        Map<String,Integer> pos=new TreeMap<>();
        for(String key: playersInfo.keySet()){
            PlayerInfo info=playersInfo.get(key);
            PlayerEntity player=ArgonLibrary.getServerPlayer(key);
            if(player != null)
                pos.put(player.getName().asString(),info.pos);
        }
        List<Map.Entry<String,Integer>> list = new ArrayList<>(pos.entrySet());
        list.sort(Comparator.comparingInt(Map.Entry::getValue));
        Map<Integer,String> newRanks=new HashMap<>();
        int i=0;
        for(Map.Entry<String,Integer> entry:list){
            newRanks.put(list.size()-i,entry.getKey());
            playersInfo.get(entry.getKey()).rank=list.size()-i;
            i++;
        }
        ranks=newRanks;
    }
    public boolean start(){
        if(runFlag) return false;
        runFlag=true;
        startTime=System.currentTimeMillis();
        playersInfo=new TreeMap<>();
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
        return getTimeDifference(startTime,time);
    }
    public void update(@NotNull PlayerEntity player){
        String name=player.getName().asString();
        if(!playersInfo.containsKey(name))
            playersInfo.put(name,new PlayerInfo());
        PlayerInfo info=new PlayerInfo();
        if(info.pos==-1) {
            info.complete=true;
            return;
        }
        info.section=inWhichSection(getPlayerInfo(name).section,player.getPos());
        info.pos=getPos(player, info.section);
        if(info.pos==-1) return;
        info.round=info.pos/roundSectionNum;
        long time=System.currentTimeMillis();
        for(int i=1;i<=info.pos;i++)
            if(!info.arriveTime.containsKey(i))
                info.arriveTime.put(i,time);
            else time=info.arriveTime.get(i);
        for(int i=info.pos+1;i<=mapLength;i++)
            info.arriveTime.remove(i);
        playersInfo.replace(name,info);
        updateRank();
        int rank=getPlayerInfo(player.getName().asString()).rank;
        int tmp=GameRule.getInstance().scoreboardRankSize /2;
        int start=rank>tmp?rank-tmp:1;
        info.scoreboardRank=new HashMap<>();
        for(int i = 0; i<=GameRule.getInstance().scoreboardRankSize; i++){
            int n=GameRule.getInstance().scoreboardRankBasic-i;
            String t=ranks.get(start+i);
            if(t==null) t="";
            info.scoreboardRank.put(n-1,t);
        }
        playersInfo.replace(name,info);
    }
    public PlayerInfo getPlayerInfo(String name){
        return playersInfo.get(name);
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
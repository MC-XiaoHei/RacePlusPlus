package xor7studio.raceplusplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import xor7studio.util.Xor7File;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Map3D {
    private final Map<Integer, Line3D> sections;
    public int roundSectionNum,roundNum;
    public Map3D(String path,String filename){
        sections=new HashMap<>();
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
            scanner.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
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
    public int getPos(@NotNull PlayerEntity player){
        return getPos(player.getPos());
    }
    public int getPos(@NotNull Vec3d p){
        Line3D section=getSection(inWhichSection(1,p));
        return (int) (section.getPos(p)+section.preSum-section.length);
    }
}

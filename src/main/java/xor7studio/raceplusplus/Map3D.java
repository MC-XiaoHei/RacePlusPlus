package xor7studio.raceplusplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import xor7studio.util.Xor7File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Map3D {
    private List<Line3D> sections;
    private List<Double> sectionLengthPreSum;
    public int roundSectionNum,roundNum;
    public Map3D(String path,String filename){
        List<Vec3d> points=new ArrayList<>();
        sections=new ArrayList<>();
        sectionLengthPreSum=new ArrayList<>();
        roundSectionNum=0;
        try {
            Scanner scanner = new Scanner(new Xor7File(path,filename).file);
            roundNum=scanner.nextInt();
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                Scanner s = new Scanner(scanner.nextLine());
                if(!s.hasNextDouble()) break;
                double x,y,z;
                x=s.nextDouble();
                y=s.nextDouble();
                z=s.nextDouble();
                points.add(roundSectionNum,new Vec3d(x,y,z));
                if(roundSectionNum>0){
                    sections.add(roundSectionNum-1,new Line3D(points.get(roundSectionNum-1),points.get(roundSectionNum)));
                    if(roundSectionNum>1)
                        sectionLengthPreSum.add(roundSectionNum,sections.get(roundSectionNum-2).length+sections.get(roundSectionNum-1).length);
                    else
                        sectionLengthPreSum.add(roundSectionNum,sections.get(0).length);
                }else
                    sectionLengthPreSum.add(0.00);
                roundSectionNum++;
            }
            scanner.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        roundSectionNum--;
    }
    public Line3D getSection(int num) {
        return sections.get(num-1);
    }
    public boolean inSection(int num,Vec3d p){
        return getSection(num).getPos(p)>=0 &&
                getSection(num+1).getPos(p)<0;
    }
    public double getPreSum(int num){
        return sectionLengthPreSum.get(num%roundSectionNum)+
                sectionLengthPreSum.get(roundSectionNum) * (num/roundSectionNum);
    }
    public int inWhichSection(int basic, Vec3d p){
        int flag=1;
        for(int i=1;i<=roundSectionNum;i++){
            if(basic>0 && inSection(basic,p)) return basic;
            basic+=flag*i;
            flag*=-1;
        }
        return -1;
    }
    public int getPos(PlayerEntity player){
        
    }
}

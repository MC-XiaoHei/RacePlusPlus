package xor7studio.raceplusplus;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class Line3D {
    public final double length,halfLen, doubleLen,preSum;
    public final Vec3d begin,end;
    public Line3D(@NotNull Vec3d begin,@NotNull Vec3d end,double lastPreSum){
        this.begin=begin;
        this.end=end;
        length=begin.distanceTo(end);
        halfLen=0.50*length;
        doubleLen =2.00*length;
        preSum=lastPreSum+length;
    }
    public double getPos(@NotNull Vec3d p){
        double distanceToBegin=p.distanceTo(begin);
        double distanceToEnd=p.distanceTo(end);
        if(p.distanceTo(new Vec3d(begin.x*2-end.x,begin.y*2-end.y,begin.z*2-end.z))<distanceToEnd) return -1;
        return ((distanceToBegin+distanceToEnd) *
                (distanceToBegin-distanceToEnd) /
                doubleLen) + halfLen;
    }
}

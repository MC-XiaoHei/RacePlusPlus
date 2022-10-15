package xor7studio.raceplusplus;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class Line3D {
    public final double length,halfLen, doubleLen;
    public final Vec3d begin,end;
    public Line3D(@NotNull Vec3d begin,@NotNull Vec3d end){
        this.begin=begin;
        this.end=end;
        length=begin.distanceTo(end);
        halfLen=0.50*length;
        doubleLen =2.00*length;
    }
    public double getPos(@NotNull Vec3d p){
        double distanceToBegin=p.distanceTo(begin);
        double distanceToEnd=p.distanceTo(end);
        return ((distanceToBegin+distanceToEnd) *
                (distanceToBegin-distanceToEnd) /
                doubleLen) + halfLen;
        /*
           Begin
            |\
            | \
            |  \
            |
            |
            |
         */
    }
}

package xor7studio.raceplusplus;

import java.util.HashMap;
import java.util.Map;

public class PlayerInfo {
    public static Map<Integer,String> ranks=new HashMap<>();
    public int pos=0;
    public int section=0;
    public int round=0;
    public int rank;
    public Map<Integer,Long> arriveTime=new HashMap<>();
    public boolean complete=false;
}

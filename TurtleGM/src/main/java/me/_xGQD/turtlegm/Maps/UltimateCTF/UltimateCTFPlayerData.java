package me._xGQD.turtlegm.Maps.UltimateCTF;

import me._xGQD.turtlegm.Maps.CTF.CTFPlayerData;

public class UltimateCTFPlayerData extends CTFPlayerData {
    public int perk;
    public boolean inTimeWarp = false;
    public UltimateCTFPlayerData(int team){
        super(team);
        perk = 0;
        inTimeWarp = false;
    }

}

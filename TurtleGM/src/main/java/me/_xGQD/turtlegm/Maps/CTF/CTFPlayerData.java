package me._xGQD.turtlegm.Maps.CTF;

import me._xGQD.turtlegm.Maps.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CTFPlayerData extends PlayerData {
    public int gold;
    public UUID lastHit;
    public List<String> buffs;
    public boolean spawnProt;
    public boolean hasFlag;

    public CTFPlayerData(int team) {
        super(team);
        this.gold = 0;
        this.lastHit = null;
        this.buffs = new ArrayList<>();
        this.spawnProt = false;
        this.hasFlag = false;
    }
}

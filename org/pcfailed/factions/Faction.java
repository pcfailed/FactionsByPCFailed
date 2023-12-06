// 
// Decompiled by Procyon v0.5.36
// 

package org.pcfailed.factions;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Faction
{
    protected String name;
    protected UUID leader;
    protected double power;
    protected List<String> allies;
    protected List<String> wars;
    
    public Faction(final String name) {
        this.allies = new ArrayList<String>();
        this.wars = new ArrayList<String>();
        this.name = name;
    }
    
    public Faction(final String name, final UUID sender, final double power, final List<String> allies, final List<String> wars) {
        this.allies = new ArrayList<String>();
        this.wars = new ArrayList<String>();
        this.name = name;
        this.leader = sender;
        this.power = power;
        this.allies = allies;
        this.wars = wars;
    }
    
    public void addPower(final double q) {
        this.power += q;
    }
    
    public void removePower(final double q) {
        this.power -= q;
    }
    
    public void setPower(final double power) {
        this.power = power;
    }
    
    public void addAlly(final String fname) {
        this.allies.add(fname);
    }
    
    public void removeAlly(final String fname) {
        this.allies.remove(fname);
    }
    
    public String serialize() {
        String serialallies = "";
        String serialwars = "";
        for (final String string : this.allies) {
            if (serialallies.equals("")) {
                serialallies = String.valueOf(serialallies) + string;
            }
            else {
                serialallies = String.valueOf(serialallies) + "`" + string;
            }
        }
        for (final String string : this.wars) {
            if (serialwars.equals("")) {
                serialwars = String.valueOf(serialwars) + string;
            }
            else {
                serialwars = String.valueOf(serialwars) + "`" + string;
            }
        }
        String serialfaction = "";
        serialfaction = this.name;
        serialfaction = String.valueOf(serialfaction) + "/" + this.leader;
        serialfaction = String.valueOf(serialfaction) + "/" + this.power;
        serialfaction = String.valueOf(serialfaction) + "/" + serialallies;
        serialfaction = String.valueOf(serialfaction) + "/" + serialwars;
        return serialfaction;
    }
    
    public static Faction deserialize(final String faction) {
        final List<String> allies = new ArrayList<String>();
        final List<String> wars = new ArrayList<String>();
        final String[] splitf = faction.split("/");
        final String[] allies2 = splitf[3].split("`");
        final String[] wars2 = splitf[4].split("`");
        String[] array;
        for (int length = (array = allies2).length, i = 0; i < length; ++i) {
            final String string = array[i];
            allies.add(string);
        }
        String[] array2;
        for (int length2 = (array2 = wars2).length, j = 0; j < length2; ++j) {
            final String string = array2[j];
            wars.add(string);
        }
        final Faction fac = new Faction(splitf[0], UUID.fromString(splitf[1]), Double.parseDouble(splitf[2]), allies, wars);
        return fac;
    }
}

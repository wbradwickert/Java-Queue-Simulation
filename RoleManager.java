package edu.ilstu;

import java.util.Random;

/**
 * Manages role selection for players
 * 
 * @author Mary Elaine Califf
 */
public class RoleManager
{
    private Random randGenerator;
    private final int[] rolePercent = { 10, 20, 60, 80, 100 };
    private final String[] roleNames = { "Tank", "Healer", "DPS", "DPS/Tank", "DPS/Heal" };
    private final int tankDPSLikelihood;
    private final int healerDPSLikelihood;

    /**
     * Constructor
     * 
     * @param randGen       The Random object for the simulation
     * @param tankPercent   The chance out of 100 that a DPS/Tank will pick tank
     * @param healerPercent The chance out of 100 that a DPS/Heal will pick healer
     */
    public RoleManager(Random randGen, int tankPercent, int healerPercent)
    {
        randGenerator = randGen;
        tankDPSLikelihood = tankPercent;
        healerDPSLikelihood = healerPercent;

    }

    /**
     * Randomly chooses the role for a player given the assumed percentages
     * 
     * @return A String with the player's role choice
     */
    public String getBaseRole()
    {
        // first get the base role
        int baseRoleValue = randGenerator.nextInt(100); // number between 0 and 99
        boolean found = false;
        int index = 0;
        while (!found)
        {
            if (baseRoleValue < rolePercent[index])
                found = true;
            else
                index++;
        }
        return roleNames[index];
    }

    /**
     * Randomly choose between tank and DPS given the simulation configuration
     * 
     * @return "Tank" if the player chooses tank and "DPS" if they choose DPS
     */
    public String getTankDPSChoice()
    {
        if (randGenerator.nextInt(100) < tankDPSLikelihood)
            return "Tank";
        else
            return "DPS";
    }

    /**
     * Randomly choose between healer and DPS given the simulation configuration
     * 
     * @return "Healer" if the player chooses healer and "DPS" if they choose DPS
     */
    public String getHealerDPSChoice()
    {
        if (randGenerator.nextInt(100) < healerDPSLikelihood)
            return "Healer";
        else
            return "DPS";
    }

}

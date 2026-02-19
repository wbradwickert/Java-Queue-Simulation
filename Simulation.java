package edu.ilstu;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Class to manage simulation of a solo instance queue
 * 
 * @author Mary Elaine Califf and Brad Wickert
 */
public class Simulation
{
    public final int MAX_PLAYERS_PER_TICK = 8;
    private Queue tankQueue;
    private Queue healerQueue;
    private Queue dpsQueue;
    private PrintWriter reportFile;
    private Random randGenerator;
    private RoleManager roleMgr;
    private int simLength;
    private int nextPlayerNum;

    /**
     * Default constructor
     */
    public Simulation()
    {
    	this.tankQueue = new Queue();
    	this.healerQueue = new Queue();
    	this.dpsQueue = new Queue();
    }

    /**
     * Set up the simulation given the configuration data
     * 
     * Configuration files hold the following data, with one item per line:
     * 
     * 1) seed for the random number generator, 2) chance out of 100 that a DPS/Tank
     * will choose to queue as a tank, 3) chance out of 100 that a DPS/Heal will
     * choose to queue as a healer, 4) number of ticks to run the simulation
     * 
     * @param configFileName The name of the configuration file to use for this
     *                       simulation run
     */
    public void setup(String configFileName)
    {
        try
        {
            Scanner input = new Scanner(new File(configFileName));
            long seed = input.nextLong();
            int tankPercent = input.nextInt();
            int healPercent = input.nextInt();
            simLength = input.nextInt();
            input.close();

            // set up the random number generator, using the specified seed if one was
            // provided
            if (seed != 0)
                randGenerator = new Random(seed);
            else
                randGenerator = new Random();

            // set up the role manager with the configuration data
            roleMgr = new RoleManager(randGenerator, tankPercent, healPercent);
            nextPlayerNum = 1;
            reportFile = new PrintWriter("simReport.txt");

        }
        catch (FileNotFoundException e)
        {
            System.err.println("Could not open configuration file: " + configFileName);
            System.err.println("Ending Simulation");
            System.exit(1);
        }
    }

    /**
     * Run the simulation
     */
    public void runSimulation()
    {
    	//Declare all int variables.
        int maxTankLength = 0;
        int maxHealLength = 0;
        int maxDPSLength = 0;
        int totalTankWait = 0;
        int totalHealWait = 0;
        int totalDPSWait = 0;
        int maxTankWait = 0;
        int maxHealWait = 0;
        int maxDPSWait = 0;
        int numInstancesStarted = 0;


        //Declare arrays to keep track of wait times for players who start instances
        int[] tankWaitTimes = new int[10000];
        int[] healWaitTimes = new int[10000];
        int[] dpsWaitTimes = new int[10000];

        //Start tick for loop
        for (int tick = 1; tick <= simLength; tick++) 
        {
            int playersEnteringQueue = randGenerator.nextInt(MAX_PLAYERS_PER_TICK) + 1;

            //For loop checking each player entering the queue.
            for (int i = 0; i < playersEnteringQueue; i++) {
                String baseRole = roleMgr.getBaseRole();
                String queueRole = baseRole;
                
                //Checks base role
                if (baseRole.equals("DPS/Tank")) {
                    queueRole = roleMgr.getTankDPSChoice();
                } else if (baseRole.equals("DPS/Heal")) {
                    queueRole = roleMgr.getHealerDPSChoice();
                }

                Player player = new Player(nextPlayerNum, queueRole, tick);
                nextPlayerNum++;

                //Checks the players role in queue.
                if (queueRole.equals("DPS")) {
                    dpsQueue.enqueue(player);
                    reportFile.println("Player " + player.getPlayerNum() + " (" + baseRole + ") queued as DPS at tick " + tick);
                } else if (queueRole.equals("Tank")) {
                    tankQueue.enqueue(player);
                    reportFile.println("Player " + player.getPlayerNum() + " (" + baseRole + ") queued as Tank at tick " + tick);
                } else if (queueRole.equals("Healer")) {
                    healerQueue.enqueue(player);
                    reportFile.println("Player " + player.getPlayerNum() + " (" + baseRole + ") queued as Healer at tick " + tick);
                }
            }

            //Checks if there are enough players to start an instance, and if so, updates statistics and dequeues the players.
            if (tankQueue.getSize() > 0 && healerQueue.getSize() > 0 && dpsQueue.getSize() >= 3) {
                Player tank = tankQueue.dequeue();
                Player healer = healerQueue.dequeue();
                Player dps1 = dpsQueue.dequeue();
                Player dps2 = dpsQueue.dequeue();
                Player dps3 = dpsQueue.dequeue();

                int tankWait = tick - tank.getArrivalTime();
                int healWait = tick - healer.getArrivalTime();
                int dps1Wait = tick - dps1.getArrivalTime();
                int dps2Wait = tick - dps2.getArrivalTime();
                int dps3Wait = tick - dps3.getArrivalTime();

                totalTankWait += tankWait;
                totalHealWait += healWait;
                totalDPSWait += dps1Wait + dps2Wait + dps3Wait;

                maxTankWait = Math.max(maxTankWait, tankWait);
                maxHealWait = Math.max(maxHealWait, healWait);
                maxDPSWait = Math.max(maxDPSWait, Math.max(dps1Wait, Math.max(dps2Wait, dps3Wait)));

                tankWaitTimes[numInstancesStarted] = tankWait;
                healWaitTimes[numInstancesStarted] = healWait;
                dpsWaitTimes[numInstancesStarted * 3] = dps1Wait;
                dpsWaitTimes[numInstancesStarted * 3 + 1] = dps2Wait;
                dpsWaitTimes[numInstancesStarted * 3 + 2] = dps3Wait;

                reportFile.println("Instance started at " + tick + " with " + tank.getPlayerNum() + ", " + healer.getPlayerNum() + ", " + dps1.getPlayerNum() + ", " + dps2.getPlayerNum() + ", " + dps3.getPlayerNum());

                numInstancesStarted++;
            }

            maxTankLength = Math.max(maxTankLength, tankQueue.getSize());
            maxHealLength = Math.max(maxHealLength, healerQueue.getSize());
            maxDPSLength = Math.max(maxDPSLength, dpsQueue.getSize());
        }
        printStats(numInstancesStarted, maxTankLength, maxHealLength, maxDPSLength, totalTankWait, totalHealWait, totalDPSWait, maxTankWait, maxHealWait, maxDPSWait, tankWaitTimes, healWaitTimes, dpsWaitTimes);
    }
    
    /**
     * printStatistics method that prints the final statements in the simReport file. It shows all of the wait times in their totals and averages.
     * @param numInstancesStarted
     * @param maxTankLength
     * @param maxHealLength
     * @param maxDPSLength
     * @param totalTankWait
     * @param totalHealWait
     * @param totalDPSWait
     * @param maxTankWait
     * @param maxHealWait
     * @param maxDPSWait
     * @param tankWaitTimes
     * @param healWaitTimes
     * @param dpsWaitTimes
     */
    private void printStats(int numInstancesStarted, int maxTankLength, int maxHealLength, int maxDPSLength, int totalTankWait, int totalHealWait, int totalDPSWait, int maxTankWait, int maxHealWait, int maxDPSWait, int[] tankWaitTimes, int[] healWaitTimes, int[] dpsWaitTimes) 
    {
    	//Declaring and initializing double and int variables.
    	double avgTankWait;
        double avgHealWait;
        double avgDPSWait;
        double avgAllPlayersWait;
        int maxDPSQueueWait = 0;
        int maxTankQueueWait = 0;
        int maxHealerQueueWait = 0;
        int totalDPSQueueWait = 0;
        int totalTankQueueWait = 0;
        int totalHealerQueueWait = 0;
        int finalDPSQueueLength = dpsQueue.getSize();
        int finalTankQueueLength = tankQueue.getSize();
        int finalHealerQueueLength = healerQueue.getSize();

        //Checks if there are any instances.
        if (numInstancesStarted == 0) 
        {
            avgTankWait = 0;
            avgHealWait = 0;
            avgDPSWait = 0;
            avgAllPlayersWait = 0;
        }
        
        //If there are, calculate the averages for the wait times.
        else 
        {
            avgTankWait = (double) totalTankWait / numInstancesStarted;
            avgHealWait = (double) totalHealWait / numInstancesStarted;
            avgDPSWait = (double) totalDPSWait / (numInstancesStarted * 3);
            avgAllPlayersWait = (double) (totalTankWait + totalHealWait + totalDPSWait) / (numInstancesStarted * 5);
        }

        //These for loops will dequeue the Player data into the temporary list so we can calculate the times.
        List<Player> dpsList = new ArrayList<>();
        while (!dpsQueue.isEmpty()) 
        {
            dpsList.add(dpsQueue.dequeue());
        }
        
        //Loops for each player in dpsList.
        for (Player player : dpsList) 
        {
            int waitTime = simLength - (player.getArrivalTime() - 1);
            totalDPSQueueWait += waitTime;
            if (waitTime > maxDPSQueueWait) 
            {
                maxDPSQueueWait = waitTime;
            } 
            //Re-enqueue Player data back into queue unchanged.
            dpsQueue.enqueue(player);
        }

        List<Player> tankList = new ArrayList<>();
        while (!tankQueue.isEmpty()) 
        {
            tankList.add(tankQueue.dequeue());
        }
        //Loops for each player in tankList.
        for (Player player : tankList) 
        {
            int waitTime = simLength - (player.getArrivalTime() - 1);
            totalTankQueueWait += waitTime;
            if (waitTime > maxTankQueueWait) 
            {
                maxTankQueueWait = waitTime;
            }
            tankQueue.enqueue(player);
        }

        List<Player> healerList = new ArrayList<>();
        while (!healerQueue.isEmpty()) 
        {
            healerList.add(healerQueue.dequeue());
        }
        //Loops for each player in healerList.
        for (Player player : healerList) {
            int waitTime = simLength - (player.getArrivalTime() - 1);
            totalHealerQueueWait += waitTime;
            if (waitTime > maxHealerQueueWait) 
            {
                maxHealerQueueWait = waitTime;
            }
            healerQueue.enqueue(player);
        }

        //Calculates the average queue wait times for each three player types.
        double avgDPSQueueWait = finalDPSQueueLength == 0 ? 0 : (double) totalDPSQueueWait / finalDPSQueueLength;
        double avgTankQueueWait = finalTankQueueLength == 0 ? 0 : (double) totalTankQueueWait / finalTankQueueLength;
        double avgHealerQueueWait = finalHealerQueueLength == 0 ? 0 : (double) totalHealerQueueWait / finalHealerQueueLength;
        
        reportFile.println("\nInstances started: " + numInstancesStarted + "\n");
        reportFile.println("Statistics for Players Who Started Instances\n");

       
        reportFile.printf("DPS: %d        %d        %.1f%n", maxDPSLength, maxDPSWait, avgDPSWait);
        reportFile.printf("Tanks: %d        %d        %.1f%n", maxTankLength, maxTankWait, avgTankWait);
        reportFile.printf("Healers: %d        %d        %.1f%n", maxHealLength, maxHealWait, avgHealWait);
        reportFile.printf("%nAll Players Average Wait Time: %.1f%n", avgAllPlayersWait);

        reportFile.printf("%nStatistics for Players Remaining in Queue%n");
        
        //If statements that decide whether to print NaN or the value of the variable, if it isn't zero. 
        if(avgDPSQueueWait == 0)
        {
        	reportFile.printf("%nDPS: %d        %d        NaN%n", finalDPSQueueLength, maxDPSQueueWait, avgDPSQueueWait);
        }
        else
        {
        	reportFile.printf("%nDPS: %d        %d        %.1f%n", finalDPSQueueLength, maxDPSQueueWait, avgDPSQueueWait);
        }
        
        if(avgTankQueueWait == 0)
        {
        	reportFile.printf("Tanks: %d        %d        NaN%n", finalTankQueueLength, maxTankQueueWait, avgTankQueueWait);
        }
        else
        {
        	reportFile.printf("Tanks: %d        %d        %.1f%n", finalTankQueueLength, maxTankQueueWait, avgTankQueueWait);
        }
        
        if(avgHealerQueueWait == 0)
        {
        	reportFile.printf("Healers: %d        %d        NaN%n", finalHealerQueueLength, maxHealerQueueWait, avgHealerQueueWait);
        }
        else
        {
        	reportFile.printf("Healers: %d        %d        %.1f%n", finalHealerQueueLength, maxHealerQueueWait, avgHealerQueueWait);
        }
        
    	
    	
    }

    /**
     * Cleans up after the simulation
     */
    public void cleanup()
    {
    	if (reportFile != null) {
            reportFile.close();
        }
    }

    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Please enter the name of the configuration file: ");
        String configFileName = keyboard.nextLine();

        Simulation theSim = new Simulation();
        theSim.setup(configFileName);
        theSim.runSimulation();
        theSim.cleanup();
    }

}

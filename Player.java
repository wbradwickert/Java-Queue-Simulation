package edu.ilstu;
/**
 * Data about a player
 * 
 * @author Mary Elaine Califf
 */
public class Player
{
    private int playerNum;
    private String playerRole;
    private int arrivalTime;

    /**
     * Constructor
     * 
     * @param playerNum   The number to identify this player
     * @param role        The player's preferred role(s)
     * @param arrivalTime The arrival time in the dungeon queue
     */
    public Player(int playerNum, String role, int arrivalTime)
    {
        this.playerNum = playerNum;
        this.playerRole = role;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Getter for player number
     * 
     * @return the player number
     */
    public int getPlayerNum()
    {
        return playerNum;
    }

    /**
     * Getter for player preferred roles
     * 
     * @return the player's preferred roles
     */
    public String getPlayerRole()
    {
        return playerRole;
    }

    /**
     * Getter for arrival time
     * 
     * @return the player's arrival time
     */
    public int getArrivalTime()
    {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (this.getClass() == obj.getClass())
        {
            Player rhs = (Player) obj;
            return this.playerNum == rhs.playerNum && this.arrivalTime == rhs.arrivalTime
                    && this.playerRole.equals(rhs.playerRole);
        }
        else
            return false;
    }
}

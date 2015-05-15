//Copyright 1999, Joseph Bergin. All rights reserved.
//Edited in 2015 by Jeff Goymerac, Chris Rawlings, and Christine Wang

/**
 *Files necessary to be included for the robot interface to work
 */
import kareltherobot.*;
import java.awt.Color;

//import AugmentedRobot.*;


/** KarelMain class that generates the world and places the robots onto the grid
 * @author Goymerac, Rawlings, Wang 
 */
class KarelMain implements Directions
{   
    /**
     * First robot that represents person1 who is attempting to cross the bridge(enter the cs)
     */
    public static AugmentedRobot karel1;
    /**
     * Second robot that represents person2 who is attempting to cross the bridge(enter the cs)
     */
    public static AugmentedRobot karel2;
    /**
     * Third robot that represents person3 who is attempting to cross the bridge(enter the cs)
     */
    public static AugmentedRobot karel3;
    /**
     * Fourth robot that represents person4 who is attempting to cross the bridge(enter the cs)
     */
    public static AugmentedRobot karel4;
    
    /** Main function for the simulation. The robots are created, their main thread started, and an infinite loop runs to allow the simulation to function
     * @param multiUse whether or not multiple robots are allowed to travel on the bridge at the same time if traveling the same direction
     * @param speed1 speed variable for robot1
     * @param speed2 speed variable for robot2
     * @param speed3 speed variable for robot3
     * @param speed4 speed variable for robot4
     * @return nothing
     */
    public static void main(String[] args)
    {
        char multiUse = args[0].charAt(0);
        int speed1 = Integer.parseInt(args[1]);
        int speed2 = Integer.parseInt(args[2]);
        int speed3 = Integer.parseInt(args[3]);
        int speed4 = Integer.parseInt(args[4]);

        karel1 = new AugmentedRobot(7, 6, East, 0, Color.blue, speed1, 'l', 1, multiUse);
        try
        { Thread.sleep(1000);
        
        }
        catch(InterruptedException ex)
        {
        	Thread.currentThread().interrupt();
        }
        karel2 = new AugmentedRobot(13, 6, East, 0, Color.red, speed2, 'l', 2, multiUse);
        try
        { Thread.sleep(1500);
        
        }
        catch(InterruptedException ex)
        {
        	Thread.currentThread().interrupt();
        }
        karel3 = new AugmentedRobot(10, 16, West, 0, Color.green, speed3, 'r', 3, multiUse);
        try
        { Thread.sleep(1200);
        
        }
        catch(InterruptedException ex)
        {
        	Thread.currentThread().interrupt();
        }
        karel4 = new AugmentedRobot(17, 16, West, 0, Color.white, speed4, 'r', 4, multiUse);
        
        new Thread(karel1).start();
        new Thread(karel2).start();
        new Thread(karel3).start();
        new Thread(karel4).start();
        
        /**
         * Infite loop that runs so the simulation will not stop
         */
        while(true)
        {
        }
       
    }
 
    
    /**
     * static variables needed for the world to be rendered properly 
     */
    static
    {
        /**
         * variable that holds the world generation file name
         */
        World.readWorld("twotown2.kwld");
        /**
         * sets the world to be visible
         */
        World.setVisible(true);
        /**
         * shows the speed controller on the gui
         */
        World.showSpeedControl(true);
    }
}

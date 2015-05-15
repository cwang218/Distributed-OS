//Copyright 1999, Joseph Bergin. All rights reserved.
//Edited in 2015 by Jeff Goymerac, Chris Rawlings, and Christine Wang


import kareltherobot.*;
import java.awt.Color;
import java.util.Comparator;



/** The AugmentedRobot. Includes some additional movement options as well as functions specific to the 5800 project
 * @author Goymerac, Rawlings, Wang
 */
public class AugmentedRobot extends Robot
{   
    /**
     * variable that counts the number of locks on the critical section
     * when only one robot crosses at a time, this should be 0 or 1
     * when multiple robots are allowed to cross at the same time, this should be 0, 1, 2, 3, or 4
     */
    static int mutexCount = 0;
    /*
     * variable to detect if CS is empty    
     */
    public static boolean inCS =false;
    /**
     * which direction the robot crossing the bridge is traveling
     * used when multiple robots are allowed to cross at the same time
     */
	  
	public static char travelDirection;
    /**
     * whether or not multiple robots are allowed to cross the bridge simulatiously
     */
    public static char allowMultipleCrossing = 'f';
    /**
     * unique ID that is assigned to each robot
     */
    int robotNumber;
    /**
     * speed variable for the robot
     * the higher the value, the slower the robot
     */
    int robotSpeed;
    /**
     * variable for if the robot is on the left side or right side of the bridge
     */
    char location;
    /**
     * variable for if the robot wishes to cross the bridge and enter the cs
     */
    boolean wantToEnterCS = false;
    /**
     * variable for if the robot is currently crossing the bridge
     */
    boolean isInCS = false;
    /**
     * local time that is neccesary when processesing CS reqs from other robots
     */
    long localTime = Long.MAX_VALUE;
    /**
     * variable for if the thread is still running
     */
    boolean isRunning = true;
    
    
    /** Basic Constructor for the AugmentedRobot Class
	 * @param street the street on which to place the robot
	 * @param avenue the avenue on which to place the robot
	 * @param direction the direction on which the robot initially faces
	 * @param beepers the number of beepers initially held by the robot
	 */
    public AugmentedRobot(int street, int avenue, Direction direction, int beepers)
    {   super(street, avenue, direction, beepers);
        World.setupThread(this);
    }
    
    /** Advanced Constructor for the AugmentedRobot Class
	 * @param street the street on which to place the robot
	 * @param avenue the avenue on which to place the robot
	 * @param direction the direction on which the robot initially faces
	 * @param beepers the number of beepers initially held by the robot
	 * @param badge the color of the badge on the robot
	 * @param speed the speed of the robot. a larger value means the robot moves more slowly
	 * @param loc which side of the bridge the robot initially starts on
	 * @param num the unique ID of the robot
	 * @param multiUse whether or not multiple robots are allowed to travel on the bridge at the same time if traveling the same direction
	 */
    public AugmentedRobot(int street, int avenue, Direction direction, int beepers, Color badge, int speed, char loc, int num, char multiUse)
    {   super(street, avenue, direction, beepers, badge);
        location = loc;
        robotSpeed=speed;
        robotNumber = num;
        allowMultipleCrossing = multiUse;
        World.setupThread(this);
    }
    
    /** 
     * turns the robot around, 180 degree turn
     * @return nothing
     */
    public void  turnAround ()
    {   turnLeft();
        turnLeft();
    }
    
    /**
     * turns the robot right
     * @return nothing
     */
    public void  turnRight ()
    {   turnLeft();
        turnLeft();
        turnLeft();
    }
    
    /**
     * moves the robot back one space
     * does not check if the robot can successfully move back one space
     * @return nothing
     */
    public void  backUp ()
    {   turnAround();
        super.move();
        turnAround();
    }
    
    /**
     * checks to see if the space left of the robot is clear
     * @return true is left is clear, false otherwise
     */
    public boolean  leftIsClear()
    {   turnLeft();
        if( frontIsClear() )
        {   
            turnRight();
            return true;
        }
        else
        {   
            turnRight();
            return false;
        }
    }
    
    /**
     * checks to see if the space right of the robot is clear
     * @return true is right is clear, false otherwise
     */
    public boolean  rightIsClear()
    {   turnRight();
        if( frontIsClear() )
        {   
            turnLeft();
            return true;
        }
        else
        {   
            turnLeft();
            return false;
        }
    }
    
    /**
     * makes the robot face north(up)
     * @return nothing
     */
    public void  faceNorth()
    {   while( ! facingNorth() )
        {   
            turnLeft(); 
        }
    }
    
    /**
     * makes the robot face east(left)
     * @return nothing
     */
    public void  faceEast()
    {   while( ! facingEast() )
        {   
            turnLeft(); 
        }
    }
    
    /**
     * makes the robot face south(down)
     * @return nothing
     */
    public void  faceSouth()
    {   while( ! facingSouth() )
        {   
            turnLeft(); 
        }
    }
    
    /**
     * makes the robot face west(left)
     * @return nothing
     */
    public void  faceWest()
    {   while( ! facingWest() )
        {   turnLeft(); 
        }
    }
    
    /**
     * makes the robot move south(down) one move
     * @return nothing
     */
    public void moveSouth()
    {
        faceSouth();
        super.move();
    }
    
    /**
     * makes the robot move east(right) one move
     * @return nothing
     */
    public void moveEast()
    {
        faceEast();
        super.move();
    }
    
    /**
     * makes the robot move west(left) one move
     * @return nothing
     */
    public void moveWest()
    {
        faceWest();
        super.move();
    }
    
    /**
     * makes the robot move north(up) one move
     * @return nothing
     */
    public void moveNorth()
    {
        faceNorth();
        super.move();
    }
    
    /**
     * makes the robot face west,sets the travelDirection, gains a lock on the mutex, 
	 * moves 10 spaces as dictated by the robots speed then releases the lock on the mutex.
	 * Setting the robots location to the left side of the bridge once it crosses
	 * If the mutex is clear it then changes the travelDirection    
	 * @return nothing
     */
    public boolean crossLeft()
    {
        faceWest();
        travelDirection ='l';
		mutexCount++;        
		augMove(10);
        mutexCount--;
        location = 'l';
		if(mutexCount==0)
		{
			travelDirection = 'r';
		}
        return true;
    }
    
  /**
     * makes the robot face east,sets the travelDirection, gains a lock on the mutex, 
	 * moves 10 spaces as dictated by the robots speed then releases the lock on the mutex.
	 * Setting the robots location to the right side of the bridge once it crosses
	 * If the mutex is clear it then changes the travelDirection    
	 * @return nothing
     */
    public boolean crossRight()
    {
        faceEast();
        travelDirection = 'r';
		mutexCount++;        
		augMove(10);
        mutexCount--;
        location = 'r';
		if(mutexCount==0)
		{
			travelDirection = 'l';
		}
        return true;
    }
    
    /**
     * augmented move function that takes into account the robots speed and that moves numSteps
     * @param numSteps the number of steps to move
     * @return nothing
     */
    public void augMove(int numSteps)
    {
        int numStepsTaken = 0;
        
        while(numStepsTaken < numSteps)
        {
            try 
            {
                Thread.sleep(robotSpeed * 100);
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }
            super.move();
            numStepsTaken++;
        }
    }
    
    /**
     * Function that causes the robot to cross the critical section 
     * @return nothing
     */
    public void attemptToCross()
    {
        if(allowMultipleCrossing == 't')
        {
            if (travelDirection != location)
            {
                isInCS = true;
                wantToEnterCS = false;
            	
                cross();
                isInCS = false; 
                
                
              
            }
            else
            {
            }
        }
        else if(enterCS() == true && inCS==false )
        {
            isInCS = true;
            wantToEnterCS = false;
			inCS=true;
			cross();
			inCS=false;
			isInCS = false;
			
			
            
        }
        else
        { 
			
        }
    }
    
    /**
     * Function that checks which side of the bridge the robot is on and then calls the appropriate crossing function
     * @return nothing
     */
    public void cross()
    {
        if(location == 'l')
        {
            crossRight();
        }
        else if(location == 'r')
        {
            crossLeft();
        }
        else
        {
        }
    }
    
    /**
     * threaded function for the AugmentedRobot class
     * Continous loop thats sets the robot as wanting to enter the CS and then attempting to cross the bridge 
     */
    public void run()
    {
        while(isRunning)
        {
            wantToEnterCS = true;
            attemptToCross();
        }
        
    }
    
    /**
     * function to destroy threads that are finished running
     */
    public void kill()
    {
    	isRunning = false;
    }
    
    /**
     * Function that is called by a robot to enter the CS
     * calls the reqcs function before incrementing the mutexCount
     * @returns true when the function has finished
     */
    public boolean enterCS()
    {	
        	wantToEnterCS = true;
        	localTime = System.currentTimeMillis();
        	reqcs(robotNumber, localTime);
        	return true;
		
		
    }
    
    /**
     * This function creates 4 new threads and depending on the reqNum parameter starts the three other threads
     * Each thread calls the csReqAck function and are joined together meaning that the function will not 
     * past that point until all threads have stopped
     * @param reqNum the unique ID of the robot calling the reqcs function
     * @param reqNumTime the local time of the robot calling the reqcs function
     * @return nothing
     */
    public void reqcs(int reqNum, final long reqNumTime)
    {
        //4 threads being created
        Thread karel1Acq = new Thread()
        {
            public void run()
            {
                KarelMain.karel1.csReqAck(reqNumTime);
            }
        };
        
        Thread karel2Acq = new Thread()
        {
            public void run()
            {
                KarelMain.karel2.csReqAck(reqNumTime);
            }
        };
        
        Thread karel3Acq = new Thread()
        {
            public void run()
            {
                KarelMain.karel3.csReqAck(reqNumTime);
            }
        };
        
        Thread karel4Acq = new Thread()
        {
            public void run()
            {
                KarelMain.karel4.csReqAck(reqNumTime);
            }
        };
        
        //switch case to decided which threads to start and join together
        switch (reqNum)
        {
            case 1:
            karel2Acq.start();
            karel3Acq.start();
            karel4Acq.start();
            
            try
            {
                karel2Acq.join();
                karel3Acq.join();
                karel4Acq.join();
            }
            catch( InterruptedException e)
            {
                System.out.println("reqcs1 failed");
            }
            break;
            
            case 2:
            karel1Acq.start();
            karel3Acq.start();
            karel4Acq.start();
            
            try
            {
                karel1Acq.join();
                karel3Acq.join();
                karel4Acq.join();
            }
            catch( InterruptedException e)
            {
                System.out.println("reqcs2 failed");
            }
            break;
            
            case 3:
            karel1Acq.start();
            karel2Acq.start();
            karel4Acq.start();
            
            try
            {
                karel1Acq.join();
                karel2Acq.join();
                karel4Acq.join();
            }
            catch( InterruptedException e)
            {
                System.out.println("reqcs3 failed");
            }
            break;
            
            case 4:
            karel1Acq.start();
            karel2Acq.start();
            karel3Acq.start();
            
            try
            {
                karel1Acq.join();
                karel2Acq.join();
                karel3Acq.join();
            }
            catch( InterruptedException e)
            {
                System.out.println("reqcs4 failed");
            }
            break;
            
            default:
            break;
        }
        
         
     
        
    }
    
    /**
     * Function that sends an ack to the robot wishing to enter the CS
     * @param reqNumTime time of robot which requested the mutex lock
     * @return will return true when the robot does not wish to enter the CS, when the local time is later than the reqNumTime, or after the robot exits the CS.
     * @return will only return false in the case something has gone wrong
     */
    public boolean csReqAck(long reqNumTime)
    {
        boolean acqValue = false;
        if(wantToEnterCS == false)
        {
            acqValue = true;
        }
        else if(localTime > reqNumTime)
        {
            acqValue = true;
        }
        else if(isInCS == true)
        {
            while(isInCS == true)
            {
            }
            acqValue = true;
        }
        else
        {
        }
        return acqValue;
    }
    
    
    

}

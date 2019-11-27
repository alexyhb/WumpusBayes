package wumpusworld;

import com.sun.deploy.util.ArrayUtil;
import sun.jvm.hotspot.memory.Generation;

import java.awt.*;
import java.lang.reflect.Array;
import java.time.Year;
import java.util.ArrayList;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    int rnd;
    private  Bayes bayes;

    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;   
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {

        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        
        if (w.gameOver()){
            return;
        }
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }
        


        //decide next move
        bayes=new Bayes(w);

        ArrayList<Point> KL=bayes.getKnowlist();
        ArrayList<Point> FL=bayes.getFrontier();
        bayes.getAllSituation();
        bayes.getProOfFrontier();
        bayes.getProWumpus();
        Point p= bayes.getGoalPoint();
        Point wp=bayes.getWumpusPoint();
        Point currentPoint =new Point(cX,cY);
        System.out.println("PRO:"+wp!=null&&w.wumpusAlive());
        ArrayList<Point> moveList=new  ArrayList<Point>();
        if (wp!=null&&w.wumpusAlive()&&w.hasArrow()){
            currentPoint=new Point(w.getPlayerX(),w.getPlayerY());
            int x=(int) wp.getX();
            int y=(int) wp.getY();
            Point shootPoint;


            if(w.isVisited(x-1,y)){

                shootPoint=new Point(x-1,y);
                moveList.add(shootPoint);
                ArrayList<Point> shootMove=moveList(currentPoint,shootPoint,moveList);
                Move(shootMove);
                currentPoint=new Point(w.getPlayerX(),w.getPlayerY());
                if(currentPoint.equals(shootPoint)){

                    shoot(1);
                }

            }else{
                shootPoint=new Point(x,y-1);
                moveList.add(shootPoint);
                ArrayList<Point> shootMove=moveList(currentPoint,shootPoint,moveList);
                Move(shootMove);
                currentPoint=new Point(w.getPlayerX(),w.getPlayerY());
                if(currentPoint.equals(shootPoint)){

                    shoot(0);
                }
            }

        }
        System.out.println("wumpus is in :"+wp+",P:"+p);
        Point c=new Point(cX,cY);
        //if in the first point has stench random shoot
        if(KL.size()==1){
            if(w.hasStench(1,1)){
                //radom shoot
                int index=((int)(10 * Math.random())) % 2;
                shoot( index);
                turnToDir(index);

            }
        }

        if(p==null){
            ArrayList<Point> moveList2=new ArrayList<Point>();
            FL=bayes.getFrontier();
            for(int i=0;i<FL.size();i++){
                if(!w.wumpusAlive()){
                    p=FL.get(i);
                    moveList2.add(p);
                    moveList2=moveList(c,p,moveList2);
                    System.out.println("null, change to P:"+p);
                    Move(moveList2);
                }else if(wp!=null&&!wp.equals(FL.get(i))){
                    p=FL.get(i);
                    moveList2.add(p);
                    moveList2=moveList(c,p,moveList2);
                    System.out.println("null, change to P:"+p);
                    Move(moveList2);
                }
            }


        }else {
            ArrayList<Point> moveList2=new ArrayList<Point>();
            moveList.add(p);
            moveList=moveList(c,p,moveList2);
            Move(moveList);
            if (w.hasGlitter((int) currentPoint.getX(), (int) currentPoint.getY())) {
                w.doAction(World.A_GRAB);
                return;
            }
        }


    }    

     /**
     * Genertes a random instruction for the Agent.
     */
     //do single move to nearest point if c point next to g point then move one step, will call turnDIr
     public void doMove(Point start,Point goal){

         if(start.getX()==goal.getX()){
             if (goal.getY()==start.getY()+1){
                 turnToDir(0);
                 return;
             }else if(goal.getY()==start.getY()-1){
                 turnToDir(2);
                 return;
             }
         }else if(start.getY()==goal.getY()){
             if (goal.getX()==start.getX()-1){
                 turnToDir(3);
                 return;
             }else if(goal.getX()==start.getX()+1){
                 turnToDir(1);
                 return;
             }
         }else{
         }

     }

     //according to movelist to move
     public void Move(ArrayList <Point> movelist){

         for(int i=movelist.size()-1;i>=0;i--){
             Point c=new Point(w.getPlayerX(),w.getPlayerY());
             Point g=movelist.get(i);
             doMove(c,g);
         }
     }


//turn dir and move one step
     public void turnToDir(int dir){
        while(w.getDirection()!=dir){
            w.doAction(World.A_TURN_RIGHT);
        }
        if(w.getDirection()==dir){
            w.doAction(World.A_MOVE);
            return;
        }
     }
     public  ArrayList<Point> getNearestGoal(Point goal){
        int x=(int)goal.getX();
        int y=(int)goal.getY();
        ArrayList<Point> GoalFrontier=new ArrayList<Point>();
        if(w.isVisited(x,y+1)) {
            Point p1 = new Point(x, y + 1);
            GoalFrontier.add(p1);
        }
        if(w.isVisited(x,y-1)) {
            Point p2 = new Point(x, y - 1);
            GoalFrontier.add(p2);
        }
        if(w.isVisited(x-1,y)) {
            Point p3 = new Point(x-1, y );
            GoalFrontier.add(p3);
        }
        if(w.isVisited(x+1,y)) {
            Point p4 = new Point(x+1, y );
            GoalFrontier.add(p4);
        }
        return GoalFrontier;
    }
    // param is dir of wp,first turn and shoot
     public void shoot(int wp){
         if(wp==1){
             while(w.getDirection()!=1){
                 w.doAction(World.A_TURN_RIGHT);
             }
             if(w.getDirection()==1){
                 w.doAction(World.A_SHOOT);
             }
         }else {
             while(w.getDirection()!=0){
                 w.doAction(World.A_TURN_RIGHT);
             }
             if(w.getDirection()==0){
                 w.doAction(World.A_SHOOT);
             }
         }
     }
     //get a move list ,agent will rely on this list to move
     public ArrayList<Point> moveList(Point c,Point g,ArrayList<Point> moveList){

         moveList.add(g);
         ArrayList<Point> Ng=getNearestGoal(g);
         int size=Ng.size();
         for(int i=0;i<size;i++){
             Point np=Ng.get(i);
             if(np.equals(c)){
                 moveList.add(np);
                 return moveList;
             }
         }
         int index=(int)(Math.random()*Ng.size());
         try{
             Point newG=Ng.get(index);
             moveList.add(newG);
             return moveList(c,newG,moveList);
         }catch (IndexOutOfBoundsException e){
             throw e;
         }




    }



















    //获取最大值的下标
    public static double getMaxIndex(double[] arr){
        int maxIndex = 0;	//获取到的最大值的角标
        for(int i=0; i<arr.length; i++){
            if(arr[i] > arr[maxIndex]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    //获取最大值
    public static double getMaxNum(double[] arr){
        double maxNum = arr[0];
        for(int i=0; i<arr.length; i++){
            if(arr[i] > maxNum){
                maxNum = arr[i];
            }
        }
        return maxNum;
    }

    //获取最大值的下标
    public static int getMinIndex(double[] arr){
        int minIndex = 0;
        for(int i=0; i<arr.length; i++){
            if(arr[i] < arr[minIndex]){
                minIndex = i;
            }
        }
        return minIndex;
    }

    //获取最小值
    public static double getMinNum(double[] arr) {
        double minNum = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < minNum) {
                minNum = arr[i];
            }
        }
        return minNum;
    }
}

    
    
    



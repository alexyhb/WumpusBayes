package wumpusworld;

import com.sun.deploy.util.ArrayUtil;
import sun.tools.jconsole.inspector.XObject;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;

public class Bayes {
	private World w;
	private int cx;
	private int cy;
	private int dir;
	private Point wumpusPoint;
	private double proWumpus;
	private ArrayList<Point> knowlist;
	private ArrayList<Point> frontier;
	private ArrayList<String[]> JointDofPit;
	private double[] proPit;
	private double[] proWupus;
	private Point point[];



	public Bayes(World w){
		proWumpus=0;
		knowlist=new ArrayList(15);
		frontier=new ArrayList(15);
		this.w=w;
	}
	//get nearest point
	public ArrayList<Point> getFrontierPoint(int x,int y){
		ArrayList<Point> frontier=new ArrayList<Point>();
		Point p1=new Point(x,y+1);
		Point p2=new Point(x,y-1);
		Point p3=new Point(x+1,y);
		Point p4=new Point(x-1,y);
		frontier.add(p1);
		frontier.add(p2);
		frontier.add(p3);
		frontier.add(p4);
		return frontier;
	}
	//get the know list
	public ArrayList<Point> getKnowlist() {
		World cw=this.w;
		ArrayList<Point> listTemp=new ArrayList<Point>();
		int size =cw.getSize();
		for(int i=1;i<=size;i++) {
			for (int j = 1; j <=size; j++) {
				if (cw.isValidPosition(i, j) == true) {
					if (cw.isVisited(i, j) == true) {
						if(!listTemp.contains(new Point(i,j)))
							listTemp.add(new Point(i,j));
					}
				}
			}
		}
		Set<Point> middleLinkedHashSet = new LinkedHashSet<Point>(listTemp);
		ArrayList<Point> knowlist = new ArrayList<Point>(middleLinkedHashSet);
		this.knowlist=knowlist;
		return knowlist;
	}
	//get frontier list
	public ArrayList<Point> getFrontier() {
		World cw=w.cloneWorld();
		ArrayList<Point> temp=new ArrayList<Point>();
		for(int i=0;i<knowlist.size();i++){
			Point pt=knowlist.get(i);
			int cx=(int)pt.getX();
			int cy=(int)pt.getY();
			ArrayList<Point> list=getFrontierPoint(cx,cy);
			for(int j=0;j<list.size();j++){
				int x=(int)list.get(j).getX();
				int y=(int)list.get(j).getY();
				if(cw.isValidPosition(x,y)==true){
					if(cw.isUnknown(x,y)==true){
						temp.add(new Point(x,y));
					}
				}
			}
		}Set<Point> middleLinkedHashSet = new LinkedHashSet<Point>(temp);
		ArrayList<Point> frontier = new ArrayList<Point>(middleLinkedHashSet);
		this.frontier=frontier;
		return frontier;
	}
	//get the all probability
	public ArrayList<String[]>  getAllSituation(){
		int size=frontier.size();

		int power=(int)Math.pow(2,frontier.size());

		ArrayList<String[]>  bit=new ArrayList<>(power);
		String temp[];
		for(int i=0;i<power;i++){
			String tempStr1="";
			String tempStr=Integer.toBinaryString(i);
			int length1=size-tempStr.length();
			if(tempStr.length()<size){
				for(int j=0;j<length1;j++){
					tempStr ="0"+tempStr;
				}
			}
			tempStr1+=tempStr;
			temp=tempStr1.split("");

			bit.add(temp);
		}

		return bit;
	}
	//compaire with clone world with true game state,
	public ArrayList<String[]> getJDofPit(){
		ArrayList<String[]> All =getAllSituation();
		ArrayList<String[]> JointD=new ArrayList<String[]>();
//		JointD.addAll(All);
		ArrayList<Point> list=getKnowlist();
		for(int i=0;i<All.size();i++){
			int nrPit=0;
			String[] temp;
			World tempW=new World(w.getSize());

			for(int move=0;move<list.size();move++){

				int movex=(int)list.get(move).getX();
				int movey=(int)list.get(move).getY();
				if(w.hasPit(movex,movey)){
					tempW.addPit(movex,movey);
				}
				tempW.setVisited(movex,movey);
			}


			temp=All.get(i);
			for(int j=0;j<temp.length;j++){
				Point p=frontier.get(j);
				String biStr=temp[j];
				if(Integer.parseInt(biStr)==1) {
					tempW.addPit((int) p.getX(), (int) p.getY());
					nrPit++;
					if(3-nrPit<0){
						All.set(i,null);
					}
				}

			}
			for(int l=0;l<list.size();l++){
				int x=(int)list.get(l).getX();
				int y=(int)list.get(l).getY();
				boolean flag1=w.hasBreeze(x,y);
				boolean flag2=tempW.hasBreeze(x,y);
				if(flag1!=flag2){
					All.set(i,null);
				}

			}

		}

		for(int i=0;i<All.size();i++){
		}
		JointDofPit=All;
		return All ;
	}

	//get probability of if there pit
	public double[] getProOfFrontier() {
		ArrayList<String[]> jointD=getJDofPit();

		Point p = null;
		int size=jointD.size();
		double pro[] = new double[frontier.size()];
		for(int i=0;i<frontier.size();i++){
			double tempT=0;
			double tempF=0;
			double proIsPit=0;
			Point temP=frontier.get(i);
			for(int j=0;j<size;j++){
				int count_0=0;
				int count_1=0;
				if(!(jointD.get(j)==null)){
					String[] single=jointD.get(j);
					int valueOfSingle=Integer.parseInt(single[i]);
					if(valueOfSingle==0){
						double temPit=0;
						for(int k=0;k<jointD.get(j).length;k++){
								int c=Integer.parseInt(single[k]);
								if(c==1){
									count_0++;
								}
						}
						temPit=((double)Math.pow(0.8,size-1-count_0))*((double)(Math.pow(0.2,count_0)));
						tempF+=temPit;
					}else{
						double temPit=0;
						for(int k=0;k<jointD.get(j).length;k++){
								int c=Integer.parseInt(single[k]);
								if(c==1){
									count_1++;
								}
						}
						count_1--;
						temPit = ((double)Math.pow(0.8,frontier.size()-1-count_1))*((double)Math.pow(0.2,count_1));
						tempT += temPit;
					}
				}
			}
			proIsPit=(0.2*tempT)/(0.8*tempF+0.2*tempT);
			pro[i]=proIsPit;
			System.out.println(frontier.get(i)+"pro:"+proIsPit);

		}
		double minTemp=1;

		for(int g=0;g<pro.length;g++){

			if(pro[g]<minTemp){
				minTemp=pro[g];
				p=frontier.get(g);
			}
		}
		proPit=pro;
		return proPit;

	}
	//get JD of wumpus
	public ArrayList<String[]> getJDofWumpus(){
		ArrayList<String[]> All =getAllSituation();
//		JointD.addAll(All);
		ArrayList<Point> list=getKnowlist();

		for(int i=0;i<All.size();i++){
			int nrWumpus=0;
			String[] temp;
			World tempW=new World(w.getSize());
			for(int move=0;move<list.size();move++){

				int movex=(int)list.get(move).getX();
				int movey=(int)list.get(move).getY();

				tempW.setVisited(movex,movey);
			}
			temp=All.get(i);
			for(int j=0;j<temp.length;j++){
				Point p=frontier.get(j);
				String biStr=temp[j];
				if(Integer.parseInt(biStr)==1) {
					int wx=(int)p.getX();
					int wy=(int)p.getY();
					tempW.addWumpus(wx,wy);

					nrWumpus++;
					if (nrWumpus>1){
						All.set(i,null);
					}

				}

			}

			for(int l=0;l<list.size();l++){
				int x=(int)list.get(l).getX();
				int y=(int)list.get(l).getY();
				boolean flag1=w.hasStench(x,y);
				boolean flag2=tempW.hasStench(x,y);
				if(flag1!=flag2){
					All.set(i,null);
				}

			}
		}


		return All ;
	}

	public HashMap getProWumpus(){
		ArrayList<String[]> jointD=getJDofWumpus();
		HashMap pro=new HashMap();
		Point p = null;
		int size=jointD.size();
//		double pro[] = new double[frontier.size()];
		for(int i=0;i<frontier.size();i++){
			double tempT=0;
			double tempF=0;
			double proIsWumpus=0;
			Point temP=frontier.get(i);
			for(int j=0;j<size;j++){
				int count_0=0;
				int count_1=0;
				if(!(jointD.get(j)==null)){
					String[] single=jointD.get(j);
					int valueOfSingle=Integer.parseInt(single[i]);
					if(valueOfSingle==0){
						double temWupus=0;
						for(int k=0;k<jointD.get(j).length;k++){
							int c=Integer.parseInt(single[k]);
							if(c==1){
								count_0++;
							}
						}
						temWupus=((double)Math.pow(0.93,size-1-count_0))*((double)(Math.pow(0.07,count_0)));
						tempF+=temWupus;
					}else{
						double temWupus=0;
						for(int k=0;k<jointD.get(j).length;k++){
							int c=Integer.parseInt(single[k]);
							if(c==1){
								count_1++;
							}
						}
						count_1--;
						temWupus = ((double)Math.pow(0.93,frontier.size()-1-count_1))*((double)Math.pow(0.07,count_1));
						tempT += temWupus;
					}
				}
			}
			proIsWumpus=(0.07*tempT)/(0.93*tempF+0.07*tempT);

			if(proIsWumpus==1){
				wumpusPoint=frontier.get(i);
				System.out.println("FIND WUMPUS!!!");
			}
			pro.put(frontier.get(i),proIsWumpus);

		}
		double minTemp=0;

		return  pro;

	}

	public Point getGoalPoint(){
		ArrayList<Point> gp=new ArrayList<Point>();

		ArrayList<Point> gp2=new ArrayList<Point>();
		Map proWumpus=getProWumpus();
		Point gpoint=null;
		Point wp=getWumpusPoint();
		double pitTemp=1;
		double pitTemp2=1;
		for(int g=0;g<proPit.length;g++){
			if(w.wumpusAlive()){
				if(proPit[g]<pitTemp&&!frontier.get(g).equals(wp)){
					pitTemp=proPit[g];
					if(gp.size()==0){
						gp.add(frontier.get(g));
					}else {
						gp.set(0,frontier.get(g));
					}
				}
			}else {
				if(proPit[g]<pitTemp){
					pitTemp=proPit[g];
					if(gp.size()==0){
						gp.add(frontier.get(g));
					}else {
						gp.set(0,frontier.get(g));
					}
				}
			}
//			ArrayList<Point> nearList=getNearestGoal(frontier.get(g));
//			for(int n=0;n<nearList.size();n++){
//				int rx=(int)getNearestGoal(frontier.get(g)).get(n).getX();
//				int ry=(int)getNearestGoal(frontier.get(g)).get(n).getY();
//				double value= (double) proWumpus.get(frontier.get(g));
//				if(w.wumpusAlive()){
//					if(proPit[g]<pitTemp&&value==0&&!w.hasPit(rx,ry)){
//						pitTemp=proPit[g];
//						gp.add(frontier.get(g));
//					}else  if (proPit[g]<pitTemp2&&value>0&&value<1&&!w.hasPit(rx,ry)){
//						pitTemp2=proPit[g];
//						gp2.add(frontier.get(g));
//					}
//				}else{
//
//					if(proPit[g]<pitTemp&&!w.hasPit(rx,ry)){
//						pitTemp=proPit[g];
//						gp.add(frontier.get(g));
//					}
//				}
			}



		double wumpusTemp=0;
		if(gp!=null){
			for(int i=0;i<gp.size();i++){
				gpoint=gp.get(i);
				break;
			}
		} else if (gp2 != null) {
			for(int i=0;i<gp2.size();i++){
				gpoint=gp2.get(i);
				break;
			}
		}

		System.out.println("Best move:"+gpoint);
		return gpoint;
	}
	public Point getWumpusPoint(){
		return wumpusPoint;
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
	public static int getMinIndex(double[] arr){
		int minIndex = 0;
		for(int i=0; i<arr.length; i++){
			if(arr[i] < arr[minIndex]){
				minIndex = i;
			}
		}
		return minIndex;
	}

}

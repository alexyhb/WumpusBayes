//package wumpusworld;
//
//public class Astar {
//    public double[] getPro(){
//
//        ArrayList<String[]> bList = validateMap();
//        double[] b_pro = new double[frontier.size()];
//
//        //Loop the squares and query each one
//        for (int square=0;square<frontier.size();square++){
//
//            double no_pit = 0;
//            double has_pit = 0;
//            double is_pit = 0;
//
//            int[] squares = frontier.get(square);
//            int x = squares[0];
//            int y = squares[1];
////                    System.out.println("Now calculate square ("+x+","+y+")");
//            //Query the NO.square square
//            //Traversing the bList
//            for (int i=0;i<bList.size();i++){
//
//                int count_pit0 = 0;
//                int count_pit1 = 0;
//
//                if (!(bList.get(i)==null)){
//                    String[] b = bList.get(i);
//                    int b_int = Integer.parseInt(b[square]);
//                    if (b_int==0){
//                        double tem_pit = 0;
////                        System.out.println("00000000");
//                        for (int j=0;j<frontier.size();j++){
//                            int c = Integer.parseInt(b[j]);
//                            if (c == 1 ){
//                                count_pit0++;
//                            }
//                        }
////                        System.out.println("Query square ("+x+","+y+"),bList "+i+
////                                ", it has no pit,others has "+count_pit0);
//                        //Calculate the probability
//                        tem_pit = ((double)Math.pow(0.8,frontier.size()-1-count_pit0))*((double)Math.pow(0.2,count_pit0));
//                        no_pit += tem_pit;
//                    }
//                    else {
//                        double tem_pit = 0;
////                        System.out.println("1111111");
//                        for (int j=0;j<frontier.size();j++){
//                            int c = Integer.parseInt(b[j]);
//                            if (c == 1 ){
//                                count_pit1++;
//                            }
//                        }
//                        count_pit1--;
////                        System.out.println("Query square ("+x+","+y+"),bList "+i+", it has " +
////                                "pit,When it has pit,others has "+count_pit1+" pit");
//                        tem_pit = ((double)Math.pow(0.8,frontier.size()-1-count_pit1))*((double)Math.pow(0.2,count_pit1));
//                        has_pit += tem_pit;
////                        System.out.println("tem_pit is " + (double)Math.pow(0.8,frontier.size()-1-count_pit1) + " * "
////                        +(double)Math.pow(0.2,count_pit1)+" = "+tem_pit);
//                    }
//                }
//            }
////            System.out.println("no_pit p is "+no_pit+", has_pit p is "+has_pit);
//            is_pit = (0.2*has_pit)/(0.8*no_pit + 0.2*has_pit);
////            System.out.println("The probability of this square is PIT is: "+is_pit);
//            b_pro[square] = is_pit;
//        }
////        for (int m=0;m<b_pro.length;m++){
////            System.out.println(b_pro[m]);
////        }
//        return b_pro;
//    }
//}
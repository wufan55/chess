package team.IRobot;

public class StupidRobot implements IRobot {
    private static final int BOARD_SIZE = 15;
    private static final int ROLE_OPPONENT = 1;
    private static final int ROLE_ROBOT = 2;
    private static final int ROLE_NON = 0;
    private static final int ORIENTATION_LR = 0;
    private static final int ORIENTATION_UD = 1;
    private static final int ORIENTATION_LT_RD = 2;
    private static final int ORIENTATION_RT_LD = 3;

    private int[][] boardRef = null;


    /**
     * There we provide a default implementation to simulate robot's behavior
     *
     * @return a {@code robot.Pair} which contains a valid (x,y) position
     */
    @Override
    public Pair getDeterminedPos() {
        int[][] situationRobot  = new int[boardRef.length][boardRef[0].length];
        int[][] situationOpponent  = new int[boardRef.length][boardRef[0].length];

        int maxRobotScore = 0;
        Pair maxRobotPoint = new Pair();

        int maxOpponentScore = 0;
        Pair maxOpponentPoint = new Pair();
        for(int i=0;i<BOARD_SIZE;i++){
            for(int k=0;k<BOARD_SIZE;k++){
                if(boardRef[i][k]!=ROLE_NON){
                    situationOpponent[i][k]=situationRobot[i][k]=0;
                }else{
                    boardRef[i][k] = ROLE_OPPONENT;
                    situationOpponent[i][k] = evaluateScore(ROLE_OPPONENT,i,k);
                    boardRef[i][k]=ROLE_NON;
                    if(situationOpponent[i][k]>maxOpponentScore){
                        maxOpponentScore = situationOpponent[i][k];
                        maxOpponentPoint.x = i;
                        maxOpponentPoint.y = k;
                    }

                    boardRef[i][k]=ROLE_ROBOT;
                    situationRobot[i][k]=evaluateScore(ROLE_ROBOT,i,k);
                    boardRef[i][k]=ROLE_NON;
                    if(situationRobot[i][k]>maxRobotScore){
                        maxRobotScore = situationRobot[i][k];
                        maxRobotPoint.x = i;
                        maxRobotPoint.y = k;
                    }

                }
            }
        }
        if(maxRobotScore > maxOpponentScore || maxRobotScore==Integer.MAX_VALUE){
            return maxRobotPoint;
        }else{
            return maxOpponentPoint;
        }
    }

    /**
     * This method is used to retrieve game board such that robot can determine its (x,y) position
     *
     * @param gameBoard the 2-dimension array to represent the game board
     */
    @Override
    public void retrieveGameBoard(int[][] gameBoard) {
        boardRef = gameBoard;
    }


    /**
     * The policy of evaluating was referred to https://www.cnblogs.com/maxuewei2/p/4825520.html
     * @param role the role of current player
     * @param x position x
     * @param y position y
     * @param orientation orientation of determining line
     * @return
     */
    private int patternRecognition(int role, int x,int y,int orientation){
        StringBuilder sb = new StringBuilder();
        if(orientation==ORIENTATION_LR){
            int leftBound = (x - 4)>=0?x-4:0;
            int rightBound = (x +4)<BOARD_SIZE?x+4:BOARD_SIZE-1;

            for(int i=leftBound;i<=rightBound;i++){
                sb.append(boardRef[i][y]);
            }
        }else if(orientation == ORIENTATION_UD){
            int bottomBound = (y+4)<BOARD_SIZE?y+4:BOARD_SIZE-1;
            int topBound = (y-4)>=0?y-4:0;

            for(int i=topBound;i<=bottomBound;i++){
                sb.append(boardRef[x][i]);
            }
        }else if(orientation== ORIENTATION_LT_RD){
            int leftBound = 0,rightBound = 0,bottomBound = 0,topBound = 0;

            for(int i=1;i<=4;i++){
                leftBound = x-i;
                topBound = y-i;
                if(leftBound<0||topBound<0){
                    leftBound++;
                    topBound++;
                    break;
                }
            }
            for(int k=1;k<=4;k++){
                rightBound = x+k;
                bottomBound = y+k;
                if(rightBound>BOARD_SIZE||bottomBound>BOARD_SIZE){
                    rightBound--;
                    bottomBound--;
                    break;
                }
            }
            for(int i=topBound,k=leftBound;i<=bottomBound && k<=rightBound;i++,k++){
                sb.append(boardRef[k][i]);
            }
        }else if(orientation== ORIENTATION_RT_LD){
            int leftBound = 0,rightBound = 0,bottomBound = 0,topBound = 0;

            for(int i=1;i<=4;i++){
                rightBound = x+i;
                topBound = y-i;
                if(rightBound>BOARD_SIZE||topBound<0){
                    rightBound--;
                    topBound++;
                    break;
                }
            }
            for(int k=1;k<=4;k++){
                leftBound = x-k;
                bottomBound = y+k;
                if(leftBound<0||bottomBound>BOARD_SIZE){
                    leftBound++;
                    bottomBound--;
                    break;
                }
            }

            for(int i=topBound,k=rightBound;i<=bottomBound && k>=leftBound;i++,k--){
                sb.append(boardRef[k][i]);
            }
        }
        String str = sb.toString();
        if(str.contains(role == ROLE_ROBOT ? "22222" : "11111")){
            return Integer.MAX_VALUE;
        }
        if(str.contains(role == ROLE_ROBOT ? "022220" : "011110")){
            return 300000;
        }
        if(str.contains(role == ROLE_ROBOT ? "22202" : "11101") ||
                str.contains(role == ROLE_ROBOT ? "20222" : "10111")){
            return 3000;
        }
        if(str.contains(role == ROLE_ROBOT ? "0022200" : "0011100")){
            return 3000;
        }
        if(str.contains(role == ROLE_ROBOT ? "22022" : "11011")){
            return 2600;
        }
        if(str.contains(role == ROLE_ROBOT ? "22220" : "11110")||
                str.contains(role == ROLE_ROBOT ? "02222" : "01111")){
            return 2500;
        }
        if(str.contains(role == ROLE_ROBOT ? "020220" : "010110")||
                str.contains(role == ROLE_ROBOT ? "022020" : "011010")){
            return 800;
        }
        if(str.contains(role == ROLE_ROBOT ? "00022000" : "00011000")){
            return 650;
        }
        if(str.contains(role == ROLE_ROBOT ? "20022" : "10011")||
                str.contains(role == ROLE_ROBOT ? "22002" : "11001")){
            return 600;
        }
        if(str.contains(role == ROLE_ROBOT ? "20202" : "10101")){
            return 550;
        }
        if(str.contains(role == ROLE_ROBOT ? "22200" : "11100")||
                str.contains(role == ROLE_ROBOT ? "00222" : "00111")){
            return 500;
        }
        if(str.contains(role == ROLE_ROBOT ? "0020200" : "0010100")){
            return 250;
        }
        if(str.contains(role == ROLE_ROBOT ? "020020" : "010010")){
            return 200;
        }
        if(str.contains(role == ROLE_ROBOT ? "22000" : "11000")||
                str.contains(role == ROLE_ROBOT ? "00022" : "00011")){
            return 150;
        }
        return 0;
    }

    private int evaluateScore(int role,int x, int y){
        int a = patternRecognition(role,x,y,ORIENTATION_RT_LD);
        int b = patternRecognition(role,x,y,ORIENTATION_LT_RD);
        int c = patternRecognition(role,x,y,ORIENTATION_UD);
        int d = patternRecognition(role,x,y,ORIENTATION_LR);
        return Math.max(Math.max(Math.max(a,b),c),d);
    }
}


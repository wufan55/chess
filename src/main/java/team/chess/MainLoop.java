package team.chess;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.IRobot.IRobot;
import team.IRobot.Pair;
import team.IRobot.StupidRobot;
import team.chess.Action.DecideMan;
import team.chess.Action.JudgeMan;
import team.chess.Action.RecordMan;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainLoop {
    //计算机先手
    public static void FirstHand() throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        NodePOJO rootNodePOJO = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", 0);
        //System.out.println("computerX: " + rootNodePOJO.getX());
        //System.out.println("computerY: " + rootNodePOJO.getY());
        String comVal = "black";
        String humVal = "white";

        //System.out.println("computerValue: " + comVal);
        sqlSession.commit();
        sqlSession.close();
        boolean result;

        NodePOJO beginNode = rootNodePOJO;
        //循环一次
        int i = 0;
        while (i < 1) {
            DecideMan decideMan = new DecideMan();
            RecordMan recordMan = new RecordMan();
            JudgeMan judgeMan = new JudgeMan();
            NodePOJO endNode = new NodePOJO();
            IRobot iRobot = new StupidRobot();
            while (true) {
                SqlSession sqlSession1 = sqlSessionFactory.openSession();

                ChessboardPOJO chessboardPOJO = sqlSession1.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", beginNode.getChessboardId());
                List<String> lines = chessboardPOJO.getLines();
                //
                int[][] chessboard = Transfer(chessboardPOJO.getLines());
                iRobot.retrieveGameBoard(chessboard);
                Pair pair = iRobot.getDeterminedPos();
                Integer huX = pair.x + 1;
                //System.out.println("Input Human X: " + huX);
                Integer huY = pair.y + 1;
                //System.out.println("Input Human Y: " + huY);


                StringBuilder stringBuilder = new StringBuilder(lines.get(huX-1));
                stringBuilder.setCharAt(huY-1, Character.forDigit(2, 10));
                lines.set(huX-1, stringBuilder.toString());
                chessboardPOJO.setLines(lines);

                List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);
                Integer chessboardId = chessboardPOJO.getId();

                endNode.setChessboardId(chessboardId);
                endNode.setX(huX);
                endNode.setY(huY);
                endNode.setValue(2);
                List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", endNode);
                if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", endNode);
                else endNode = nodePOJOList.get(0);

                sqlSession1.commit();
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);

                if (result == true) {
                    recordMan.UpdateRecord(2);
                    System.out.println(humVal + " " + "win");
                    break;
                }

                beginNode = endNode;
                endNode = decideMan.Decide(beginNode);
                if (endNode == null) {
                    recordMan.UpdateRecord(2);
                    System.out.println(humVal + " " + "win");
                    break;
                }
                //System.out.println("computerX: " + endNode.getX());
                //System.out.println("computerY: " + endNode.getY());
                //System.out.println("computerValue: " + comVal);

                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);

                if (result == true) {
                    recordMan.UpdateRecord(endNode.getValue());
                    System.out.println(comVal + " " + "win");
                    break;
                }
                beginNode = endNode;

                sqlSession1.commit();
                sqlSession1.close();
            }
            i++;
        }
        return;
    }

    //计算机后手
    public static void SecondHand() throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Scanner scanner = new Scanner(System.in);

        String comVal = "white";
        String humVal = "black";

        NodePOJO rootNodePOJO = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", 0);
        sqlSession.commit();
        sqlSession.close();
        boolean result;

        NodePOJO beginNode = rootNodePOJO;
        //循环一次
        int i = 0;
        while (i < 1) {
            DecideMan decideMan = new DecideMan();
            RecordMan recordMan = new RecordMan();
            JudgeMan judgeMan = new JudgeMan();
            while (true) {
                NodePOJO endNode = decideMan.Decide(beginNode);
                if (endNode == null){
                    recordMan.UpdateRecord(1);
                    System.out.println(humVal + " " + "win");
                    break;
                }
                System.out.println("computerX: " + endNode.getX());
                System.out.println("computerY: " + endNode.getY());
                System.out.println("computerValue: " + comVal);
                //记录begin to end
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);

                if (result == true) {
                    recordMan.UpdateRecord(endNode.getValue());
                    System.out.println(comVal + " " + "win");
                    break;
                }
                beginNode = endNode;

                System.out.println("Input Human X: ");
                Integer huX = scanner.nextInt();
                System.out.println("Input Human Y: ");
                Integer huY = scanner.nextInt();

                SqlSession sqlSession1 = sqlSessionFactory.openSession();
                ChessboardPOJO chessboardPOJO = sqlSession1.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", endNode.getChessboardId());
                List<String> lines = chessboardPOJO.getLines();
                StringBuilder stringBuilder = new StringBuilder(lines.get(huX-1));
                stringBuilder.setCharAt(huY-1, Character.forDigit(1, 10));
                lines.set(huX-1, stringBuilder.toString());
                chessboardPOJO.setLines(lines);

                List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);
                Integer chessboardId = chessboardPOJO.getId();

                endNode.setChessboardId(chessboardId);
                endNode.setX(huX);
                endNode.setY(huY);
                endNode.setValue(1);
                List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", endNode);
                if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", endNode);
                else endNode = nodePOJOList.get(0);

                //记录end to next
                sqlSession1.commit();
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);

                if (result == true) {
                    recordMan.UpdateRecord(endNode.getValue());
                    System.out.println(humVal + " " + "win");
                    break;
                }
                beginNode = endNode;

                sqlSession1.commit();
                sqlSession1.close();
            }
            i++;
        }
        scanner.close();
        return;
    }

    //自己下
    public static void OnesOwn() throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        NodePOJO rootNodePOJO = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", 0);

        String comVal1 = "black";
        String comVal2 = "white";

        NodePOJO beginNode = rootNodePOJO;
        NodePOJO endNode;
        boolean result;
        Integer i = 0;
        //循环一次
        while (i < 1){
            DecideMan decideMan = new DecideMan();
            JudgeMan judgeMan = new JudgeMan();
            RecordMan recordMan = new RecordMan();
            while (true){
                endNode = decideMan.Decide(beginNode);
                if (endNode == null){
                    recordMan.UpdateRecord(1);
                    System.out.println(comVal1 + " " + "win");
                    break;
                }
                //获取最近的begin进行decide,解决周围都有子堵死情况
                NodePOJO temp;
                while (endNode.getValue() == null) {
                    List<StepPOJO> stepPOJOList = recordMan.getStepPOJOS();
                    Integer stepListSize = stepPOJOList.size();
                    StepPOJO stepPOJO = stepPOJOList.get(stepListSize-2);
                    Map param = new HashMap();
                    param.put("stepId", stepPOJO.getId());
                    List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                    RelationPOJO relationPOJO = relationPOJOS.get(0);
                    Integer beginId = relationPOJO.getNodeBeginId();
                    temp = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", beginId);
                    endNode = decideMan.Decide(temp);
                }
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);
                if (result == true) {
                    recordMan.UpdateRecord(2);
                    System.out.println("white win");
                    break;
                }
                beginNode = endNode;
                endNode = decideMan.Decide(beginNode);
                if (endNode == null){
                    recordMan.UpdateRecord(2);
                    System.out.println(comVal2 + " " + "win");
                    break;
                }
                while (endNode.getValue() == null) {
                    List<StepPOJO> stepPOJOList = recordMan.getStepPOJOS();
                    Integer stepListSize = stepPOJOList.size();
                    StepPOJO stepPOJO = stepPOJOList.get(stepListSize-2);
                    Map param = new HashMap();
                    param.put("stepId", stepPOJO.getId());
                    List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                    RelationPOJO relationPOJO = relationPOJOS.get(0);
                    Integer beginId = relationPOJO.getNodeBeginId();
                    temp = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", beginId);
                    endNode = decideMan.Decide(temp);
                }
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);
                if (result == true) {
                    recordMan.UpdateRecord(1);
                    System.out.println("black win");
                    break;
                }
                beginNode = endNode;
            }
            i++;
        }
        sqlSession.close();
        return;
    }

    //List<String> 转 int[][]
    private static int[][] Transfer(List<String> lines) {
        int[][] chessboard = new int[16][16];
        for (int i=0; i < 15; i++) {
            for (int k=0; k < 15; k++) {
                chessboard[i][k] = Integer.parseInt(String.valueOf(lines.get(i).charAt(k)));
            }
        }
        return chessboard;
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 10; i++)
            FirstHand();
    }
}

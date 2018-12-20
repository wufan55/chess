package team.chess;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.Action.DecideMan;
import team.chess.Action.JudgeMan;
import team.chess.Action.RecordMan;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MainLoop {
    //计算机先手
    public static void FirstHand() throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Scanner scanner = new Scanner(System.in);

        NodePOJO rootNodePOJO = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", 0);
        System.out.println("computerX: " + rootNodePOJO.getX());
        System.out.println("computerY: " + rootNodePOJO.getY());
        String comVal = "black";
        String humVal = "white";

        System.out.println("computerValue: " + comVal);
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
            while (true) {
                SqlSession sqlSession1 = sqlSessionFactory.openSession();

                System.out.println("Input Human X: ");
                Integer huX = scanner.nextInt();
                System.out.println("Input Human Y: ");
                Integer huY = scanner.nextInt();

                ChessboardPOJO chessboardPOJO = sqlSession1.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", beginNode.getChessboardId());
                List<String> lines = chessboardPOJO.getLines();
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
                System.out.println("computerX: " + endNode.getX());
                System.out.println("computerY: " + endNode.getY());
                System.out.println("computerValue: " + comVal);

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
        scanner.close();
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

    public static void OnesOwn() throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        NodePOJO rootNodePOJO = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", 0);

        String comVal1 = "black";
        String comVal2 = "white";

        NodePOJO beginNode = rootNodePOJO;
        boolean result;
        Integer i = 0;
        //循环一次
        while (i < 1){
            DecideMan decideMan = new DecideMan();
            JudgeMan judgeMan = new JudgeMan();
            RecordMan recordMan = new RecordMan();
            while (true){
                NodePOJO endNode = decideMan.Decide(beginNode);
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);
                if (result == true) {
                    recordMan.UpdateRecord(2);
                    System.out.println("white win");
                    break;
                }
                beginNode = endNode;
                endNode = decideMan.Decide(beginNode);
                recordMan.Record(beginNode, endNode);
                result = judgeMan.Judge(endNode);
                if (result == true) {
                    recordMan.UpdateRecord(1);
                    System.out.println("black win");
                    break;
                }
            }
            i++;
        }
        sqlSession.close();
        return;
    }

    public static void main(String[] args) throws IOException {
        OnesOwn();
    }
}

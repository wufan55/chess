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
    //计算机后手
    public static void main(String[] args) throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Scanner scanner = new Scanner(System.in);

        DecideMan decideMan = new DecideMan();
        RecordMan recordMan = new RecordMan();
        JudgeMan judgeMan = new JudgeMan();
        NodePOJO rootNodePOJO = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", 0);
        boolean result;

        NodePOJO beginNode = rootNodePOJO;
        //循环一次
        int i = 0;
        while (i < 1) {
            while (true) {
                result = judgeMan.Judge(beginNode);

                String strValue;
                if (beginNode.getValue() == 1) strValue = "black";
                else strValue = "white";

                if (result == true) {
                    recordMan.UpdateRecord(beginNode.getValue());
                    System.out.println(strValue + " " + "win");
                    break;
                }

                NodePOJO endNode = decideMan.Decide(beginNode);
                System.out.println("computerX: " + endNode.getX());
                System.out.println("computerY: " + endNode.getY());
                System.out.println("computerValue: " + strValue);
                //记录begin to end
                recordMan.Record(beginNode, endNode);

                System.out.println("Input Human X: ");
                Integer huX = scanner.nextInt();
                System.out.println("Input Human Y: ");
                Integer huY = scanner.nextInt();
                Integer huVal = beginNode.getValue();

                ChessboardPOJO chessboardPOJO = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", endNode.getChessboardId());
                List<String> lines = chessboardPOJO.getLines();
                StringBuilder stringBuilder = new StringBuilder(lines.get(huX-1));
                stringBuilder.setCharAt(huY-1, Character.forDigit(huVal, 10));
                lines.set(huX-1, stringBuilder.toString());
                chessboardPOJO.setLines(lines);

                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);
                Integer chessboardId = chessboardPOJO.getId();

                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(huX);
                nodePOJO.setY(huY);
                nodePOJO.setValue(huVal);
                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                //记录end to next
                recordMan.Record(endNode, nodePOJO);
                beginNode = nodePOJO;
            }
            i++;
        }
        sqlSession.close();
        scanner.close();
        return;
    }
}

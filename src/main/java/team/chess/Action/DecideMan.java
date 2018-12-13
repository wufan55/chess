package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.List;

public class DecideMan {
    private SqlUtil sqlUtil;

    public NodePOJO weightDecide(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Integer nodeBeginId = nodeBegin.getId();
        Integer nodeBeginValue = nodeBegin.getValue();

        List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryList", nodeBeginId);

        StepPOJO stepDecide = new StepPOJO();
        RelationPOJO relationDecide = new RelationPOJO();
        for (RelationPOJO relationPOJO : relationPOJOS){
            Integer stepId = relationPOJO.getStepId();
            StepPOJO stepPOJO = sqlSession.selectOne("team.chess.Mapper.StepMapper.queryObject", stepId);
            Integer total = stepPOJO.getTotal();
            if (nodeBeginValue == 1) {
                Integer whiteWin = stepPOJO.getWhiteWin();
                Float currWinPoint = Float.valueOf(whiteWin / total);
                Float winPoint = Float.valueOf(stepDecide.getWhiteWin() / stepDecide.getTotal());
                if (currWinPoint > winPoint) {
                    stepDecide = stepPOJO;
                    relationDecide = relationPOJO;
                }
            }
            if (nodeBeginValue == 2) {
                Integer blackWin = stepPOJO.getBlackWin();
                Float currWinPoint = Float.valueOf(blackWin / total);
                Float winPoint = Float.valueOf(stepDecide.getBlackWin() / stepDecide.getTotal());
                if (currWinPoint > winPoint) {
                    stepDecide = stepPOJO;
                    relationDecide = relationPOJO;
                }
            }
        }
        Integer nodeEndId = relationDecide.getNodeEndId();
        NodePOJO nodeEnd = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", nodeEndId);
        sqlSession.close();
        return nodeEnd;
    }

    //饱和度分析
    public Float filledLevelDecide(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //获取total
        Integer total = 0;
        Integer chessBoardId = nodeBegin.getChessboardId();
        ChessboardPOJO chessboardPOJO = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", chessBoardId);
        List<String> lines = chessboardPOJO.getLines();
        Integer x = nodeBegin.getX();
        Integer y = nodeBegin.getY();
        if (x-2 >= 0 && y-2 >= 0 && lines.get(x-2).charAt(y-2) != '0') total++;
        if (y-2 >= 0 && lines.get(x-1).charAt(y-2) != '0') total++;
        if (x <= 14 && y-2 >=0 && lines.get(x).charAt(y-2) != '0') total++;
        if (x-2 >= 0 && lines.get(x-2).charAt(y-1) != '0') total++;
        if (x <= 14 && lines.get(x).charAt(y-1) != '0') total++;
        if (x-2 >= 0 && y <= 14 && lines.get(x-2).charAt(y) != '0') total++;
        if (y <= 14 && lines.get(x-1).charAt(y) != '0') total++;
        if (x <= 14 && y <= 14 && lines.get(x).charAt(x) != '0') total++;

        //获取known
        Integer known = 0;
        Integer nodeBeginId = nodeBegin.getId();
        List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryList", nodeBeginId);
        known = relationPOJOS.size();

        //饱和度
        Float filledLevel;
        if (total == 0) filledLevel = Float.valueOf(0);
        else  filledLevel = Float.valueOf(known / total);

        sqlSession.close();
        return filledLevel;
    }
}

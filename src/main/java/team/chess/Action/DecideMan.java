package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.List;

public class DecideMan {
    private SqlUtil sqlUtil;

    public NodePOJO Decide(NodePOJO nodeBegin) throws IOException {
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
        NodePOJO nodeEnd = sqlSession.selectOne("team.chess.Mapper.NodeMapper", nodeEndId);
        return nodeEnd;
    }
}

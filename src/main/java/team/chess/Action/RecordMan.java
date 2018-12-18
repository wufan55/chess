package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordMan {
    private List<StepPOJO> stepPOJOS;

    private SqlUtil sqlUtil;

    public RecordMan(){
        stepPOJOS = new ArrayList<>();
        sqlUtil = new SqlUtil();
    }

    public List<StepPOJO> getStepPOJOS() {
        return stepPOJOS;
    }

    public void setStepPOJOS(List<StepPOJO> stepPOJOS) {
        this.stepPOJOS = stepPOJOS;
    }

    public void Record(StepPOJO step){
        stepPOJOS.add(step);
    }

    public void Record(NodePOJO beginNode, NodePOJO endNode) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Map params = new HashMap();
        params.put("nodeBeginId", beginNode.getId());
        params.put("nodeEndId", endNode.getId());
        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", params);
        //创建关系
        if (relationPOJOList.size() == 0){
            StepPOJO stepPOJO = new StepPOJO();
            sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
            //记录
            stepPOJOS.add(stepPOJO);
            Integer stepId = stepPOJO.getId();
            RelationPOJO relationPOJO = new RelationPOJO();
            relationPOJO.setNodeBeginId(beginNode.getId());
            relationPOJO.setNodeEndId(endNode.getId());
            relationPOJO.setStepId(stepId);
            sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
        }
        else {
            RelationPOJO relationPOJO = relationPOJOList.get(0);
            Integer stepId = relationPOJO.getStepId();
            StepPOJO stepPOJO = sqlSession.selectOne("team.chess.Mapper.StepMapper.queryObject", stepId);
            //记录
            stepPOJOS.add(stepPOJO);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    public void UpdateRecord(Integer value) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();

        for (StepPOJO stepPOJO : stepPOJOS){
            SqlSession sqlSession = sqlSessionFactory.openSession();
            Integer total = stepPOJO.getTotal();
            Integer whiteWin = stepPOJO.getWhiteWin();
            Integer blackWin = stepPOJO.getBlackWin();
            stepPOJO.setTotal(total+1);
            if (value == 1) stepPOJO.setBlackWin(blackWin+1);
            if (value == 2) stepPOJO.setWhiteWin(whiteWin+1);

            sqlSession.update("team.chess.Mapper.StepMapper.update", stepPOJO);
            sqlSession.commit();
            sqlSession.close();
        }
        return;
    }
}

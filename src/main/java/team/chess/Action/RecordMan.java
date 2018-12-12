package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordMan {
    private List<StepPOJO> stepPOJOS;

    private SqlUtil sqlUtil;

    RecordMan(){
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

    public void UpdateRecord(Integer value) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        for (StepPOJO stepPOJO : stepPOJOS){
            Integer total = stepPOJO.getTotal();
            Integer whiteWin = stepPOJO.getWhiteWin();
            Integer blackWin = stepPOJO.getBlackWin();
            stepPOJO.setTotal(total+1);
            if (value == 1) stepPOJO.setTotal(blackWin+1);
            if (value == 2) stepPOJO.setTotal(whiteWin+1);

            sqlSession.update("team.chess.Mapper.StepMapper.update", stepPOJO);
        }
        sqlSession.close();
    }
}

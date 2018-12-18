package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class testAction {
    public static void main(String[] args) throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //获取对象
        StepPOJO stepPOJO = sqlSession.selectOne("team.chess.Mapper.StepMapper.queryObject", 1);
        stepPOJO.setTotal(2);
        sqlSession.update("team.chess.Mapper.StepMapper.update", stepPOJO);
        sqlSession.commit();
        sqlSession.close();
    }
}

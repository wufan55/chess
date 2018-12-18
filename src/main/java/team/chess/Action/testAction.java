package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.RelationPOJO;
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
        RelationPOJO relationPOJO = new RelationPOJO();
        relationPOJO.setNodeBeginId(1);
        List<RelationPOJO> testPojo = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryList", relationPOJO);
        if (testPojo.size() != 0) relationPOJO = testPojo.get(0);
        System.out.println(relationPOJO.getStepId());
        sqlSession.close();
    }
}

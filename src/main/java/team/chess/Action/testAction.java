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
        List<NodePOJO> nodePOJO = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", 0);
        System.out.println(nodePOJO.get(0).getValue());

        sqlSession.commit();
        sqlSession.close();
    }
}

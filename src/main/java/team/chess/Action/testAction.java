package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.NodePOJO;
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
        NodePOJO nodePOJO = new NodePOJO();
        nodePOJO.setChessboardId(1);
        nodePOJO.setX(1);
        nodePOJO.setY(1);
        nodePOJO.setValue(1);
        sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
        System.out.println(nodePOJO.getId());

        sqlSession.commit();
        sqlSession.close();
    }
}

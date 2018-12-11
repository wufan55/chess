package team.chess.Action;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import team.chess.POJO.testPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.io.InputStream;

public class testAction {
    public static void main(String[] args) throws IOException {
        SqlUtil sqlUtil = new SqlUtil();
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //获取对象
        testPOJO testPojo = sqlSession.selectOne("team.chess.Mapper.testMapper.queryObject", 1);
        System.out.println(testPojo.getName());
        sqlSession.close();
    }
}

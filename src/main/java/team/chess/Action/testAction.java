package team.chess.Action;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import team.chess.POJO.testPOJO;

import java.io.IOException;
import java.io.InputStream;

public class testAction {
    public static void main(String[] args) throws IOException {
        //读取配置文件
        String resource = "resource/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        //构建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //获取对象
        testPOJO testPojo = sqlSession.selectOne("team.chess.Mapper.testMapper.queryObject", 1);
        System.out.println(testPojo.getName());
        sqlSession.close();
    }
}

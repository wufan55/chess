package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.List;

public class JudgeMan {
    SqlUtil sqlUtil = new SqlUtil();

    public boolean Judge(NodePOJO node) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        boolean result = false;

        Integer chessboardId = node.getChessboardId();
        Integer x = node.getX();
        Integer y = node.getY();
        char value = Character.forDigit(node.getValue(), 10);

        ChessboardPOJO chessboardPOJO = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", chessboardId);
        List<String> lines = chessboardPOJO.getLines();

        int num = 1;
        int temp = 0;
        //水平方向判断
        String currLine = lines.get(x-1);
        while ((currLine.charAt(y-temp-2) == value && y-temp-2 >= 0) || currLine.charAt(y+temp) == value && y+temp <= 14){
            num++;
            temp++;
        }
        if (num == 5) result = true;
        else num = 1;

        //垂直方向判断

        currLine.charAt(y-1);

        return result;
    }
}

package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.List;

public class JudgeMan {
    private SqlUtil sqlUtil;

    public JudgeMan(){
        sqlUtil = new SqlUtil();
    }

    public boolean Judge(NodePOJO node) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        boolean result = false;

        Integer chessboardId = node.getChessboardId();
        Integer x = node.getX();
        Integer y = node.getY();
        //把Integer转化成char
        char value = Character.forDigit(node.getValue(), 10);

        ChessboardPOJO chessboardPOJO = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", chessboardId);
        List<String> lines = chessboardPOJO.getLines();

        int num = 1;
        int temp = 0;
        //水平方向判断
        String currLine = lines.get(x-1);
        while ((y-temp-2 >= 0 && currLine.charAt(y-temp-2) == value) || (y+temp <= 14 && currLine.charAt(y+temp) == value)){
            num++;
            temp++;
        }
        if (num == 5) result = true;
        else num = 1;

        //垂直方向判断
        temp = 0;
        while ((x-temp-2 >= 0 && lines.get(x-temp-2).charAt(y-1) == value) || (x+temp <= 14 && lines.get(x+temp).charAt(y-1) == value)){
            num++;
            temp++;
        }
        if (num == 5) result = true;
        else num = 1;

        //左斜方向判断
        temp = 0;
        while ((x-temp-2 >= 0 && y-temp-2 >= 0 && lines.get(x-temp-2).charAt(y-temp-2) == value) ||
                (x+temp <= 14 && y+temp <= 14 && lines.get(x+temp).charAt(y+temp) == value)){
            num++;
            temp++;
        }
        if (num == 5) result = true;
        else num = 1;

        //右斜方向判断
        temp = 0;
        while ((x+temp <= 14 && y-temp-2 >= 0 && lines.get(x+temp).charAt(y-temp-2) == value) ||
                (x-temp-2 >= 0 && y+temp <= 14 && lines.get(x-temp-2).charAt(y+temp) == value)){
            num++;
            temp++;
        }
        if (num == 5) result = true;

        return result;
    }
}

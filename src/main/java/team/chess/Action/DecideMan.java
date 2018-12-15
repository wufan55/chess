package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecideMan {
    private SqlUtil sqlUtil;

    DecideMan(){
        sqlUtil = new SqlUtil();
    }

    //未完成
    public NodePOJO Decide(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Float filledLevel = filledLevel(nodeBegin);
        if (filledLevel > 1/2) {
            sqlSession.close();
            return weightDecide(nodeBegin);
        }
        //创建新分支
        else {
            NodePOJO nodeEnd = new NodePOJO();

            Integer x = nodeBegin.getX();
            Integer y = nodeBegin.getY();
            Integer value = nodeBegin.getValue();
            ChessboardPOJO chessboardBegin = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", nodeBegin.getChessboardId());
            ChessboardPOJO chessboardPOJO = chessboardBegin;
            List<String> lines = chessboardBegin.getLines();

            if (x-2 >= 0 && y-2 >= 0 && lines.get(x-2).charAt(y-2) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
                stringBuilder.setCharAt(y-2, Character.forDigit(value, 10));
                lines.set(x-2, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x-1);
                nodePOJO.setY(y-1);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (y-2 >= 0 && lines.get(x-1).charAt(y-2) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x-1));
                stringBuilder.setCharAt(y-2, Character.forDigit(value, 10));
                lines.set(x-1, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x);
                nodePOJO.setY(y-1);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (x <= 14 && y-2 >=0 && lines.get(x).charAt(y-2) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x));
                stringBuilder.setCharAt(y-2, Character.forDigit(value, 10));
                lines.set(x, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x+1);
                nodePOJO.setY(y-1);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (x-2 >= 0 && lines.get(x-2).charAt(y-1) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
                stringBuilder.setCharAt(y-1, Character.forDigit(value, 10));
                lines.set(x-2, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x-1);
                nodePOJO.setY(y);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (x <= 14 && lines.get(x).charAt(y-1) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x));
                stringBuilder.setCharAt(y-1, Character.forDigit(value, 10));
                lines.set(x, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x+1);
                nodePOJO.setY(y);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (x-2 >= 0 && y <= 14 && lines.get(x-2).charAt(y) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
                stringBuilder.setCharAt(y, Character.forDigit(value, 10));
                lines.set(x-2, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x-1);
                nodePOJO.setY(y+1);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (y <= 14 && lines.get(x-1).charAt(y) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x-1));
                stringBuilder.setCharAt(y, Character.forDigit(value, 10));
                lines.set(x-1, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x);
                nodePOJO.setY(y+1);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            if (x <= 14 && y <= 14 && lines.get(x).charAt(x) == '0'){
                StringBuilder stringBuilder = new StringBuilder(lines.get(x));
                stringBuilder.setCharAt(y, Character.forDigit(value, 10));
                lines.set(x, stringBuilder.toString());
                chessboardPOJO.setLines(lines);
                List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                //判断网路中是否有该棋盘记录
                //如果不存在就创建
                if (chessboardPOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                else chessboardPOJO = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardPOJO.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x+1);
                nodePOJO.setY(y+1);
                Integer val = (value == 1) ? 2 : 1;
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，创建节点
                if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                else nodePOJO = nodePOJOList.get(0);

                nodeEnd = nodePOJO;
                Integer nodeEndId = nodeEnd.getId();
                Integer nodeBeginId = nodeBegin.getId();

                Map param = new HashMap();
                param.put("nodeBeginId", nodeBeginId);
                param.put("nodeEndId", nodeEndId);
                List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                //判断网络中节点间的关系是否存在
                //如果不存在，创建关系
                //先创建step，再创建relation
                if (relationPOJOList.size() == 0){
                    StepPOJO stepPOJO = new StepPOJO();
                    sqlSession.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                    Integer stepId = stepPOJO.getId();
                    RelationPOJO relationPOJO = new RelationPOJO();
                    relationPOJO.setNodeBeginId(nodeBeginId);
                    relationPOJO.setNodeEndId(nodeEndId);
                    relationPOJO.setStepId(stepId);
                    sqlSession.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                }
            }

            sqlSession.close();
            return nodeEnd;
        }
    }

    //单纯权重决策
    public NodePOJO weightDecide(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Integer nodeBeginId = nodeBegin.getId();
        Integer nodeBeginValue = nodeBegin.getValue();

        List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryList", nodeBeginId);

        StepPOJO stepDecide = new StepPOJO();
        RelationPOJO relationDecide = new RelationPOJO();
        for (RelationPOJO relationPOJO : relationPOJOS){
            Integer stepId = relationPOJO.getStepId();
            StepPOJO stepPOJO = sqlSession.selectOne("team.chess.Mapper.StepMapper.queryObject", stepId);
            Integer total = stepPOJO.getTotal();
            if (nodeBeginValue == 1) {
                Integer whiteWin = stepPOJO.getWhiteWin();
                Float currWinPoint = Float.valueOf(whiteWin / total);
                Float winPoint = Float.valueOf(stepDecide.getWhiteWin() / stepDecide.getTotal());
                if (currWinPoint > winPoint) {
                    stepDecide = stepPOJO;
                    relationDecide = relationPOJO;
                }
            }
            if (nodeBeginValue == 2) {
                Integer blackWin = stepPOJO.getBlackWin();
                Float currWinPoint = Float.valueOf(blackWin / total);
                Float winPoint = Float.valueOf(stepDecide.getBlackWin() / stepDecide.getTotal());
                if (currWinPoint > winPoint) {
                    stepDecide = stepPOJO;
                    relationDecide = relationPOJO;
                }
            }
        }
        Integer nodeEndId = relationDecide.getNodeEndId();
        NodePOJO nodeEnd = sqlSession.selectOne("team.chess.Mapper.NodeMapper.queryObject", nodeEndId);
        sqlSession.close();
        return nodeEnd;
    }

    //饱和度分析
    public Float filledLevel(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //获取total
        Integer total = 0;
        Integer chessBoardId = nodeBegin.getChessboardId();
        ChessboardPOJO chessboardPOJO = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", chessBoardId);
        List<String> lines = chessboardPOJO.getLines();
        Integer x = nodeBegin.getX();
        Integer y = nodeBegin.getY();
        if (x-2 >= 0 && y-2 >= 0 && lines.get(x-2).charAt(y-2) != '0') total++;
        if (y-2 >= 0 && lines.get(x-1).charAt(y-2) != '0') total++;
        if (x <= 14 && y-2 >=0 && lines.get(x).charAt(y-2) != '0') total++;
        if (x-2 >= 0 && lines.get(x-2).charAt(y-1) != '0') total++;
        if (x <= 14 && lines.get(x).charAt(y-1) != '0') total++;
        if (x-2 >= 0 && y <= 14 && lines.get(x-2).charAt(y) != '0') total++;
        if (y <= 14 && lines.get(x-1).charAt(y) != '0') total++;
        if (x <= 14 && y <= 14 && lines.get(x).charAt(x) != '0') total++;

        //获取known
        Integer known = 0;
        Integer nodeBeginId = nodeBegin.getId();
        List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryList", nodeBeginId);
        known = relationPOJOS.size();

        //饱和度
        Float filledLevel;
        if (total == 0) filledLevel = Float.valueOf(0);
        else  filledLevel = Float.valueOf(known / total);

        sqlSession.close();
        return filledLevel;
    }
}

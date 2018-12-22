package team.chess.Action;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import team.chess.POJO.ChessboardPOJO;
import team.chess.POJO.NodePOJO;
import team.chess.POJO.RelationPOJO;
import team.chess.POJO.StepPOJO;
import team.chess.Util.SqlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecideMan {
    private SqlUtil sqlUtil;

    public DecideMan(){
        sqlUtil = new SqlUtil();
    }

    public NodePOJO Decide(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        NodePOJO threeStop = threeStopUp(nodeBegin);
        if (threeStop != null) return threeStop;
        else {
            Float filledLevel = filledLevel(nodeBegin);
            //饱和度大于0.5进行权重决策
            if (filledLevel > 1/2) {
                sqlSession.commit();
                sqlSession.close();
                return weightDecide(nodeBegin);
            }
            //否则创建新分支，或更新现有循环次数最少的分支
            else {
                NodePOJO nodeEnd = new NodePOJO();

                Integer totalTimeChoice = totalTime(nodeBegin);
                Integer x = nodeBegin.getX();
                Integer y = nodeBegin.getY();
                Integer value = nodeBegin.getValue();
                Integer val = (value == 1) ? 2 : 1;
                ChessboardPOJO chessboardBegin = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", nodeBegin.getChessboardId());
                ChessboardPOJO chessboardPOJO = chessboardBegin;
                List<String> lines = chessboardBegin.getLines();

                if (totalTimeChoice == 1){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
                    stringBuilder.setCharAt(y-2, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 2){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x-1));
                    stringBuilder.setCharAt(y-2, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 3){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x));
                    stringBuilder.setCharAt(y-2, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 4){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
                    stringBuilder.setCharAt(y-1, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 5){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x));
                    stringBuilder.setCharAt(y-1, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 6){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
                    stringBuilder.setCharAt(y, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 7){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x-1));
                    stringBuilder.setCharAt(y, Character.forDigit(val, 10));
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

                else if (totalTimeChoice == 8){
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x));
                    stringBuilder.setCharAt(y, Character.forDigit(val, 10));
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

                sqlSession.commit();
                sqlSession.close();
                return nodeEnd;
            }
        }
    }

    //三子围堵
    public NodePOJO threeStopUp(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Integer chessboardId = nodeBegin.getChessboardId();
        ChessboardPOJO chessboardBegin = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", chessboardId);
        ChessboardPOJO chessboardPOJO = chessboardBegin;

        Integer x = nodeBegin.getX();
        Integer y = nodeBegin.getY();
        //把Integer转化成char
        char value = Character.forDigit(nodeBegin.getValue(), 10);
        Integer val = (value == '1') ? 2 : 1;
        List<String> lines = chessboardPOJO.getLines();
        String currLine = lines.get(x-1);

        Integer x1;
        Integer x2;
        Integer y1;
        Integer y2;

        int num = 1;
        int temp = 0;

        //水平方向，左
        while (y-temp-2 >= 0 && currLine.charAt(y-temp-2) == value){
            num++;
            temp++;
        }
        x1 = x;
        y1 = y - temp - 1;
        temp = 0;//右
        while (y+temp <= 14 && currLine.charAt(y+temp) == value){
            num++;
            temp++;
        }
        x2 = x;
        y2 = y + temp + 1;
        if (y1 >= 1 && y2 <= 15){
            if (currLine.charAt(y1-1) == '0' && currLine.charAt(y2-1) == '0') {
                if (num == 3) {
                    //修改chessboard，查chessboard/
                    //查node/
                    //调用辅助函数/
                    //比较weight返回node，可能要new
                    Float weight1;
                    Float weight2;
                    NodePOJO nodePOJO1 = new NodePOJO();
                    NodePOJO nodePOJO2 = new NodePOJO();
                    ChessboardPOJO chessboardPOJO1;
                    ChessboardPOJO chessboardPOJO2;

                    //获取weight1
                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO1 = chessboardPOJO;
                    List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x1);
                        nodePOJO.setY(y1);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight1 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    //获取weight2
                    chessboardPOJO = chessboardBegin;
                    stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO2 = chessboardPOJO;
                    chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x2);
                        nodePOJO.setY(y2);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight2 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    if (weight1 > weight2) {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO1);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO1);
                        else chessboardPOJO1 = chessboardPOJOList.get(0);
                        nodePOJO1.setChessboardId(chessboardPOJO1.getId());
                        nodePOJO1.setX(x1);nodePOJO1.setY(y1);nodePOJO1.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO1);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO1);
                        else nodePOJO1 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO1.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO1.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO1;
                    }
                    else {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO2);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO2);
                        else chessboardPOJO2 = chessboardPOJOList.get(0);
                        nodePOJO2.setChessboardId(chessboardPOJO2.getId());
                        nodePOJO2.setX(x2);nodePOJO2.setY(y2);nodePOJO2.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO2);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO2);
                        else nodePOJO2 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO2.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO2.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO2;
                    }
                }
                if (num == 4) return null;
            }
            else if (currLine.charAt(y1-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x1);
                    nodePOJO.setY(y1);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
            else if (currLine.charAt(y2-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x2);
                    nodePOJO.setY(y2);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
        }
        num = 1;
        temp = 0;

        //垂直方向，上
        while (x-temp-2 >= 0 && lines.get(x-temp-2).charAt(y-1) == value){
            num++;
            temp++;
        }
        x1 = x - temp - 1;
        y1 = y;
        temp = 0;//下
        while (x+temp <= 14 && lines.get(x+temp).charAt(y-1) == value){
            num++;
            temp++;
        }
        x2 = x + temp + 1;
        y2 = y;
        if (x1 >= 1 && x2 <= 15) {
            if (lines.get(x1-1).charAt(y1-1) == '0' && lines.get(x2-1).charAt(y2-1) == '0'){
                if (num == 3) {
                    //修改chessboard，查chessboard/
                    //查node/
                    //调用辅助函数/
                    //比较weight返回node，可能要new
                    Float weight1;
                    Float weight2;
                    NodePOJO nodePOJO1 = new NodePOJO();
                    NodePOJO nodePOJO2 = new NodePOJO();
                    ChessboardPOJO chessboardPOJO1;
                    ChessboardPOJO chessboardPOJO2;

                    //获取weight1
                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO1 = chessboardPOJO;
                    List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x1);
                        nodePOJO.setY(y1);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight1 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    //获取weight2
                    chessboardPOJO = chessboardBegin;
                    stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO2 = chessboardPOJO;
                    chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x2);
                        nodePOJO.setY(y2);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight2 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    if (weight1 > weight2) {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO1);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO1);
                        else chessboardPOJO1 = chessboardPOJOList.get(0);
                        nodePOJO1.setChessboardId(chessboardPOJO1.getId());
                        nodePOJO1.setX(x1);nodePOJO1.setY(y1);nodePOJO1.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO1);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO1);
                        else nodePOJO1 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO1.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO1.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO1;
                    }
                    else {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO2);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO2);
                        else chessboardPOJO2 = chessboardPOJOList.get(0);
                        nodePOJO2.setChessboardId(chessboardPOJO2.getId());
                        nodePOJO2.setX(x2);nodePOJO2.setY(y2);nodePOJO2.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO2);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO2);
                        else nodePOJO2 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO2.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO2.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO2;
                    }
                }
                if (num == 4) return null;
            }
            else if (lines.get(x1-1).charAt(y1-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x1);
                    nodePOJO.setY(y1);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
            else if (lines.get(x2-1).charAt(y2-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x2);
                    nodePOJO.setY(y2);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
        }

        num = 1;
        temp = 0;

        //左斜方向，左上
        while (x-temp-2 >= 0 && y-temp-2 >= 0 && lines.get(x-temp-2).charAt(y-temp-2) == value){
            num++;
            temp++;
        }
        x1 = x - temp - 1;
        y1 = y - temp - 1;
        temp = 0;//右下
        while (x+temp <= 14 && y+temp <= 14 && lines.get(x+temp).charAt(y+temp) == value){
            num++;
            temp++;
        }
        x2 = x + temp + 1;
        y2 = y + temp + 1;
        if (x1 >= 1 && y1 >= 1 && x2 <= 15 && y2 <= 15) {
            if (lines.get(x1-1).charAt(y1-1) == '0' && lines.get(x2-1).charAt(y2-1) == '0') {
                if (num == 3) {
                    //修改chessboard，查chessboard/
                    //查node/
                    //调用辅助函数/
                    //比较weight返回node，可能要new
                    Float weight1;
                    Float weight2;
                    NodePOJO nodePOJO1 = new NodePOJO();
                    NodePOJO nodePOJO2 = new NodePOJO();
                    ChessboardPOJO chessboardPOJO1;
                    ChessboardPOJO chessboardPOJO2;

                    //获取weight1
                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO1 = chessboardPOJO;
                    List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x1);
                        nodePOJO.setY(y1);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight1 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    //获取weight2
                    chessboardPOJO = chessboardBegin;
                    stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO2 = chessboardPOJO;
                    chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x2);
                        nodePOJO.setY(y2);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight2 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    if (weight1 > weight2) {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO1);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO1);
                        else chessboardPOJO1 = chessboardPOJOList.get(0);
                        nodePOJO1.setChessboardId(chessboardPOJO1.getId());
                        nodePOJO1.setX(x1);nodePOJO1.setY(y1);nodePOJO1.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO1);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO1);
                        else nodePOJO1 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO1.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO1.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO1;
                    }
                    else {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO2);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO2);
                        else chessboardPOJO2 = chessboardPOJOList.get(0);
                        nodePOJO2.setChessboardId(chessboardPOJO2.getId());
                        nodePOJO2.setX(x2);nodePOJO2.setY(y2);nodePOJO2.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO2);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO2);
                        else nodePOJO2 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO2.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO2.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO2;
                    }
                }
                if (num == 4) return null;
            }
            else if (lines.get(x1-1).charAt(y1-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x1);
                    nodePOJO.setY(y1);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
            else if (lines.get(x2-1).charAt(y2-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x2);
                    nodePOJO.setY(y2);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
        }
        num = 1;
        temp = 0;

        //右斜方向，右上
        while (x-temp-2 >= 0 && y+temp <= 14 && lines.get(x-temp-2).charAt(y+temp) == value){
            num++;
            temp++;
        }
        x1 = x - temp - 1;
        y1 = y + temp + 1;
        temp = 0;//左下
        while (x+temp <= 14 && y-temp-2 >= 0 && lines.get(x+temp).charAt(y-temp-2) == value){
            num++;
            temp++;
        }
        x2 = x + temp + 1;
        y2 = y - temp - 1;
        if (x1 >= 1 && y1 <= 15 && x2 <= 15 && y2 >= 1){
            if (lines.get(x1-1).charAt(y1-1) == '0' && lines.get(x2-1).charAt(y2-1) == '0') {
                if (num == 3) {
                    //修改chessboard，查chessboard/
                    //查node/
                    //调用辅助函数/
                    //比较weight返回node，可能要new
                    Float weight1;
                    Float weight2;
                    NodePOJO nodePOJO1 = new NodePOJO();
                    NodePOJO nodePOJO2 = new NodePOJO();
                    ChessboardPOJO chessboardPOJO1;
                    ChessboardPOJO chessboardPOJO2;

                    //获取weight1
                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO1 = chessboardPOJO;
                    List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x1);
                        nodePOJO.setY(y1);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight1 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight1 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    //获取weight2
                    chessboardPOJO = chessboardBegin;
                    stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);
                    chessboardPOJO2 = chessboardPOJO;
                    chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    if (chessboardPOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                    else {
                        NodePOJO nodePOJO = new NodePOJO();
                        nodePOJO.setChessboardId(chessboardPOJO.getId());
                        nodePOJO.setX(x2);
                        nodePOJO.setY(y2);
                        nodePOJO.setValue(val);

                        List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                        if (nodePOJOList.size() == 0) weight2 = Float.valueOf(1/3);
                        else {
                            nodePOJO = nodePOJOList.get(0);
                            weight2 = choiceWeight(nodeBegin, nodePOJO);
                        }
                    }

                    if (weight1 > weight2) {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO1);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO1);
                        else chessboardPOJO1 = chessboardPOJOList.get(0);
                        nodePOJO1.setChessboardId(chessboardPOJO1.getId());
                        nodePOJO1.setX(x1);nodePOJO1.setY(y1);nodePOJO1.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO1);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO1);
                        else nodePOJO1 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO1.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO1.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO1;
                    }
                    else {
                        SqlSession sqlSession1 = sqlSessionFactory.openSession();
                        chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO2);
                        if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO2);
                        else chessboardPOJO2 = chessboardPOJOList.get(0);
                        nodePOJO2.setChessboardId(chessboardPOJO2.getId());
                        nodePOJO2.setX(x2);nodePOJO2.setY(y2);nodePOJO2.setValue(val);
                        List<NodePOJO> nodePOJOList = sqlSession1.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO2);
                        if (nodePOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.NodeMapper.save", nodePOJO2);
                        else nodePOJO2 = nodePOJOList.get(0);

                        Map param = new HashMap();
                        param.put("nodeBeginId", nodeBegin.getId());
                        param.put("nodeEndId", nodePOJO2.getId());
                        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
                        //判断网络中节点间的关系是否存在
                        //如果不存在，创建关系
                        //先创建step，再创建relation
                        if (relationPOJOList.size() == 0){
                            StepPOJO stepPOJO = new StepPOJO();
                            sqlSession1.insert("team.chess.Mapper.StepMapper.save", stepPOJO);
                            Integer stepId = stepPOJO.getId();
                            RelationPOJO relationPOJO = new RelationPOJO();
                            relationPOJO.setNodeBeginId(nodeBegin.getId());
                            relationPOJO.setNodeEndId(nodePOJO2.getId());
                            relationPOJO.setStepId(stepId);
                            sqlSession1.insert("team.chess.Mapper.RelationMapper.save", relationPOJO);
                        }

                        sqlSession1.commit();
                        sqlSession1.close();
                        return nodePOJO2;
                    }
                }
                if (num == 4) return null;
            }
            else if (lines.get(x1-1).charAt(y1-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x1-1));
                    stringBuilder.setCharAt(y1-1, Character.forDigit(val, 10));
                    lines.set(x1-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x1);
                    nodePOJO.setY(y1);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
            else if (lines.get(x2-1).charAt(y2-1) == '0') {
                if (num == 4) {
                    SqlSession sqlSession1 = sqlSessionFactory.openSession();

                    chessboardPOJO = chessboardBegin;
                    StringBuilder stringBuilder = new StringBuilder(lines.get(x2-1));
                    stringBuilder.setCharAt(y2-1, Character.forDigit(val, 10));
                    lines.set(x2-1, stringBuilder.toString());
                    chessboardPOJO.setLines(lines);

                    List<ChessboardPOJO> chessboardPOJOList = sqlSession1.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardPOJO);
                    //判断网路中是否有该棋盘记录
                    //如果不存在就创建
                    if (chessboardPOJOList.size() == 0) sqlSession1.insert("team.chess.Mapper.ChessboardMapper.save", chessboardPOJO);
                    else chessboardPOJO = chessboardPOJOList.get(0);

                    NodePOJO nodePOJO = new NodePOJO();
                    nodePOJO.setChessboardId(chessboardPOJO.getId());
                    nodePOJO.setX(x2);
                    nodePOJO.setY(y2);
                    nodePOJO.setValue(val);

                    List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                    //判断网络中节点是否存在
                    //如果不存在，创建节点
                    if (nodePOJOList.size() == 0) sqlSession.insert("team.chess.Mapper.NodeMapper.save", nodePOJO);
                    else nodePOJO = nodePOJOList.get(0);

                    Integer nodeEndId = nodePOJO.getId();
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
                    sqlSession1.commit();
                    sqlSession1.close();
                    return nodePOJO;
                }
            }
        }

        sqlSession.commit();
        sqlSession.close();
        return null;
    }

    //权重分析
    public NodePOJO weightDecide(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Integer nodeBeginId = nodeBegin.getId();
        Integer nodeBeginValue = nodeBegin.getValue();

        Map param = new HashMap();
        param.put("nodeBeginId", nodeBeginId);

        List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);

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

        sqlSession.commit();
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
        if (x-2 >= 0 && y-2 >= 0 && lines.get(x-2).charAt(y-2) == '0') total++;
        if (y-2 >= 0 && lines.get(x-1).charAt(y-2) == '0') total++;
        if (x <= 14 && y-2 >=0 && lines.get(x).charAt(y-2) == '0') total++;
        if (x-2 >= 0 && lines.get(x-2).charAt(y-1) == '0') total++;
        if (x <= 14 && lines.get(x).charAt(y-1) == '0') total++;
        if (x-2 >= 0 && y <= 14 && lines.get(x-2).charAt(y) == '0') total++;
        if (y <= 14 && lines.get(x-1).charAt(y) == '0') total++;
        if (x <= 14 && y <= 14 && lines.get(x).charAt(x) == '0') total++;

        //获取known
        Integer known = 0;
        Integer nodeBeginId = nodeBegin.getId();
        Map param = new HashMap();
        param.put("nodeBeginId", nodeBeginId);
        List<RelationPOJO> relationPOJOS = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
        known = relationPOJOS.size();

        //饱和度
        Float filledLevel;
        if (total == 0) filledLevel = Float.valueOf(0);
        else  filledLevel = Float.valueOf(known / total);

        sqlSession.commit();
        sqlSession.close();
        return filledLevel;
    }

    //循环次数分析
    //result不随机
    //增加随机
    public Integer totalTime(NodePOJO nodeBegin) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Integer chessboardBeginId = nodeBegin.getChessboardId();
        ChessboardPOJO chessboardBegin = sqlSession.selectOne("team.chess.Mapper.ChessboardMapper.queryObject", chessboardBeginId);
        List<String> lines = chessboardBegin.getLines();

        Integer x = nodeBegin.getX();
        Integer y = nodeBegin.getY();
        Integer value = nodeBegin.getValue();
        Integer val = (value == 1) ? 2 : 1;
        Integer total = 1;
        Integer result = 0;
        Integer total1 = 0;
        Integer total2 = 0;
        Integer total3 = 0;
        Integer total4 = 0;
        Integer total5 = 0;
        Integer total6 = 0;
        Integer total7 = 0;
        Integer total8 = 0;
        List<Integer> totalResult = new ArrayList<>();

        if (x-2 >= 0 && y-2 >= 0 && lines.get(x-2).charAt(y-2) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
            stringBuilder.setCharAt(y-2, Character.forDigit(val, 10));
            lines.set(x-2, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total1 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x-1);
                nodePOJO.setY(y-1);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total1 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total1 = temp;
                }
            }
        }
        if (y-2 >= 0 && lines.get(x-1).charAt(y-2) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x-1));
            stringBuilder.setCharAt(y-2, Character.forDigit(val, 10));
            lines.set(x-1, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total2 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x);
                nodePOJO.setY(y-1);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total2 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total2 = temp;
                }
            }
        }
        if (x <= 14 && y-2 >=0 && lines.get(x).charAt(y-2) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x));
            stringBuilder.setCharAt(y-2, Character.forDigit(val, 10));
            lines.set(x, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total3 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x+1);
                nodePOJO.setY(y-1);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total3 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total3 = temp;
                }
            }
        }
        if (x-2 >= 0 && lines.get(x-2).charAt(y-1) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
            stringBuilder.setCharAt(y-1, Character.forDigit(val, 10));
            lines.set(x-2, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total4 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x-1);
                nodePOJO.setY(y);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total4 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total4 = temp;
                }
            }
        }
        if (x <= 14 && lines.get(x).charAt(y-1) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x));
            stringBuilder.setCharAt(y-1, Character.forDigit(val, 10));
            lines.set(x, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total5 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x+1);
                nodePOJO.setY(y);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total5 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total5 = temp;
                }
            }
        }
        if (x-2 >= 0 && y <= 14 && lines.get(x-2).charAt(y) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x-2));
            stringBuilder.setCharAt(y, Character.forDigit(val, 10));
            lines.set(x-2, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total6 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x-1);
                nodePOJO.setY(y+1);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total6 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total6 = temp;
                }
            }
        }
        if (y <= 14 && lines.get(x-1).charAt(y) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x-1));
            stringBuilder.setCharAt(y, Character.forDigit(val, 10));
            lines.set(x-1, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total7 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x);
                nodePOJO.setY(y+1);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total7 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total7 = temp;
                }
            }
        }
        if (x <= 14 && y <= 14 && lines.get(x).charAt(x) == '0') {
            StringBuilder stringBuilder = new StringBuilder(lines.get(x));
            stringBuilder.setCharAt(y, Character.forDigit(val, 10));
            lines.set(x, stringBuilder.toString());
            chessboardBegin.setLines(lines);
            List<ChessboardPOJO> chessboardPOJOList = sqlSession.selectList("team.chess.Mapper.ChessboardMapper.queryList", chessboardBegin);
            //判断网路中是否有该棋盘记录
            //如果不存在返回total = 1
            if (chessboardPOJOList.size() == 0) total8 = 1;
            else {
                chessboardBegin = chessboardPOJOList.get(0);

                Integer chessboardId = chessboardBegin.getId();
                NodePOJO nodePOJO = new NodePOJO();
                nodePOJO.setChessboardId(chessboardId);
                nodePOJO.setX(x+1);
                nodePOJO.setY(y+1);
                nodePOJO.setValue(val);

                List<NodePOJO> nodePOJOList = sqlSession.selectList("team.chess.Mapper.NodeMapper.queryList", nodePOJO);
                //判断网络中节点是否存在
                //如果不存在，返回total = 1
                if (nodePOJOList.size() == 0) total8 = 1;
                else {
                    nodePOJO = nodePOJOList.get(0);
                    Integer temp = getTotal(nodeBegin, nodePOJO);
                    total8 = temp;
                }
            }
        }
        if (total1 != 0) totalResult.add(1);
        if (total2 != 0) totalResult.add(2);
        if (total3 != 0) totalResult.add(3);
        if (total4 != 0) totalResult.add(4);
        if (total5 != 0) totalResult.add(5);
        if (total6 != 0) totalResult.add(6);
        if (total7 != 0) totalResult.add(7);
        if (total8 != 0) totalResult.add(8);
        int index = 1 + (int)(Math.random() * totalResult.size());
        result = totalResult.get(index-1);
        sqlSession.commit();
        sqlSession.close();
        return result;
    }

    //辅助函数
    //默认nodeBegin和nodeEnd存在即数据库包装好的数据
    private Integer getTotal(NodePOJO nodeBegin, NodePOJO nodeEnd) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Integer total;
        Map param = new HashMap();
        param.put("nodeBeginId", nodeBegin.getId());
        param.put("nodeEndId", nodeEnd.getId());
        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
        if (relationPOJOList.size() == 0) total = 1;
        else {
            RelationPOJO relationPOJO = relationPOJOList.get(0);
            Integer stepId = relationPOJO.getStepId();
            StepPOJO stepPOJO = sqlSession.selectOne("team.chess.Mapper.StepMapper.queryObject", stepId);
            total = stepPOJO.getTotal();
        }

        sqlSession.close();
        return total;
    }

    private Integer getWinTime(NodePOJO nodeBegin, NodePOJO nodeEnd) throws IOException {
        SqlSessionFactory sqlSessionFactory = sqlUtil.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Map param = new HashMap();
        param.put("nodeBeginId", nodeBegin.getId());
        param.put("nodeEndId", nodeEnd.getId());
        List<RelationPOJO> relationPOJOList = sqlSession.selectList("team.chess.Mapper.RelationMapper.queryListByMap", param);
        if (relationPOJOList.size() == 0) {
            sqlSession.close();
            return 0;
        }
        else {
            RelationPOJO relationPOJO = relationPOJOList.get(0);
            Integer stepId = relationPOJO.getStepId();
            StepPOJO stepPOJO = sqlSession.selectOne("team.chess.Mapper.StepMapper.queryObject", stepId);
            sqlSession.close();
            if (nodeBegin.getValue() == 1) return stepPOJO.getWhiteWin();
            else return stepPOJO.getBlackWin();
        }


    }

    private Float choiceWeight(NodePOJO nodeBegin, NodePOJO nodeEnd) throws IOException {
        Float weight;
        Float total = Float.valueOf(getTotal(nodeBegin, nodeEnd));
        Float winPoint;
        winPoint = Float.valueOf(getWinTime(nodeBegin, nodeEnd) / getTotal(nodeBegin, nodeEnd));

        weight = total / 3 + 2 * winPoint / 3;
        return weight;
    }
}

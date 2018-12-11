package team.chess.POJO;

public class NodePOJO {
    private Integer id;

    private Integer chessboardId;

    private Integer x;

    private Integer y;

    private Integer value;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setChessboardId(Integer chessboardId) {
        this.chessboardId = chessboardId;
    }

    public Integer getChessboardId() {
        return chessboardId;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getX() {
        return x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getY() {
        return y;
    }
}

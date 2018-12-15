package team.chess.POJO;

public class StepPOJO {
    private Integer id;

    private Integer total;

    private Integer whiteWin;

    private Integer blackWin;

    //防止计算胜率时分母为0的情况
    public StepPOJO() {
        total = 1;
        whiteWin = 0;
        blackWin = 0;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }

    public void setWhiteWin(Integer whiteWin) {
        this.whiteWin = whiteWin;
    }

    public Integer getWhiteWin() {
        return whiteWin;
    }

    public void setBlackWin(Integer blackWin) {
        this.blackWin = blackWin;
    }

    public Integer getBlackWin() {
        return blackWin;
    }
}

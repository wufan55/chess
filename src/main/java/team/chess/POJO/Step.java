package team.chess.POJO;

public class Step {
    private Integer id;

    private Integer total;

    private Integer whiteWin;

    private Integer blackWin;

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

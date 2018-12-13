package team.chess.POJO;

import java.util.ArrayList;
import java.util.List;

public class ChessboardPOJO {
    private Integer id;

    private String line1;

    private String line2;

    private String line3;

    private String line4;

    private String line5;

    private String line6;

    private String line7;

    private String line8;

    private String line9;

    private String line10;

    private String line11;

    private String line12;

    private String line13;

    private String line14;

    private String line15;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine4(String line4) {
        this.line4 = line4;
    }

    public String getLine4() {
        return line4;
    }

    public void setLine5(String line5) {
        this.line5 = line5;
    }

    public String getLine5() {
        return line5;
    }

    public void setLine6(String line6) {
        this.line6 = line6;
    }

    public String getLine6() {
        return line6;
    }

    public void setLine7(String line7) {
        this.line7 = line7;
    }

    public String getLine7() {
        return line7;
    }

    public void setLine8(String line8) {
        this.line8 = line8;
    }

    public String getLine8() {
        return line8;
    }

    public void setLine9(String line9) {
        this.line9 = line9;
    }

    public String getLine9() {
        return line9;
    }

    public void setLine10(String line10) {
        this.line10 = line10;
    }

    public String getLine10() {
        return line10;
    }

    public void setLine11(String line11) {
        this.line11 = line11;
    }

    public String getLine11() {
        return line11;
    }

    public void setLine12(String line12) {
        this.line12 = line12;
    }

    public String getLine12() {
        return line12;
    }

    public void setLine13(String line13) {
        this.line13 = line13;
    }

    public String getLine13() {
        return line13;
    }

    public void setLine14(String line14) {
        this.line14 = line14;
    }

    public String getLine14() {
        return line14;
    }

    public void setLine15(String line15) {
        this.line15 = line15;
    }

    public String getLine15() {
        return line15;
    }

    //返回行集合
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);
        lines.add(line5);
        lines.add(line6);
        lines.add(line7);
        lines.add(line8);
        lines.add(line9);
        lines.add(line10);
        lines.add(line11);
        lines.add(line12);
        lines.add(line13);
        lines.add(line14);
        lines.add(line15);
        return lines;
    }

    public void setLines(List<String> lines) {
        line1 = lines.get(0);
        line2 = lines.get(1);
        line3 = lines.get(2);
        line4 = lines.get(3);
        line5 = lines.get(4);
        line6 = lines.get(5);
        line7 = lines.get(6);
        line8 = lines.get(7);
        line9 = lines.get(8);
        line10 = lines.get(9);
        line11 = lines.get(10);
        line12 = lines.get(11);
        line13 = lines.get(12);
        line14 = lines.get(13);
        line15 = lines.get(14);
    }
}

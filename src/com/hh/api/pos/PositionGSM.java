package com.hh.api.pos;

import java.math.BigDecimal;

import com.platform.annotation.Table;

/**
 * Created by Steven Hua on 2017/5/19.
 */

@Table(tableName = "bs_460", dataSourceName = "pos", pkName = "ID")
public class PositionGSM extends PositionBase<PositionGSM> {
    public static final PositionGSM dao = new PositionGSM().dao();

    private Integer MNC;
    private Integer AC;
    private Long CI;
    private String Roads;
    private BigDecimal Lata;
    private BigDecimal Lnga;
    private BigDecimal Latb;
    private BigDecimal Lngb;
    private BigDecimal Latg;
    private BigDecimal Lngg;
    private Integer RID;
    private Long RIDS;

    public static final String column_MNC = "MNC";
    public static final String column_AC = "AC";
    public static final String column_CI = "CI";
    public static final String column_Roads = "Roads";
    public static final String column_Lata = "Lata";
    public static final String column_Lnga = "Lnga";
    public static final String column_Latb = "Latb";
    public static final String column_Lngb = "Lngb";
    public static final String column_Latg = "Latg";
    public static final String column_Lngg = "Lngg";
    public static final String column_RID = "RID";
    public static final String column_RIDS = "RIDS";

    public PositionGSM() {
        super();
    }

    public Integer getMNC() {
        return get(column_MNC);
    }

    public void setMNC(Integer MNC) {
        this.set(column_MNC, MNC);
    }

    public Integer getAC() {
        return get(column_AC);
    }

    public void setAC(Integer AC) {
        this.set(column_AC, AC);
    }

    public Long getCI() {
        return get(column_CI);
    }

    public void setCI(Long CI) {
        this.set(column_CI, CI);
    }

    public String getRoads() {
        return get(column_Roads);
    }

    public void setRoads(String roads) {
        set(column_Roads, roads);
    }

    public BigDecimal getLata() {
        return get(column_Lata);
    }

    public void setLata(BigDecimal lata) {
        set(column_Lata, lata);
    }

    public BigDecimal getLnga() {
        return get(column_Lnga);
    }

    public void setLnga(BigDecimal lnga) {
        set(column_Lnga, lnga);
    }

    public BigDecimal getLatb() {
        return get(column_Latb);
    }

    public void setLatb(BigDecimal latb) {
        set(column_Latb, latb);
    }

    public BigDecimal getLngb() {
        return get(column_Lngb);
    }

    public void setLngb(BigDecimal lngb) {
        set(column_Lngb, lngb);
    }

    public BigDecimal getLatg() {
        return get(column_Latg);
    }

    public void setLatg(BigDecimal latg) {
        set(column_Latg, latg);
    }

    public BigDecimal getLngg() {
        return get(column_Lngg);
    }

    public void setLngg(BigDecimal lngg) {
        set(column_Lngg, lngg);
    }

    public Integer getRID() {
        return get(column_RID);
    }

    public void setRID(Integer RID) {
        this.set(column_RID, RID);
    }

    public Long getRIDS() {
        return get(column_RIDS);
    }

    public void setRIDS(Long RIDS) {
        this.set(column_RIDS, RIDS);
    }

    @Override
    public String toString() {
        return super.toString() + " PositionGSM{" +
                "MNC=" + getMNC() +
                ", AC=" + getAC() +
                ", CI=" + getCI() +
                ", Roads='" + getRoads() + '\'' +
                ", Lata=" + getLata() +
                ", Lnga=" + getLnga() +
                ", Latb=" + getLatb() +
                ", Lngb=" + getLngb() +
                ", Latg=" + getLatg() +
                ", Lngg=" + getLngg() +
                ", RID=" + getRID() +
                ", RIDS=" + getRIDS() +
                '}';
    }
}

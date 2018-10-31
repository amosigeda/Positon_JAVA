package com.hh.api.pos;

import java.math.BigDecimal;

import com.platform.annotation.Table;

/**
 * Created by Steven Hua on 2017/5/19.
 */

@Table(tableName = "bs_cdma_460", dataSourceName = "pos", pkName = "ID")
public class PositionCDMA extends PositionBase<PositionCDMA> {
    public static final PositionCDMA dao = new PositionCDMA().dao();

    private Integer SID;
    private Integer NID;
    private Long BID;
    private String Roads;
    private BigDecimal Lata;
    private BigDecimal Lnga;
    private BigDecimal Latb;
    private BigDecimal Lngb;
    private BigDecimal Latg;
    private BigDecimal Lngg;
    private Integer RID;
    private Long RIDS;

    public static final String column_SID = "SID";
    public static final String column_NID = "NID";
    public static final String column_BID = "BID";
    public static final String column_Roads = "Roads";
    public static final String column_Lata = "Lata";
    public static final String column_Lnga = "Lnga";
    public static final String column_Latb = "Latb";
    public static final String column_Lngb = "Lngb";
    public static final String column_Latg = "Latg";
    public static final String column_Lngg = "Lngg";
    public static final String column_RID = "RID";
    public static final String column_RIDS = "RIDS";

    public PositionCDMA() {
        super();
    }

    public Integer getSID() {
        return get(column_SID);
    }

    public void setSID(Integer SID) {
        this.set(column_SID, SID);
    }

    public Integer getNID() {
        return get(column_NID);
    }

    public void setNID(Integer NID) {
        this.set(column_NID, NID);
    }

    public Long getBID() {
        return get(column_BID);
    }

    public void setBID(Long BID) {
        this.set(column_BID, BID);
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
        return super.toString() + " PositionCDMA{" +
                "SID=" + getSID() +
                ", NID=" + getNID() +
                ", BID=" + getBID() +
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

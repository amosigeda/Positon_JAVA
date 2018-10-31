package com.hh.api.pos;

import com.platform.annotation.Table;

/**
 * Created by Steven Hua on 2017/5/19.
 */

@Table(tableName = "bs", dataSourceName = "pos", pkName = "ID")
public class PositionForeign extends PositionBase<PositionForeign> {
    public static final PositionForeign dao = new PositionForeign().dao();

    private Integer MNC;
    private Integer AC;
    private Long CI;

    public static final String column_MNC = "MNC";
    public static final String column_AC = "AC";
    public static final String column_CI = "CI";

    public PositionForeign() {
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

    @Override
    public String toString() {
        return super.toString() + " PositionForeign{" +
                "MNC=" + getMNC() +
                ", AC=" + getAC() +
                ", CI=" + getCI() +
                '}';
    }
}

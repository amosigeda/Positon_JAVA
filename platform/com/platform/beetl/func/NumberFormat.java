package com.platform.beetl.func;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.constant.Constants;

/**
 * 过滤xml文档函数
 *
 * @author 董华健
 */
public class NumberFormat implements Function {
    private static final Logger log = LoggerFactory.getLogger(NumberFormat.class);
    private DecimalFormat lastFormator;
    private String lastFmt;

    /**
     * 过滤xml文档函数实现
     */
    @Override
    public Object call(Object[] arg, Context context) {
        String defaultVal = Constants.DEFAULT_BLANK;
        if (arg.length >= 3) {
            defaultVal = arg[2].toString();
        }
        if (arg.length == 0 || arg[0] == null) {
            return defaultVal;
        }
        if (arg.length < 2) {
            return arg[0].toString();
        }
        Double val = null;
        Object obj = arg[0];
        if (obj instanceof BigDecimal) {
            val = ((BigDecimal) obj).doubleValue();
        } else {
            try {
                val = Double.parseDouble(obj.toString());
            } catch (Exception e) {
                log.error("Parse obj error", e);
            }
        }
        if (val != null) {
            String ft = arg[1].toString();
            DecimalFormat fmt;
            if (ft.equals(lastFmt) || lastFormator != null) {
                fmt = lastFormator;
            } else {
                fmt = new DecimalFormat(ft);
                lastFmt = ft;
                lastFormator = fmt;
            }

            return fmt.format(val);
        }
        return defaultVal;
    }

}

package com.hh.api.pos;

import com.hh.api.tools.CommUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hh.api.common.APIBaseController;
import com.hh.api.vo.RespData;
import com.platform.annotation.Controller;

/**
 * Created by Administrator on 2016/11/29.
 */
@Controller("/api/position")
public class PositionController extends APIBaseController {
    public static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    private PositionService positionService;

    public void query() {
        String lbsStr = getPara("lbs");
        String deviceId = getPara("deviceId");
        String wifi = getPara("wifi");
        if (CommUtil.isBlank(lbsStr)) {
            renderJson(new RespData(-1, "lbs参数不能为空！"));
            return;
        }
        if (lbsStr.length() < 6) {
            renderJson(new RespData(-1, "lbs参数至少要有一组数据！"));
            return;
        }

        Position pos = positionService.query(lbsStr,deviceId,wifi);
        if (pos != null) {
            RespData resp = new RespData(1, "成功！");
            resp.setBody(pos);
            renderJson(resp);
        } else {
            RespData resp = new RespData(-2, "失败");
            renderJson(resp);
            logger.error("定位失败：{}", lbsStr);
        }
    }



    /**
     * type： WGS84
     */
    public void regeo() {
        String latStr = getPara("lat");
        String lngStr = getPara("lng");
        String type = getPara("type");
        double lat = CommUtil.toDouble(latStr, 0d);
        double lng = CommUtil.toDouble(lngStr, 0d);
        if (lat == 0 || lng == 0) {
            renderJson(new RespData(-1, "坐标参数不能为空！"));
            return;
        }
        Address address = positionService.regeo(lat, lng, type);
        if (address != null) {
            RespData resp = new RespData(1, "成功");
            resp.setBody(address);
            renderJson(resp);
        } else {
            RespData resp = new RespData(-2, "未获取到地址");
            renderJson(resp);
            logger.error("获取地址失败：{},{},{}", lat, lng, type);
        }
    }

    public void weather() {
        String province = getPara("province");
        String city = getPara("city");
        if (CommUtil.isBlank(province) || CommUtil.isBlank(city)) {
            renderJson(new RespData(-1, "province,city参数不能为空！"));
            return;
        }
        Weather wea = positionService.weather(province, city);
        if (wea != null) {
            RespData resp = new RespData(1, "成功");
            resp.setBody(wea);
            renderJson(resp);
        } else {
            RespData resp = new RespData(-2, "未获取到天气");
            renderJson(resp);
            logger.error("获取天气失败:{},{}", province, city);
        }
    }
}

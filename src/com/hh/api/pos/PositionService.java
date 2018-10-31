package com.hh.api.pos;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.hh.api.tools.CommUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hh.api.tools.CoordinateUtil;
import com.hh.api.tools.Triangulation;
import com.jfinal.kit.PropKit;
import com.platform.annotation.Service;
import com.platform.constant.ConstantInit;
import com.platform.constant.Constants;
import com.platform.mvc.base.BaseService;

@Service(name = PositionService.serviceName)
public class PositionService extends BaseService {
    private final static Logger logger = LoggerFactory.getLogger(PositionService.class);
    public static final String serviceName = "positionService";
    public static final String BLANK = "";
    private static Pattern areaPattern = null;
    private static Map<String, String> areaReplaceMap = null;

    public static Position  query(String lbsStr,String deviceId,String wifi) {
        logger.error("20181030修改:{}", lbsStr+","+deviceId+",wifi:"+wifi);
        if(!"1".equals(wifi)&&wifi!=null&&!"".equals(wifi)){
           String mmac="";
           String macs="";
            String[] wifis = StringUtils.split(wifi, '|');
            for(int i=0;i<wifis.length;i++){
                if(i==0){
                    mmac=wifis[0];
                }else{
                    if(i>1){
                        macs=macs+"|";
                    }
                    macs=macs+wifis[i];
                }
                }
            String url="http://apilocate.amap.com/position?key=93b4cf92ab27576506c6ea1edbe8bb54&output=json&accesstype=1&mmac="+mmac+"&macs="+macs;
            logger.info("wifiURL="+url);
            String res = HttpUtil.get(url, 40000);
            logger.info("第三方wifi定位结果：{}", res);
            JSONObject json = JSON.parseObject(res);
            if (json != null && json.getIntValue("status") == 1) {
                JSONObject jo = json.getJSONObject("result");
                String loc = jo.getString("location");
                if (CommUtil.isBlank(loc)) {
                    return null;
                }
                String[] its = StringUtils.split(loc, ',');
                if (its.length != 2) {
                    return null;
                }
                double[] LatLng=CoordinateUtil.GCJToWGS84(new BigDecimal(its[1]).doubleValue(), new BigDecimal(its[0]).doubleValue());
                Position pos = new Position(new BigDecimal(LatLng[0]).doubleValue(), new BigDecimal(LatLng[1]).doubleValue());
                pos.setRadius(jo.getIntValue("radius"));
                pos.setAddress(jo.getString("desc"));
                logger.debug("成功获取到wifi第三方定位，并保存到数据库中：{}", pos);
                return pos;
            }
        }

        if(deviceId.contains("992018")||deviceId.contains("8618")){
            //未找到GSM定位，开始查询CDMA定位:460,0,33852,31161,14|460,0,33852,31159,15|460,0,33852,37097,12|460,0,33852,57603,11|460,0,33852,31163,7
            String[] lbs = StringUtils.split(lbsStr, '|');
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<lbs.length;i++){
               if(i==0){
                   String[] bts = StringUtils.split(lbs[0], ',');
                  if(Integer.valueOf(bts[4])>0){
                      bts[4]=(Integer.valueOf(bts[4])*2-113)+"";
                  }
                  sb.append(bts[0]+","+bts[1]+","+bts[2]+","+bts[3]+","+bts[4]);
                  sb.append("&nearbts=");
               }else{
                  /* if(lbs.length>1){
                       sb.append("|");
                   }*/
                   String[] bts = StringUtils.split(lbs[i], ',');
                   if(Integer.valueOf(bts[4])>0){
                       bts[4]=(Integer.valueOf(bts[4])*2-113)+"";
                   }
                   sb.append(bts[0]+","+bts[1]+","+bts[2]+","+bts[3]+","+bts[4]);
               }
            }
            sb.append("&imei="+deviceId);
            String res = HttpUtil.get("http://apilocate.amap.com/position?key=93b4cf92ab27576506c6ea1edbe8bb54&accesstype=0&cdma=0&network=GSM&bts=" + sb.toString(), 40000);
            logger.info("第三方定位结果：{}", res);
            JSONObject json = JSON.parseObject(res);
            if (json != null && json.getIntValue("status") == 1) {
                JSONObject jo = json.getJSONObject("result");
                String loc = jo.getString("location");
                if (CommUtil.isBlank(loc)) {
                    return null;
                }
                String[] its = StringUtils.split(loc, ',');
                if (its.length != 2) {
                    return null;
                }
                double[] LatLng=CoordinateUtil.GCJToWGS84(new BigDecimal(its[1]).doubleValue(), new BigDecimal(its[0]).doubleValue());
                Position pos = new Position(new BigDecimal(LatLng[0]).doubleValue(), new BigDecimal(LatLng[1]).doubleValue());
                pos.setRadius(jo.getIntValue("radius"));
                pos.setAddress(jo.getString("desc"));
                logger.debug("成功获取到第三方定位，并保存到数据库中：{}", pos);
                return pos;
            }

        }
        List<String> items = new ArrayList<>();
        List<Integer> signals = new ArrayList<>();
        String mcc = BLANK, mnc = BLANK, lac = BLANK, ci = BLANK;
        String[] lbs = StringUtils.split(lbsStr, '|');
        if (logger.isDebugEnabled()) {
            logger.debug("基站定位：{}", lbsStr);
        }
        StringBuilder sb = new StringBuilder();
        for (String bs : lbs) {
            String[] s = StringUtils.split(bs, ',');
            if (s.length == 5) {
                mcc = s[0];
                if ("0".equals(mcc)) {
                    mcc = "460";
                }
                mnc = s[1];
                lac = s[2];
                ci = s[3];
                signals.add(CommUtil.toInteger(s[4]));
            } else if (s.length == 3) {
                lac = s[0];
                ci = s[1];
                signals.add(CommUtil.toInteger(s[2]));
            } else {
                break;
            }
            sb.setLength(0);
            sb.append(StringUtils.leftPad(mcc, 3, '0'));
            sb.append(StringUtils.leftPad(mnc, 3, '0'));
            sb.append(StringUtils.leftPad(lac, 5, '0'));
            sb.append(StringUtils.leftPad(ci, 9, '0'));
            items.add(sb.toString());
        }
        if (items.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("查询定位：{}", StringUtils.join(items.toArray(), ','));
        }
        List<PositionBase> posList = new ArrayList<>();
        LinkedList<Object> paramList = new LinkedList<>();
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            param.put("it" + i, items.get(i));
        }
        param.put("items", items);
        List<PositionGSM> posGSMList = PositionGSM.dao.find(getSqlByBeetl("pos.loc.findAllPos", param, paramList), paramList.toArray());
        if (posGSMList != null && !posGSMList.isEmpty()) {
            posList.addAll(posGSMList);
        }
        if (posList.isEmpty()) {
            logger.info("未找到GSM定位，开始查询CDMA定位:{}", lbsStr);
            //未找到GSM定位，开始查询CDMA定位:460,0,33852,31161,14|460,0,33852,31159,15|460,0,33852,37097,12|460,0,33852,57603,11|460,0,33852,31163,7
            paramList.clear();
            List<PositionCDMA> posCDMAList = PositionCDMA.dao.find(getSqlByBeetl("pos.loc.findAllPosCDMA", param, paramList), paramList.toArray());
            if (posCDMAList != null && !posCDMAList.isEmpty()) {
                posList.addAll(posCDMAList);
            }
        }
        if (posList.isEmpty()) {
            logger.info("未找到CDMA定位，开始查询Foreign定位:{}", lbsStr);
            paramList.clear();
            List<PositionForeign> posForeignList = PositionForeign.dao.find(getSqlByBeetl("pos.loc.findAllPosForeign", param, paramList), paramList.toArray());
            if (posForeignList != null && !posForeignList.isEmpty()) {
                posList.addAll(posForeignList);
            }
        }
        logger.info("查询到的定位：{}", posList);
        if (!posList.isEmpty()) {
            int max = CommUtil.min(3, posList.size());
            Triangulation tri = new Triangulation(max);
            for (int i = 0; i < max; i++) {
                PositionBase p = posList.get(i);
                tri.add(new Point2D.Double(p.getLng().doubleValue(), p.getLat().doubleValue()), -Math.abs(signals.get(i)));
            }
            Point2D pt = tri.location();
            Position pos = new Position(pt.getY(), pt.getX());
            PositionBase near = posList.get(0);
            pos.setRadius(near.getRadius());
            pos.setAddress(near.getAddress());
            if (logger.isDebugEnabled()) {
                logger.debug("定位成功：{}", pos);
            }
            return pos;
        }
        logger.error("定位失败，进行第三方查询:{}", lbsStr);
        String[] lb = StringUtils.split(lbs[0], ',');
        if (lb.length >= 4) {
            int idx = lbsStr.indexOf('|');
            String nearbts = Constants.DEFAULT_BLANK;
            if (idx != -1 && idx != lbsStr.length() - 1) {
                String leftBts = lbsStr.substring(idx + 1);
                if (CommUtil.isNotBlank(leftBts)) {
                    nearbts = "&nearbts=" + leftBts;
                }
            }
            String res = HttpUtil.get("http://apilocate.amap.com/position?key=1f8dfa96bf05a00af718504a54fea774&accesstype=0&cdma=0&network=GSM&bts=" + lbs[0] + nearbts, 40000);
            logger.info("第三方定位结果：{}", res);
            JSONObject json = JSON.parseObject(res);
            if (json != null && json.getIntValue("status") == 1) {
                int spType = CommUtil.toInteger(lb[1]);
                PositionBase posi;
                if (spType == 0 || spType == 1) {
                    posi = new PositionGSM();
                } else if (spType == 11) {
                    posi = new PositionCDMA();
                } else {
                    return null;
                }
                StringBuilder sbID = new StringBuilder();
                sbID.append(StringUtils.leftPad(lb[0], 3, '0'));
                sbID.append(StringUtils.leftPad(lb[1], 3, '0'));
                sbID.append(StringUtils.leftPad(lb[2], 5, '0'));
                sbID.append(StringUtils.leftPad(lb[3], 9, '0'));
                posi.setID(sbID.toString());
                posi.setMCC(CommUtil.toInteger(lb[0]));
                if (spType == 11) {
                    ((PositionCDMA) posi).setSID(CommUtil.toInteger(lb[1]));
                    ((PositionCDMA) posi).setNID(CommUtil.toInteger(lb[2]));
                    ((PositionCDMA) posi).setBID(CommUtil.toLong(lb[3]));
                } else {
                    ((PositionGSM) posi).setMNC(CommUtil.toInteger(lb[1]));
                    ((PositionGSM) posi).setAC(CommUtil.toInteger(lb[2]));
                    ((PositionGSM) posi).setCI(CommUtil.toLong(lb[3]));
                }
                JSONObject jo = json.getJSONObject("result");
                String loc = jo.getString("location");
                if (CommUtil.isBlank(loc)) {
                    return null;
                }
                String[] its = StringUtils.split(loc, ',');
                if (its.length != 2) {
                    return null;
                }
                double[] LatLng=CoordinateUtil.GCJToWGS84(new BigDecimal(its[1]).doubleValue(), new BigDecimal(its[0]).doubleValue());
                posi.setLat(new BigDecimal(LatLng[0]));
                posi.setLng(new BigDecimal(LatLng[1]));
                posi.setRadius(jo.getIntValue("radius"));
                posi.setAddress(jo.getString("desc"));
                String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                posi.setVer(CommUtil.toInteger(date));
                try {
                    posi.save(posi.getID());
                } catch (Exception e) {
                    logger.debug("已经有定位数据，忽略:" + posi.getID());
                }

                Position pos = new Position(posi.getLat().doubleValue(), posi.getLng().doubleValue());
                pos.setRadius(posi.getRadius());
                pos.setAddress(posi.getAddress());
                logger.debug("成功获取到第三方定位，并保存到数据库中：{}", pos);
                return pos;
            }
        }
        logger.error("定位失败：{}", lbsStr);
        return null;
    }

    public Address regeo(double lat, double lng, String type) {
        if (StringUtils.equalsIgnoreCase("WGS84", type)) {
            double[] coord = CoordinateUtil.WGS84ToGCJ(lat, lng);
            lat = coord[0];
            lng = coord[1];
        }
        String uri = PropKit.get(ConstantInit.CONFIG_AMAP_LOC_URI);
        uri += "&location=" + lng + "," + lat;
        String res = HttpUtil.get(uri);
        JSONObject json = JSON.parseObject(res);
        if (json == null || json.getIntValue("status") != 1) {
            logger.error("调用逆地理编码接口失败, 坐标：{},{} 内容：{}", lat, lng, res);
            return null;
        }
        Address address = new Address();
        JSONObject regeoCode = json.getJSONObject("regeocode");
        address.getNearby().add(cleanContent(regeoCode.getString("formatted_address")));
        JSONObject area = regeoCode.getJSONObject("addressComponent");
        address.setProvince(cleanContent(area.getString("province")));
        address.setCity(cleanContent(area.getString("city")));
        address.setDistrict(cleanContent(area.getString("district")));
        JSONObject street = area.getJSONObject("streetNumber");
        address.setRoad(cleanContent(street.getString("street") + street.getString("number")));
        logger.debug("获取到地址解析：{} {},{}", lat, lng, address);
        return address;
    }

    public Weather weather(String province, String city) {
        String prov = preProcess(province);
        String cty = preProcess(city);
        Map<String, Object> param = new HashMap<>();
        param.put("province", province);
        param.put("prov", prov);
        param.put("city", city);
        param.put("ct", cty);
        logger.debug("省市名称,原始：{},{},处理后：{},{}", province, city, prov, cty);
        LinkedList<Object> paramList = new LinkedList<>();
        List<CountryAreaCode> list = CountryAreaCode.dao.find(getSqlByBeetl("pos.area.find", param, paramList), paramList.toArray());
        if (list == null || list.isEmpty()) {
            logger.error("查询天气失败,城市错误：{}-{},{}-{}", province, city, prov, cty);
            return null;
        }
        CountryAreaCode cac = list.get(0);
//        return getWeatherFromGaode(province, city, prov, cty, cac);
//    }
//
//    private Weather getWeatherFromGaode(String province, String city, String prov, String cty, CountryAreaCode cac) {
//        return null;
//    }
//
//    private Weather getWeatherFromWeatherCN(String province, String city, String prov, String cty, CountryAreaCode cac) {
        String queryUri = PropKit.get("config.weather.query.uri");
        String referer = PropKit.get("config.weather.referer.uri");
        HttpRequest req = HttpUtil.createGet(String.format(queryUri, cac.getCode()));
        req.header("referer", referer);
        req.header("user-agent", Constants.USER_AGENT);
        String res = req.execute().body();
        if (CommUtil.isBlank(res)) {
            logger.error("查询天气失败：{}-{},{}-{}", province, city, prov, cty);
            return null;
        }
        JSONObject json = JSON.parseObject(res.replaceAll("^var dataSK = ", ""));
        if (json != null) {
            Weather wea = new Weather();
            wea.setProvince(province);
            wea.setCity(city);
            wea.setCityCode(CommUtil.toStr(cac.getCode()));
            wea.setCityName(cac.getCn());
            wea.setWeather(json.getString("weather"));
            wea.setTemp(json.getIntValue("temp"));
            wea.setWind(json.getString("WD"));
            wea.setWindGrade(json.getString("WS"));
            wea.setHumidity(json.getString("SD"));
            wea.setPm25(json.getString("aqi_pm25"));
            logger.debug("获取到天气：{}", wea);
            return wea;
        }
        logger.error("查询天气失败：{}-{},{}-{} 获取内容：{}", province, city, prov, cty, res);
        return null;
    }

    private String cleanContent(String cont) {
        return cont == null ? cont : cont.replaceAll("[\\{\\}\\[\\] ,\"']+", "");
    }

    private String preProcess(String name) {
        if (CommUtil.isBlank(name)) {
            return name;
        }
        if (areaPattern == null) {
            areaPattern = Pattern.compile(PropKit.get(ConstantInit.CONFIG_AREA_NAME_REPLACE_REGEX));
        }
        if (areaReplaceMap == null) {
            areaReplaceMap = new HashMap<>();
            String repStr = PropKit.get(ConstantInit.CONFIG_AREA_NAME_REPLACE_ITEMS);
            if (CommUtil.isNotBlank(repStr)) {
                String[] items = StringUtils.split(repStr, ',');
                for (String item : items) {
                    String[] its = StringUtils.split(item, ':');
                    if (its.length == 2) {
                        areaReplaceMap.put(its[0], its[1]);
                    }
                }
            }
        }
        if (areaReplaceMap != null) {
            for (Map.Entry<String, String> entry : areaReplaceMap.entrySet()) {
                if (name.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        String tp = areaPattern.matcher(name).replaceAll(Constants.DEFAULT_BLANK);
        if (tp.length() == 1 && name.length() > 1) {
            return name.substring(0, 2);
        }
        return tp;
    }
    /*
    * 2018-09-13 11:17:31.844 [http-apr-8080-exec-19] INFO  com.hh.api.pos.PositionService - 未找到GSM定位，开始查询CDMA定位:460,0,33852,31161,14|460,0,33852,31159,15|460,0,33852,37097,12|460,0,33852,57603,11|460,0,33852,31163,7
2018-09-13 11:17:31.870 [http-apr-8080-exec-19] INFO  com.hh.api.pos.PositionService - 未找到CDMA定位，开始查询Foreign定位:460,0,33852,31161,14|460,0,33852,31159,15|460,0,33852,37097,12|460,0,33852,57603,11|460,0,33852,31163,7
2018-09-13 11:17:31.874 [http-apr-8080-exec-19] INFO  com.hh.api.pos.PositionService - 查询到的定位：[]
2018-09-13 11:17:31.893 [http-apr-8080-exec-19] INFO  com.hh.api.pos.PositionService - 第三方定位结果：{"infocode":"10000","result":{"type":"0"},"info":"OK","status":"1"}
    * */



   /* public static void main(String[] args) {
        Position pos = query("460,0,10173,4931,51|460,0,10173,4572,40|460,0,10173,4934,36|460,0,9363,5463,35|460,0,10173,4263,34|460,0,10173,4932,34,0","861805000073460");
        System.out.println(pos.getLat());
        System.out.println(pos.getLng());
        System.out.println(pos.getRadius());
        System.out.println(pos.getAddress());

        double[] WGS84ToGCJ = CoordinateUtil.WGS84ToGCJ(pos.getLat(),pos.getLng());
        System.out.println(WGS84ToGCJ[0]);
        System.out.println(WGS84ToGCJ[1]);
    }*/

}

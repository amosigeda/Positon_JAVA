package com.hh.api.vo;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;


public class RespData implements Serializable {
    @JSONField(ordinal = 1)
    private int code;
    @JSONField(ordinal = 2)
    private String msg;
    @JSONField(ordinal = 3)
    private Object body;
    @JSONField(ordinal = 4)
    private JSONArray bodyArray;

    public RespData() {
    }

    public RespData(int code) {
        this.code = code;
    }

    public RespData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespData(String jsonStr) {
        JSONObject json = JSON.parseObject(jsonStr);
        init(json);
    }

    public RespData(JSONObject json) {
        init(json);
    }

    private void init(JSONObject json) {
        code = json.getIntValue("code");
        msg = json.getString("msg");
        JSONArray ary = json.getJSONArray("body");
        if (ary == null) {
            body = json.getJSONObject("body");
        } else {
            bodyArray = ary;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public JSONArray getBodyArray() {
        return bodyArray;
    }

    public void setBodyArray(JSONArray bodyArray) {
        this.bodyArray = bodyArray;
    }
}

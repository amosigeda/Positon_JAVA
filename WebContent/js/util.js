/**
 * 工具类
 * Created by HH on 2017/1/8.
 */

var comm = {};
comm.alert = function (msg) {
    if (layer) {
        layer.alert(msg, {skin: 'layui-layer-lan', closeBtn: 0, shift: 4, icon: 1});
    } else {
        alert(msg);
    }
};
comm.delay = function (timeMill, func) {
    setTimeout(func, timeMill);
};

/**
 * 对Date的扩展，将 Date 转化为指定格式的String
 * 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q)可以用 1-2 个占位符
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * eg:
 * (new Date()).format("yyyy-MM-dd hh:mm:ss.S")==> 2006-07-02 08:09:04.423
 * (new Date()).format("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04
 * (new Date()).format("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04
 * (new Date()).format("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04
 * (new Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 */
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    var week = {
        "0": "/u65e5",
        "1": "/u4e00",
        "2": "/u4e8c",
        "3": "/u4e09",
        "4": "/u56db",
        "5": "/u4e94",
        "6": "/u516d"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "/u661f/u671f" : "/u5468") : "") + week[this.getDay() + ""]);
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

/**
 * 精确计算加法。语法：Math.add(v1, v2)
 * @param v1 type="number"操作数。
 * @param v2 type="number"操作数。
 * @returns {number} 计算结果
 */
Math.add = function (v1, v2) {
    var r1, r2, m;
    try {
        r1 = v1.toString().split(".")[1].length;
    }
    catch (e) {
        r1 = 0;
    }
    try {
        r2 = v2.toString().split(".")[1].length;
    }
    catch (e) {
        r2 = 0;
    }
    m = Math.pow(10, Math.max(r1, r2));

    return (v1 * m + v2 * m) / m;
};


/**
 * 精确计算加法。语法：number1.add(v)
 * @param v type="number" 操作数
 * @returns {number} 计算结果
 */
Number.prototype.add = function (v) {
    return Math.add(v, this);
};


/**
 * 精确计算减法。语法：Math.sub(v1, v2)
 * @param v1 type="number" 操作数
 * @param v2 type="number" 操作数
 * @returns {number} 计算结果
 */
Math.sub = function (v1, v2) {
    return Math.add(v1, -v2);
};

/**
 * 精确计算减法。语法：number1.sub(v)
 * @param v type="number" 操作数
 * @returns {number} 计算结果
 */
Number.prototype.sub = function (v) {
    return Math.sub(this, v);
};

/**
 * 精确计算乘法。语法：Math.mul(v1, v2)
 * @param v1 type="number" 操作数
 * @param v2 type="number" 操作数
 * @returns {number} 计算结果
 */
Math.mul = function (v1, v2) {
    var m = 0;
    var s1 = v1.toString();
    var s2 = v2.toString();
    try {
        m += s1.split(".")[1].length;
    }
    catch (e) {
    }
    try {
        m += s2.split(".")[1].length;
    }
    catch (e) {
    }

    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
};

/**
 * 精确计算乘法。语法：number1.mul(v)
 * @param v type="number" 操作数
 * @returns {number} 计算结果
 */
Number.prototype.mul = function (v) {
    return Math.mul(v, this);
};

/**
 * 精确计算除法。语法：Math.div(v1, v2)
 * @param v1 type="number" 操作数
 * @param v2 type="number" 操作数
 * @returns {number} 计算结果
 */
Math.div = function (v1, v2) {
    var t1 = 0;
    var t2 = 0;
    var r1, r2;
    try {
        t1 = v1.toString().split(".")[1].length;
    }
    catch (e) {
    }
    try {
        t2 = v2.toString().split(".")[1].length;
    }
    catch (e) {
    }

    with (Math) {
        r1 = Number(v1.toString().replace(".", ""));
        r2 = Number(v2.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
};

/**
 * 精确计算除法。语法：number1.div(v)
 * @param v type="number" 操作数
 * @returns {number} 计算结果
 */
Number.prototype.div = function (v) {
    return Math.div(this, v);
};
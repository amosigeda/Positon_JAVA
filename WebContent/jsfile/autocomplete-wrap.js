var AutoComplete = {
    init: function (selectors, callback) {
        var fillFields=function ($field,suggestion,defaultVal) {
            var fields = $.parseJSON($field.attr("autoFill"));
            var fillMode = $field.attr("fillMode");

            for (var fid in fields) {
                var selector;
                if (fid.charAt(0) == "#" || fid.charAt(0) == ".") {
                    selector = fid;
                } else {
                    selector = "[name=" + fid.replace(/\./ig, "\\\.") + "]";
                }
                var tgt;
                var val = suggestion ? suggestion.data[fields[fid]] : defaultVal;
                if (fillMode && fillMode == "tr") {
                    tgt = $field.parents("tr").find(selector);
                } else {
                    tgt = $(selector);
                }
                if (tgt && tgt.length > 0) {
                    tgt.each(function () {
                        var $this = $(this);
                        if ($this.get(0) == $field.get(0) && !val) {
                            return true;
                        }
                        switch ($this.get(0).tagName) {
                            case "TEXTAREA":
                            case "DIV":
                            case "SPAN":
                            case "LABEL":
                            case "EM":
                                $this.html(val);
                                break;
                            default:
                                $this.val(val);
                        }
                    });
                }
            }
        };
        $(selectors).each(function () {
            var $field = $(this);
            $field.autocomplete({
                autoSelectFirst: true,
                serviceUrl: cxt + '/com/comm',
                paramName: "q",
                onSearchStart: function (params) {
                    var fields = $.parseJSON($field.attr("autoFill"));
                    var s = "";
                    for (var ss in fields) {
                        s += fields[ss] + ",";
                    }
                    if (s.length > 0) {
                        s = s.substring(0, s.length - 1);
                    }
                    params["cate"] = $field.attr("cate");
                    params["cols"] = s;
                    params["level"] = $field.attr("level");
                    params["category"] = $field.attr("category");
                    return true
                },
                transformResult: function (data, originalQuery) {
                    var res = {};
                    res.suggestions = [];
                    if (!data) {
                        fillFields($field,null,"");
                        return res;
                    }
                    data = $.parseJSON(data);
                    if (data.length == 0) {
                        fillFields($field,null,"");
                        return res;
                    }
                    var disCols = $field.attr("disCols");
                    var dCols = disCols ? disCols.split(",") : null;
                    for (var i = 0; i < data.length; i++) {
                        var item = data[i];
                        var s = "";
                        for (var it in item) {
                            if (ArrayContains(dCols, it)) {
                                s += item[it] + "-";
                            }
                        }
                        if (s.length > 0) {
                            s = s.substring(0, s.length - 1);
                        }
                        res.suggestions.push({value: s, data: item});
                    }
                    return res;
                },
                onSearchComplete: function () {
                },
                onSelect: function (suggestion) {
                    fillFields($field,suggestion,null);
                    if (typeof callback == "function") {
                        callback($field,suggestion);
                    }
                }
            });
        });
    }
};

function ArrayContains(ary, tgt) {
    if(!ary){
        return true;
    }
    if (!ary || !tgt) {
        return false;
    }
    for (var i = 0; i < ary.length; i++) {
        if (ary[i] == tgt) {
            return true;
        }
    }
    return false;
}
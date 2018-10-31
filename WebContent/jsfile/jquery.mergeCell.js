/**
 * Created by Administrator on 2016/9/26.
 */
(function ($) {
    $.fn.extend({
        //表格合并单元格，colIdx要合并的列序号，从0开始
        "mergeCell": function (colIdxs) {
            var _this=this;
            $.each(colIdxs,function (idx) {
                _this.each(function () {
                    var that;
                    $('tr', _this).each(function (row) {
                        $('td:eq(' + colIdxs[idx] + ')', this).filter(':visible').each(function (col) {
                            if (that != null && $(this).attr("sign") == $(that).attr("sign")) {
                                var rowspan = $(that).attr("rowSpan");
                                if (rowspan == undefined) {
                                    $(that).attr("rowSpan", 1);
                                    rowspan = $(that).attr("rowSpan");
                                }
                                rowspan = Number(rowspan) + 1;
                                $(that).attr("rowSpan", rowspan);
                                $(this).hide();
                            } else {
                                that = this;
                            }
                        });
                    });
                });
            })
        }
    });
})(jQuery);
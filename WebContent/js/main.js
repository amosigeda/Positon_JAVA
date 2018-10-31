jQuery(function () {
    bindTableHover();
});

function bindTableHover() {
    $("body").on("mouseenter mouseleave", ".table.table-hover tbody>tr", function (event) {
        var $this = $(this);
        var $td = $this.find("td:first");
        var rows = 0;
        if (!$td.is("[rowspan]") && $td.is(":hidden")) {
            var tempTrs = $this.prevAll("tr");
            for (var i = 0; i < tempTrs.length; i++) {
                var tmpTd = tempTrs.eq(i).find("td:first:visible");
                if (tmpTd.length > 0) {
                    $this = tempTrs.eq(i);
                    rows = tmpTd.attr("rowspan");
                    break;
                }
            }
        } else {
            rows = $td.attr("rowspan");
        }
        if (rows) {
            rows = rows - 1;
        } else {
            return;
        }
        var trs = $this.nextAll('tr:lt(' + rows + ')').add($this);
        if (event.type == "mouseenter") {
            trs.addClass("tableHover");
        } else {
            trs.removeClass("tableHover");
        }
    });
}
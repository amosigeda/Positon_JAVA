;
var UploadFileUtil = (function () {
    var dropBox, progress, url, finished, dragOver, fileExt;
    var init = function (params) {
        dropBox = params.dropBox;
        progress = params.progress;
        url = params.url;
        finished = params.finished;
        dragOver = params.dragOver;
        fileExt = params.fileExt;

        $(dropBox).on("dragover dragleave", function (event) {
            event.preventDefault();
            dragOver(event);
        });
        $(dropBox).on("drop", function (event) {
            event.preventDefault();
            dropFile(event);
        });
    };

    function dropFile(e) {
        var objXhr = new XMLHttpRequest();
        var files = e.originalEvent.dataTransfer.files;
        var objForm = new FormData();

        objXhr.upload.onprogress = function (e) {
            if (e.lengthComputable) {
                var intComplete = (e.loaded / e.total) * 100 | 0;
                if (typeof progress == "function") {
                    progress(intComplete);
                }
            }
        };

        objXhr.onload = function (e) {
            var arrData = JSON.parse(objXhr.responseText);
            if (typeof finished == "function") {
                finished(arrData);
            }
        };

        objXhr.open('POST', url);
        for (var i = 0; i < files.length; i++) {
            var fileName = files[i].name;
            if (!isValidExt(fileName)) {
                alert("文件 " + fileName + " 格式不合法！");
                continue;
            }
            objForm.append('files[]', files[i]);
        }

        objXhr.send(objForm);
    }

    function isValidExt(fileName) {
        if (!fileName) {
            return true;
        }
        var exts = fileExt.split(",");
        var ext = fileName.substring(fileName.lastIndexOf(".") + 1)
        return $.inArray(ext, exts) >= 0;
    };

    return {"init": init};
})();
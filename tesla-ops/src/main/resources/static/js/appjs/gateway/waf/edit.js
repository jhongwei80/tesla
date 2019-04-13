$(function () {
    var apiPrefix = "/api/gateway/waf";

    var e = "<i class='fa fa-times-circle'></i> ";
    var wafName = $("#wafName").val();
    $("#wafForm").validate({
        rules: {
            "wafName": "required",
            "wafDesc": "required",
        },
        messages: {
            "wafName": e + "请输入" + wafName + " 名称",
            "wafDesc": e + "请输入" + wafName + " 描述"
        },
        submitHandler: function (form) {
            swal({
                title: "修改记录？",
                text: "是否确定修改" + wafName + "信息？",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "保存",
                closeOnConfirm: false,
                showLoaderOnConfirm: true
            }, function () {
                var formData = new FormData(document.getElementById("wafForm"));
                var pluginParam = getConfigByPlugin($("#wafType").val()).getFormatResult($("#wafContent"));
                if (pluginParam !== null) {
                    formData.append("pluginParam", pluginParam);
                }
                formData.append("_method", "PUT");
                $.ajax({
                    cache: true,
                    type: "post",
                    url: apiPrefix,
                    data: formData,
                    processData: false,
                    contentType: false,
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "修改" + wafName + "成功！",
                                    text: "修改" + wafName + "成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("修改" + wafName + "失败！", data.msg, "error");
                        }

                    },
                    error: function (err) {
                        swal("新增" + wafName + "失败！", err, "error");
                    }
                });
            });
        },
        errorPlacement: function (error, element) {
            error.insertAfter(element.parent());
        }

    });

});

function getConfigByPlugin(pluginType) {
    switch (pluginType) {
        case 'AppKeyControlRequestPlugin':
            return wafAppKeyControlConfig;
            break;
        case 'BlackCookieRequestPlugin':
        case 'BlackIpRequestPlugin' :
        case 'BlackUaRequestPlugin' :
        case 'BlackURLParamRequestPlugin' :
        case 'BlackURLRequestPlugin':
            return wafTextAreaContentConfig;
            break;
        default:
            return wafNoContentConfig;
    }
}

function initTextAreaByContent(endpointDiv, content, mode) {
    var textareas = endpointDiv.find("textarea");
    textareas.each(function () {
        var textarea = $(this);
        textarea.val(content);
        var myCodeMirror = CodeMirror.fromTextArea(textarea[0], {
            lineWrapping: true,
            lineNumbers: true,
            autoRefresh: true,
            mode: mode
        });
        myCodeMirror.on("blur", function () {
            textarea.val(myCodeMirror.getValue());
        })
        textarea.change(function () {
            myCodeMirror.setValue(textarea.val());
        })
    });

}
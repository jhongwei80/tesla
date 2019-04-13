
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
            "wafName": e + "请输入"+wafName+" 名称",
            "wafDesc": e + "请输入"+wafName+" 描述"
        },
        submitHandler: function (form) {
            swal({
                title: "修改记录？",
                text: "是否确定修改"+wafName+"信息？",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "保存",
                closeOnConfirm: false,
                showLoaderOnConfirm: true
            }, function () {
                $.ajax({
                    cache: true,
                    type: "post",
                    url: apiPrefix,
                    data: $('#wafForm').serialize()+"&_method=PUT",
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "修改"+wafName+"成功！",
                                    text: "修改"+wafName+"成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("修改"+wafName+"失败！", data.msg, "error");
                        }

                    },
                    error: function(err) {
                        swal("新增"+wafName+"失败！", err, "error");
                    }
                });
            });
        },
        errorPlacement: function(error, element) {
            error.insertAfter(element.parent());
        }

    });

});
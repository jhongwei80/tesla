$(function () {
    var apiPrefix = "/api/gateway/policy";

    var e = "<i class='fa fa-times-circle'></i> ";
    $("#policyForm").validate({
        rules: {
            "policyParamVo.rateLimit.rate": {
                required: {
                    depends: function (value, element) {
                        return $('#policyParamVo\\.rateLimit\\.enabled').val() == "Y";
                    }
                },
                number: {
                    depends: function (value, element) {
                        return $('#policyParamVo\\.rateLimit\\.enabled').val() == "Y";
                    }
                }
            },
            "policyParamVo.rateLimit.perSeconds": {
                required: {
                    depends: function (value, element) {
                        return $('#policyParamVo\\.rateLimit\\.enabled').val() == "Y";
                    }
                },
                number: {
                    depends: function (value, element) {
                        return $('#policyParamVo\\.rateLimit\\.enabled').val() == "Y";
                    }
                }
            },
            "policyParamVo.quota.interval": {
                required: {
                    depends: function (value, element) {
                        return Number($('#policyParamVo\\.quota\\.maxRequest').val()) > 0;
                    }
                },
                number: {
                    depends: function (value, element) {
                        return Number($('#policyParamVo\\.quota\\.maxRequest').val()) > 0;
                    }
                }
            },
            "policyParamVo.quota.maxRequest": {
                number: {
                    depends: function (value, element) {
                        return true;
                    }
                }
            },
            "policyName": "required",
            "policyDesc": "required"
        },
        messages: {
            "policyParamVo.rateLimit.rate": {required: e + "请输入Rate limit rate", number: e + "请输入正确的数字"},
            "policyName": e + "请输入policy名称",
            "policyDesc": e + "请输入policy描述",
            "policyParamVo.rateLimit.perSeconds": {required: e + "请输入Rate limit perSeconds", number: e + "请输入正确的数字"},
            "policyParamVo.quota.interval": {required: e + "请输入复位间隔时间", number: e + "请输入正确的数字"},
            "policyParamVo.quota.maxRequest" :{number: e + "请输入正确的数字"}
        },
        submitHandler: function (form) {
            swal({
                title: "新增记录？",
                text: "是否确定保存Policy信息？",
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
                    data: $('#policyForm').serialize(),
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "新增Policy成功！",
                                    text: "新增Policy成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("新增Policy失败！", data.msg, "error");
                        }

                    },
                    error: function (err) {
                        swal("新增Policy失败！", err, "error");
                    }
                });
            });
        },
        errorPlacement: function (error, element) {
            error.insertAfter(element.parent());
        }

    });

});
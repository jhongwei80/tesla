
$(function () {
    var apiPrefix = "/api/gateway/appkey";

    var e = "<i class='fa fa-times-circle'></i> ";
    $("#appkeyForm").validate({
        rules: {
            "rateLimit.rate": {
                required: {
                    depends: function (value, element) {
                        return $('#rateLimitEnabled').val() == "Y";
                    }
                },
                number: {
                    depends: function (value, element) {
                        return true;
                    }
                }
            },
            "rateLimit.perSeconds": {
                required: {
                    depends: function (value, element) {
                        return $('#rateLimitEnabled').val() == "Y";
                    }
                },
                number: {
                    depends: function (value, element) {
                        return true;
                    }
                }
            },
            "quota.interval": {
                required: {
                    depends: function (value, element) {
                        return Number($('#quotaMaxRequest').val()) > 0;
                    }
                },
                number: {
                    depends: function (value, element) {
                        return true;
                    }
                }
            },
            "quota.maxRequest": {
                number: {
                    depends: function (value, element) {
                        return true;
                    }
                }
            },
            "appName": "required",
            "appKey": "required",
            "appKeyDesc": "required"
        },
        messages: {
            "rateLimit.rate": {required: e + "请输入Rate limit rate", number: e + "请输入正确的数字"},
            "appName": e + "请输入Appkey名称",
            "appKey": e + "请输入Appkey",
            "appKeyDesc": e + "请输入Appkey描述",
            "rateLimit.perSeconds": {required: e + "请输入Rate limit perSeconds", number: e + "请输入正确的数字"},
            "quota.interval": {required: e + "请输入复位间隔时间", number: e + "请输入正确的数字"},
            "quota.maxRequest" :{number: e + "请输入正确的数字"}
        },
        submitHandler: function (form) {
            swal({
                title: "修改记录？",
                text: "是否确定修改接入系统信息？",
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
                    data: $('#appkeyForm').serialize()+"&_method=PUT",
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "修改Appkey成功！",
                                    text: "修改Appkey成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("修改Appkey成功失败！", "修改Appkey成功失败。", "error");
                        }

                    },
                    error: function(err) {
                        alert(err);
                    }
                });
            });
        },
        errorPlacement: function(error, element) {
            error.insertAfter(element.parent());
        }

    });
    var policysChange = function() {
        $('#policys').change(function(){
            var params = $("#policys").val();
            var paramsJson = JSON.parse(params);
            if(paramsJson && paramsJson.rateLimit && paramsJson.rateLimit.enabled){
                $("#rateLimitEnabled").selectpicker('val', paramsJson.rateLimit.enabled);
                $('#rateLimitRate').val(paramsJson.rateLimit.rate);
                $('#rateLimitPerSeconds').val(paramsJson.rateLimit.perSeconds);
            }else{
                $("#rateLimitEnabled").selectpicker('val', 'Y');
                $('#rateLimitRate').val('');
                $('#rateLimitPerSeconds').val('');
            }
            if(paramsJson && paramsJson.accessControls){
                $('#accessControlAccessServices').selectpicker('val',paramsJson.accessControls);
            }else{
                $('#accessControlAccessServices').selectpicker('deselectAll');
            }
            if(paramsJson && paramsJson.quota){
                $('#quotaInterval').val(paramsJson.quota.interval);
                $("#quotaTimeUtil").selectpicker('val', paramsJson.quota.timeUtil);
                $('#quotaMaxRequest').val(paramsJson.quota.maxRequest);
            }else{
                $("#quotaTimeUtil").selectpicker('val', '3');
                $('#quotaInterval').val('');
                $('#quotaMaxRequest').val('');
            }
        })
    };
    policysChange()
});
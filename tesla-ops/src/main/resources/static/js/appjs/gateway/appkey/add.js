$(function () {
    var apiAppkeyPrefix = "/api/gateway/appkey";
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
                title: "新增记录？",
                text: "是否确定保存接入系统信息？",
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
                    url: apiAppkeyPrefix,
                    data: $('#appkeyForm').serialize(),
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "新增Appkey成功！",
                                    text: "新增Appkey成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("新增Appkey失败！", data.msg, "error");
                        }

                    },
                    error: function(err) {
                        swal("新增Appkey失败！", err, "error");
                    }
                });
            });
        },
        errorPlacement: function(error, element) {
            error.insertAfter(element.parent());
        }

    });
    var btnfunction = function() {
        $('#btnAppKey').bind("click", function() {
            $.ajax({
                type: "GET",
                url: apiAppkeyPrefix+"/generateKey",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function(data) {
                    $('#appKey').val(data.msg);
                },
                error: function(err) {
                    alert(err);
                }
            });
        });
    };
    btnfunction();
    var policysChange = function() {
        $('#policys').change(function(){
            var params = $("#policys").val();
            var paramsJson = JSON.parse(params);
            if(paramsJson && paramsJson.rateLimit && paramsJson.rateLimit.enabled){
                //$("#rateLimitEnabled").find("option[value="+ paramsJson.rateLimit.enabled +"]").attr("selected",true);
                $("#rateLimitEnabled").selectpicker('val', paramsJson.rateLimit.enabled);
                $('#rateLimitRate').val(paramsJson.rateLimit.rate);
                $('#rateLimitPerSeconds').val(paramsJson.rateLimit.perSeconds);
            }else{
                //$("#rateLimitEnabled").find("option[value='Y']").attr("selected",true);
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
                //$("#quotaTimeUtil").find("option[value='"+paramsJson.quota.timeUtil+"']").attr("selected",true);
                $("#quotaTimeUtil").selectpicker('val', paramsJson.quota.timeUtil);
                $('#quotaMaxRequest').val(paramsJson.quota.maxRequest);
            }else{
                //$("#quotaTimeUtil").find("option[value='3']").attr("selected",true);
                $("#quotaTimeUtil").selectpicker('val', '3');
                $('#quotaInterval').val('');
                $('#quotaMaxRequest').val('');
            }
        })
    };
    policysChange()
});
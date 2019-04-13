var PREFIX = "/oauth2/client";
var RESTFUL_URL = "/api" + PREFIX;

$(function () {
    formValidator();
});


var formValidator = function () {
    var e = "<i class='fa fa-times-circle'></i> ";
    $.extend($.validator.defaults, {ignore: ""});
    $("#clientForm").validate({
        rules: {
            clientId: {
                required: true
            },
            clientName: {
                required: true
            },
            clientSecret: "required",
            clientUri: "required",
            clientIconUri: "required",
            resourceIds: "required",
            scope: "required",
            grantTypes: "required",
            redirectUri: "required",
            accessTokenValidity: {
                required: true,
                number: true
            },
            refreshTokenValidity: {
                required: true,
                number: true
            }
        },
        messages: {
            clientId: e + "请输入客户端Id",
            clientName: e + "请输入客户端名称",
            clientSecret: e + "请输入客户端秘钥",
            clientUri: e + "请输入客户端Uri",
            clientIconUri: e + "请输入客户端图标Uri",
            resourceIds: e + "请输入客户端资源Ids",
            scope: e + "请输入scope",
            grantTypes: e + "请输入客户端授权类型",
            accessTokenValidity: e + "请输入超时时间",
            refreshTokenValidity: e + "请输入超时时间",
            redirectUri: e + "请输入redirectUri"
        },
        errorPlacement: function (error, element) {
            if ($(element).is('select')) {
                element.next().after(error); // special placement for select elements
            } else {
                error.insertAfter(element);  // default placement for everything else
            }
        },
        submitHandler: function (form) {
            swal({
                title: "保存客户端？",
                text: "是否确定添加客户端？",
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
                    url: RESTFUL_URL,
                    data: $(form).serialize()+"&_method=PUT",
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal({
                                title: "保存客户端成功！",
                                text: "保存客户端成功！",
                                type: "success"
                            }, function (e) {
                                removeIframe(true);
                            });
                        } else {
                            swal("保存客户端失败！", "保存客户端失败。", "error");
                        }
                    }
                });
            });
        }
    });
};




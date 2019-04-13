var RESTFUL_URL = "/api/sys/menu";

$(function() {
    initSelect();
    formValidator();
});

var initSelect = function(){
    $("#name").selectpicker({
        liveSearch: true,
        noneSelectedText: '请选择菜单类型',
        noneResultsText: '没有找到菜单类型！'
    });
};

var formValidator = function() {
    var e = "<i class='fa fa-times-circle'></i> ";
    $.extend($.validator.defaults,{ignore:""});
    $("#menuForm").validate({
        rules: {
            type: "required",
            name: "required"
        },
        messages: {
            type: e + "请选择菜单类型",
            name: e + "请输入菜单名称",
            perms: e + "请输入权限标识"
        },
        submitHandler: function(form) {
            swal({
                title: "修改记录？",
                text: "是否确定修改系统菜单？",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "修改",
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
                                title: "修改系统菜单成功！",
                                text: "修改系统菜单成功！",
                                type: "success"
                            }, function (e) {
                                removeIframe(true);
                            });
                        } else {
                            swal("修改系统菜单失败！", "修改系统菜单失败。", "error");
                        }
                    }
                });
            });
        }
    });
};
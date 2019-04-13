var PREFIX = "/sys/user";
var RESTFUL_URL = "/api" + PREFIX;

$(function() {
    initSelect();
    formValidator();
});

var initSelect = function () {
    $("#status").selectpicker({
        liveSearch: false,
        noneSelectedText: '请选择用户状态',
        noneResultsText: '没有找到用户状态！'
    });
    $("#role").selectpicker({
        liveSearch: false,
        noneSelectedText: '请选择用户角色',
        noneResultsText: '没有找到用户角色！'
    });
};

var formValidator = function() {
    var e = "<i class='fa fa-times-circle'></i> ";
    $.extend($.validator.defaults,{ignore:""});
    $("#userForm").validate({
        rules: {
            name: "required",
            username: {
                required: true,
                minlength: 2,
                remote: {
                    url: RESTFUL_URL + "/exit",
                    type: "post",
                    dataType: "json",
                    data: {
                        userId: function() {
                            return $("#userId").val();
                        },username: function() {
                            return $("#username").val();
                        },
                        flag: "edit"
                    }
                }
            },
            password: {
                required: true,
                minlength: 6
            },
            confirmPassword: {
                required: true,
                minlength: 6,
                equalTo: "#password"
            },
            deptName: "required",
            status: "required",
            role: "required"
        },
        messages: {
            name: e + "请输入姓名",
            username: {
                required: e + "请输入用户名",
                minlength: e + "用户名必须两个字符以上",
                remote: e + "用户名已经存在"
            },
            password: {
                required: e + "请输入密码",
                minlength: e + "密码必须6个字符以上"
            },
            confirmPassword: {
                required: e + "请输入确认密码",
                minlength: e + "密码必须6个字符以上",
                equalTo: e + "两次输入密码不一致",
            },
            deptName: e + "请选择所属部门",
            status: e + "请选择用户状态",
            role: e + "请选择用户角色",
            email: e + "请输入有效E-mail",
            mobile: e + "请输入有效手机号"
        },
        submitHandler: function(form) {
            swal({
                title: "修改记录？",
                text: "是否确定修改用户？",
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
                                title: "修改用户成功！",
                                text: "修改用户成功！",
                                type: "success"
                            }, function (e) {
                                removeIframe(true);
                            });
                        } else {
                            swal("修改用户失败！", "修改用户失败。", "error");
                        }
                    }
                });
            });
        }
    });
};

var openDeptDialog = function() {
    layer.open({
        type: 2,
        title: "选择部门",
        area: ['700px', '450px'],
        content: "/sys/dept/treeView"
    });
};

var loadDept = function (deptId, deptName) {
    $("#deptId").val(deptId);
    $("#deptName").val(deptName);
};
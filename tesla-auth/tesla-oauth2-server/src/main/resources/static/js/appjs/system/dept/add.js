$(document).ready(function () {
    var prefix = "/api/sys/dept";
    var e = "<i class='fa fa-times-circle'></i> ";
    $("#deptForm").validate({
        rules: {
            name: {
                required: true
            },
            orderNum: {
                digits: true,
                required: true,
                min: 1
            }
        },
        messages: {
            name: {
                required: e + "请输入姓名"
            }, orderNum: {
                digits: e + "请输入整数",
                required: e + "请输入顺序",
                min: e + "请输入大于0的正整数"
            }
        },
        submitHandler: function (form) {
            swal({
                title: "保存部门信息？",
                text: "是否确定保存部门信息？",
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
                    url: prefix,
                    data: $('#deptForm').serialize(),
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "保存部门成功！",
                                    text: "保存部门成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("保存部门失败！", "保存部门失败。", "error");
                        }
                    }
                });
            });
        },
        errorPlacement: function (error, element) {
            if ($(element).is('select')) {
                element.next().after(error); // special placement for select elements
            } else {
                error.insertAfter(element);  // default placement for everything else
            }
        }
    });
});

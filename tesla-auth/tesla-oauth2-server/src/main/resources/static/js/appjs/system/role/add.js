$(document).ready(function () {
    var prefix = "/sys/role";
    var apiPrefix = "/api/sys/role";
    var e = "<i class='fa fa-times-circle'></i> ";
    $("#roleForm").validate({
        rules: {
            roleName: {
                required: true
            }
        },
        messages: {
            roleName: {
                required: e + "请输入角色名"
            }
        },
        submitHandler: function (form) {
            swal({
                title: "新增角色？",
                text: "是否确定新增角色信息？",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "保存",
                closeOnConfirm: false,
                showLoaderOnConfirm: true
            }, function () {
                var ref = $('#menuTree').jstree(true);
                var menuIds = ref.get_selected();
                $("#menuTree").find(".jstree-undetermined").each(function (i, element) {
                    menuIds.push($(element).closest('.jstree-node').attr("id"));
                });
                $('#menuIds').val(menuIds);
                $.ajax({
                    cache: true,
                    type: "post",
                    url: apiPrefix,
                    data: $('#roleForm').serialize(),
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "新增角色信息成功！",
                                    text: "新增角色信息成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("新增角色信息失败！", "新增角色信息失败。", "error");
                        }

                    }
                });
            });

        }
    });
    $.ajax({
        type: "GET",
        url: "/api/sys/menu/tree",
        success: function (menuTree) {
            $('#menuTree').jstree({
                'core': {
                    'data': menuTree
                },
                "checkbox": {
                    "three_state": true,
                },
                "plugins": ["wholerow", "checkbox"]
            });
        }
    });

});
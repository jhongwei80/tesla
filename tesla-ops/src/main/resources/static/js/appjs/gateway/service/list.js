var apiPrefix = "/api/gateway/service";
var pagePrefix = "/gateway/service";
$(function () {
    $("#serviceSlect").change(function () {
        reLoad();
    })
    load();
});

function load() {
    $('#serviceListTable').bootstrapTable({
        method: 'get',
        url: apiPrefix,
        striped: true,
        dataType: "json",
        pagination: true,
        pageSize: 10,
        pageList: [5, 10, 20],
        pageNumber: 1,
        sidePagination: "server",
        showRefresh: true,
        iconSize: "outline",
        icons: {refresh: "glyphicon-repeat"},
        toolbar: "#servicetoolbar",
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset,
                id:$("#serviceSlect").val()
            };
        },
        onLoadSuccess:function(data){
            $("#serviceSlect").val(data.params.id);
        },
        columns: [{
            field: 'serviceName',
            title: 'API名称'
        }, {
            field: 'servicePath',
            title: '调用路径'
        }, {
            field: 'serviceDesc',
            title: 'API描述',
            formatter: function (value, row, index) {
                //此处对value值做判断，不然value为空就会报错
                value = value ? value : '';
                var length = value.length;
                if (length && length > 20) {
                    length = 20;
                    return "<span title ='" + value + "'>" + value.substring(0, length) + "...</span>"
                }
                return "<span title ='" + value + "'>" + value + "</span>"
            }
        }, {
            field: 'serviceEnabled',
            title: '启用状态',
            align: 'center',
            formatter: function (item, index) {
                if (item == 'N') {
                    return '<span class="label label-danger">禁用</span>';
                } else if (item == 'Y') {
                    return '<span class="label label-primary">启用</span>';
                }
            }
        }, {
            field: 'approvalStatus',
            title: '审批状态',
            align: 'center',
            formatter: function (item, index) {
                if (item == 'N') {
                    return '<span class="label label-danger">未审批</span>';
                } else if (item == 'Y') {
                    return '<span class="label label-primary">已审批</span>';
                }
            }
        }, {
            field: 'serviceOwner',
            title: '负责人'
        }, {
            field: 'gmtCreate',
            title: '创建时间'
        }, {
            field: 'gmtModified',
            title: '更新时间'
        }, {
            title: '操作',
            field: 'id',
            width: '100px',
            align: 'center',
            formatter: function (value, row, index) {
                var e = '<a class="' + s_edit_h + '" href="javascript:void(0)"  title="编辑" onclick="edit(\'' + row.id + '\')">' +
                    '<i class="glyphicon glyphicon-edit"></i></a> ';
                var g = '<a class="' + s_leading_out_h + '" href="javascript:void(0)"  title="导出" onclick="leadingOut(\'' + row.id + '\')">' +
                    '<i class="glyphicon glyphicon-export"></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"   onclick="remove(\'' + row.id + '\')">' +
                    '<i class="glyphicon glyphicon-remove"></i></a> ';
                var x = '<a class="' + s_review_h + '" href="javascript:void(0)" title="审核"   onclick="review(\'' + row.id + '\',\'' + row.approvalStatus + '\')">' +
                    '<i class="glyphicon glyphicon-eye-open"></i></a> ';
                return e + g + d + x;
            }
        }]
    });

}


function review(id, approvalStatus) {
    if (approvalStatus == 'Y') {
        swal('API已审核！', 'API已审核，请勿重复审核！', 'warning');
        return;
    }
    swal({
        title: "确定要审核选中的API？",
        text: "确定要审核选中的API，请仔细检查配置是否有误？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "审核",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        $.ajax({
            url: apiPrefix + "/" + id,
            type: "put",
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "审核API成功！",
                            text: "审核API成功，API已审核成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("审核API失败！", "审核API失败！", "error");
                }
            }

        });
    })
}

function reLoad() {
    $('#serviceListTable').bootstrapTable('refresh');
}


function add() {
    var url = pagePrefix + '/add';
    var title = '新增API配置';
    page(url, title);

}

function edit(id) {
    var url = pagePrefix + '/edit/' + id;
    var title = '修改API配置';
    page(url, title);
}

function leadingOut(id) {
    var url = pagePrefix + '/export/' + id;
    swal({
        title: "确定要导出选中的记录？",
        text: "含有文件的插件无法导出，导入后请重新编辑上传文件",
        type: "info",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "导出"
    }, function () {
        document.location.href = url;
    });
}

function leadingIn() {
    var data = {};
    var tpl = $("#import_service_tmpl").html();
    var template = Handlebars.compile(tpl);
    var html = template(data);
    var e = "<i class='fa fa-times-circle'></i> ";
    layer.open({
        type: 1,
        skin: 'layui-layer-lan', //加上边框
        area: ['600px', '240px'], //宽高
        content: html,
        success: function (layero, index) {

            $("#serviceJsonFile").prettyFile();

            $("#importServiceForm").validate({
                errorPlacement: function (error, element) {
                    error.appendTo($("#serviceJsonFileTip"))
                },
                rules: {
                    serviceJsonFile: "required",
                },
                messages: {
                    serviceJsonFile: e + "请选择上传文件",
                },
                submitHandler: function (form) {
                    var url = pagePrefix + '/checkServiceRepeat';
                    var formData = new FormData(document.getElementById("importServiceForm"));
                    $.ajax({
                        cache: true,
                        type: "post",
                        url: url,
                        processData: false,
                        contentType: false,
                        data: formData,
                        async: true,
                        success: function (data) {
                            if (ajaxIsSuccess(data)) {
                                upload(index);
                            } else if (data.msg == "repeat") {
                                layer.confirm('有相同id的API配置，是否覆盖，如不想覆盖请修改文件中的serviceId？', {
                                    btn: ['覆盖', '取消'] //按钮
                                }, function () {
                                    upload(index);
                                }, function () {

                                });
                            } else {
                                layer.alert('保存API配置失败', {
                                    icon: 2,
                                    skin: 'layui-layer-lan',
                                    content: data.msg
                                })
                            }

                        }
                    });

                }
            })

            $("#closeImport").click(function () {
                layer.close(index);
            })

        }
    });
}

function upload(index) {
    var url = pagePrefix + '/importFile';
    var formData = new FormData(document.getElementById("importServiceForm"));
    $.ajax({
        cache: true,
        type: "post",
        url: url,
        processData: false,
        contentType: false,
        data: formData,
        async: true,
        success: function (data) {
            if (ajaxIsSuccess(data)) {
                layer.alert('上传API配置成功！', {
                    icon: 1,
                    skin: 'layui-layer-lan',
                    content: '上传API配置成功！'
                }, function (index1, layero) {
                    layer.close(index);
                    layer.close(index1);
                    reLoad();
                })
            } else {
                layer.alert('保存API配置失败', {
                    icon: 2,
                    skin: 'layui-layer-lan',
                    content: data.msg
                })
            }

        }
    });
}

function remove(id) {
    swal({
        title: "确定要删除选中的记录？",
        text: "确定要删除选中的记录，删除后不可逆？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        $.ajax({
            url: apiPrefix + "/" + id,
            type: "DELETE",
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "删除API配置成功！",
                            text: "删除API配置成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除API配置失败！", data.msg, "error");
                }
            }

        });
    });
}

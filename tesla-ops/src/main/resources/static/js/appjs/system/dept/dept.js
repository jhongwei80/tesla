var prefix = "/sys/dept"
var apiPrefix = "/api/sys/dept"
$(function () {
    load();
});

function load() {
    $('#deptTable').bootstrapTreeTable({
        id: 'deptId',
        code: 'deptId',
        parentCode: 'parentId',
        type: "GET",
        pageSize: 5,
        pageList: [5, 10, 20],
        expandColumn: "0",
        expandAll: false,
        toolbar: "deptToolBar",
        url: apiPrefix,
        columns: [{
            field: 'deptId',
            title: '编号'
        }, {
            field: 'name',
            title: '部门名称'
        }, {
            field: 'orderNum',
            title: '排序'
        }, {
            field: 'delFlag',
            title: '状态',
            align: 'center',
            formatter: function (item, index) {
                if (item.delFlag == '0') {
                    return '<span class="label label-danger">禁用</span>';
                } else if (item.delFlag == '1') {
                    return '<span class="label label-primary">正常</span>';
                }
            }
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            formatter: function (item, index) {
                var e = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + item.deptId + '\')"><i class="glyphicon glyphicon-edit"></i></a> ';
                var a = '<a class="' + s_add_h + '" href="javascript:void(0)" title="增加下級"  mce_href="#" onclick="add(\'' + item.deptId + '\')"><i class="glyphicon glyphicon-plus"></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="removeone(\'' + item.deptId + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                /*var f = '<a class="＂ href="#" title="备用"  mce_href="javascript:void(0)" onclick="resetPwd(\'' + item.deptId + '\')"><i class="fa fa-key"></i></a> ';*/
                return e + a + d;
            }
        }]
    });
}

function reLoad() {
    var tableHtml = '<table id="deptTable" </table>';
    $("#deptToolbar").nextAll().remove();
    $("#deptToolbar").after(tableHtml);
    load();
}

function add(pId) {
    var url = prefix + '/add/' + pId;
    var title = '新增部门';
    page(url, title);
}

function edit(id) {
    var url = prefix + '/edit/' + id;
    var title = '编辑部门';
    page(url, title);
}

function removeone(id) {


    swal({
        title: "确定要删除选中的部门？",
        text: "确定要删除选中的部门，删除后不可逆？",
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
                            title: "删除部门成功！",
                            text: "删除部门成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除部门失败！", "删除部门失败！", "error");
                }
            }

        });
    });
}

function resetPwd(id) {
}

function batchRemove() {

    var rows = $('#deptTable').bootstrapTable('getSelections');

    if (rows.length == 0) {
        swal("请选择要删除的部门？", "请选择要删除的部门？", "warning");
        return;
    }

    swal({
        title: "删除确认？",
        text: "确定要删除选中的部门？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        sureremove();
    });

    function sureremove() {
        var ids = new Array();
        $.each(rows, function (i, row) {
            ids[i] = row['id'];
        });
        $.ajax({
            type: 'POST',
            data: {
                _method: "DELETE",
                "ids": ids
            },
            url: apiPrefix,
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "删除部门成功！",
                            text: "删除部门成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除部门失败！", "删除部门失败！", "error");
                }
            }
        });
    }
}

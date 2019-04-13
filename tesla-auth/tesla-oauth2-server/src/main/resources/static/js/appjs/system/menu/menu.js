var PREFIX = "/sys/menu";
var RESTFUL_URL = "/api/sys/menu";
$(function() {
    initTable();
});

var initTable = function(){
    $('#menuTable').bootstrapTreeTable({
        id: 'menuId',
        code: 'menuId',
        parentCode: 'parentId',
        type: "GET",
        url: RESTFUL_URL,
        expandColumn: "1",
        expandAll: false,
        toolbar: "menuToolBar",
        pageSize: 5,
        pageList: [5,10,20],
        columns: [{
            title: '编号',
            field: 'menuId',
            visible: false,
            align: 'center',
            valign: 'middle',
            width: '50px'
        }, {
            title: '名称',
            field: 'name'
        }, {
            title: '图标',
            field: 'icon',
            align: 'center',
            valign: 'middle',
            formatter: function(item, index) {
                return item.icon == null ? '' : '<i class="' + item.icon + ' fa-lg"></i>';
            }
        }, {
            title: '类型',
            field: 'type',
            align: 'center',
            valign: 'middle',
            formatter: function(item, index) {
                if (item.type === 0) { return '<span class="label label-primary">目录</span>'; }
                if (item.type === 1) { return '<span class="label label-success">菜单</span>'; }
                if (item.type === 2) { return '<span class="label label-warning">按钮</span>'; }
            }
        }, {
            title: '地址',
            field: 'url'
        }, {
            title: '权限标识',
            field: 'perms'
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            formatter: function(item, index) {
                var e = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + item.menuId + '\');"><i class="glyphicon glyphicon-edit"></i></a> ';
                var p = '<a class="' + s_add_h + '" href="javascript:void(0)" mce_href="#" title="添加下级" onclick="add(\'' + item.menuId + '\');"><i class="glyphicon glyphicon-plus"></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + item.menuId + '\');"><i class="glyphicon glyphicon-remove"></i></a> ';
                return e + d + p;
            }
        }]
    });
};

var refresh = function(){
    var tableHtml = '<table id="menuTable" data-mobile-responsive="true" class="mb-bootstrap-table text-nowrap"></table>';
    $("#menuToolBar").nextAll().remove();
    $("#menuToolBar").after(tableHtml);
    initTable();
};

function add(pId) {
    var url = PREFIX + '/add/'+pId;
    var title = '添加系统菜单';
    page(url, title);
}

function edit(id) {
    var url = PREFIX + '/edit/' + id;
    var title = '编辑系统菜单';
    page(url, title);
}

function remove(id) {
    swal({
        title: "确定要删除选中的记录？",
        text: "确定要删除选中的记录(子菜单同时删除)，删除后不可逆？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        $.ajax({
            url: RESTFUL_URL + '/' +id,
            type: "delete",
            success: function (data) {
                if(ajaxIsSuccess(data)){
                    swal({
                            title: "删除系统菜单成功！",
                            text: "删除系统菜单成功！",
                            type: "success"
                        }, function (e) {
                            refresh();
                        });
                }else{
                    swal("删除系统菜单失败！", "删除系统菜单失败！", "error");
                }
            }
        });
    });
}

function batchRemove() {
    var rows = $('#menuTable').bootstrapTable('getSelections');
    if (rows.length == 0) {
        swal("请选择要删除的记录？", "请选择要删除的记录？", "warning");
        return;
    }

    swal({
        title: "删除确认？",
        text: "确定要删除选中的记录？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false
    }, function () {
        sureremove();
    });

    function sureremove() {
        var ids = [];
        $.each(rows, function (i, row) {
            ids[i] = row['id'];
        });
        $.ajax({
            type: 'POST',
            data: {
                _method: "DELETE",
                "ids": ids
            },
            url: RESTFUL_URL,
            success: function (data) {
                if(ajaxIsSuccess(data)){
                    swal({
                            title: "删除系统菜单成功！",
                            text: "删除系统菜单成功！",
                            type: "success"
                        }, function (e) {
                            refresh();
                        });
                }else{
                    swal("删除系统菜单失败！", "删除系统菜单失败！", "error");
                }
            }
        });
    }
}
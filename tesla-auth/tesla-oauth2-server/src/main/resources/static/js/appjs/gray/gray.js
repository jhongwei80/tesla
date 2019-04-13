var pagePrefix = "/gray";
var apiPrefix = "/api/gray";
$(function() {
  load();
});

function reLoad() {
    $('#grayTable').bootstrapTable('refresh');
}

function load() {
    $('#grayTable').bootstrapTable({
        method: 'get',
        url: apiPrefix,
        iconSize: 'outline',
        striped: true,
        dataType: "json",
        pagination: true,
        singleSelect: false,
        pageSize: 5,
        pageList: [5, 10, 20],
        pageNumber: 1,
        sidePagination: "server",
        showRefresh: true,
        icons: {refresh: "glyphicon-repeat"},
        toolbar: "#grayToolbar",
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        onLoadSuccess:function(data){
            $("[data-toggle='popover']").popover({
                'html':true
            });
        },
        uniqueId: 'id',
        columns: [{
            checkbox: true
        }, {
            field: 'planName',
            title: '计划名称'
        }, {
            field: 'planDesc',
            title: '计划描述'
        },{
            field: 'planOwner',
            title: '计划负责人'
        }, {
            field: 'enable',
            title: '是否启用',
            formatter: function (value, row, index) {
                if(value === 'Y'){
                    return '启用';
                } else if (value === 'N') {
                    return '禁用';
                }
                return value;
            }
        },{
            field: 'effectTime',
            title: '生效时间'
        }, {
            field: 'expireTime',
            title: '失效时间'
        }, {
            field: 'gmtCreate',
            title: '创建时间'
        }, {
            field: 'gmtModified',
            title: '更新时间'
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            width: 90,
            formatter: function (value, row, index) {
                var e = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.id + '\')"><i class="glyphicon glyphicon-edit"></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.id + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                var v = '<a class="' + s_edit_h + '" href="javascript:void(0)" title="查看策略拓扑图"  mce_href="#" onclick="view(\'' + row.id + '\')"><i class="glyphicon glyphicon-eye-open"></i></a> ';
                var p = '';
                if(row.enable === 'N'){
                    p = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="推送" onclick="push(\'' + row.id + '\')"><i class="glyphicon glyphicon-send"></i></a> ';
                }else if(row.enable === 'Y'){
                    p = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="禁用" onclick="disable(\'' + row.id + '\')"><i class="glyphicon glyphicon-off"></i></a> ';
                }
                return e + d + v + p;
            }
        }]
    });
}

function view(id){
    var url = pagePrefix + '/view/' + id;
    var title = '灰度策略拓扑图';
    page(url, title);
}

function add() {
    var url = pagePrefix + '/add';
    var title = '新建灰度计划';
    page(url, title);
}

function edit(id) {
    var url = pagePrefix + '/edit/' + id;
    var title = '编辑规则';
    page(url, title);
}

function remove(id) {
    swal({
        title: "确定要删除灰度计划？",
        text: "确定要删除灰度计划，删除后不可逆？",
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
                            title: "删除灰度计划成功！",
                            text: "删除灰度计划成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除灰度计划失败！", "删除灰度计划失败！", "error");
                }
            }
        });
    });
}

function batchRemove() {
    var rows = $('#grayTable').bootstrapTable('getSelections');
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
        closeOnConfirm: false,
        showLoaderOnConfirm: true
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
            url: apiPrefix ,
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "删除灰度计划成功！",
                            text: "删除灰度计划成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除灰度计划失败！", "删除灰度计划失败！", "error");
                }
            }
        });
    }

}

var disable = function (id) {
    swal({
        title: "确定要禁用灰度计划？",
        text: "确定要禁用灰度计划？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "禁用",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        $.ajax({
            url: apiPrefix + "/disable/"+id,
            type: "GET",
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "禁用灰度计划成功！",
                            text: "禁用灰度计划成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("禁用灰度计划失败！", "禁用灰度计划失败！", "error");
                }
            }
        });
    });
};

var push = function (id) {
    swal({
        title: "确定要推送灰度计划？",
        text: "确定要推送灰度计划？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "推送",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        $.ajax({
            url: apiPrefix + "/push/"+id,
            type: "GET",
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "推送灰度计划成功！",
                            text: "推送灰度计划成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("推送灰度计划失败！", "推送灰度计划失败！", "error");
                }
            }
        });
    });
};
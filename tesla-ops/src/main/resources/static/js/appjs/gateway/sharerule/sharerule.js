var prefix = "/filter/sharerule";
var share_rule_url = "/api/filter/sharerule";
$(function() {
    load();
});
function load() {
  $('#shareRuleTable').bootstrapTable({
      method: 'get',
      url: share_rule_url,
      striped: true,
      dataType: "json",
      pagination: true,
      singleSelect: false,
      pageSize: 5,
      pageList: [5, 10, 20],
      pageNumber: 1,
      sidePagination: "server",
      showRefresh: true,
      iconSize: "outline",
      icons: {refresh: "glyphicon-repeat"},
      toolbar:"#shareRuleToolBar",
      queryParams: function (params) {
          return {
              limit: params.limit,
              offset: params.offset
          };
      },
      uniqueId: 'id',
      columns: [{
          checkbox: true
      }, {
          field: 'name',
          title: '名称'
      }, {
          field: 'rule',
          title: '规则',
          formatter: function(value, row, index) {
              if(value != null && value.length > 0){
                  return `<a href="javascript:void(0);" onclick="view('${row.id}')"><strong>详情</strong></a>`;
              }else {
                  return '...';
              }
          }
      }, {
          field: 'describe',
          title: '描述'
      }, {
          field: 'filterName',
          title: '类型'
      },{
          field: 'filterOrder',
          title: '执行顺序'
      },{
          field: 'gmtCreate',
          title: '创建时间'
      }, {
          field: 'gmtModified',
          title: '更新时间'
      }, {
          title: '操作',
          field: 'id',
          align: 'center',
          formatter: function(value, row, index) {
              var e = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.id + '\')"><i class="glyphicon glyphicon-edit"></i></a> ';
              var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.id + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
              return e + d;
          }
      }]
  });
}

function reLoad() {
    $('#shareRuleTable').bootstrapTable('refresh');
}

function add() {
    var url = prefix + '/add';
    var title = '添加系统waf规则';
    page(url,title);
}

function edit(id) {
    var url = prefix + '/edit/' + id;
    var title = '编辑系统waf规则';
    page(url, title);
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
            url: share_rule_url + '/' +id,
            type: "delete",
            success: function (data) {
                if(ajaxIsSuccess(data)){
                    swal(
                        {
                            title: "删除系统waf规则成功！",
                            text: "删除系统waf规则成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                }else{
                    swal("删除系统waf规则失败！", "删除系统waf规则失败！", "error");
                }
            }

        });
    });
}

function batchRemove() {
    var rows = $('#shareRuleTable').bootstrapTable('getSelections');
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
            url: share_rule_url,
            success: function (data) {
                if(ajaxIsSuccess(data)){
                    swal(
                        {
                            title: "删除系统waf规则成功！",
                            text: "删除系统waf规则成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                }else{
                    swal("删除系统waf规则失败！", "删除系统waf规则失败！", "error");
                }
            }
        });
    }
}

function view(id) {
    var row = $('#shareRuleTable').bootstrapTable('getRowByUniqueId', id);
    $('#dialog').find('.modal-body').html('<textarea id="ruleDetailId" class="rule" rows="100" style="width:100%"></textarea>');
    var editor = initEditor('ruleDetailId');
    editor.getDoc().setValue(row.rule);
    $('#dialog').modal('show');
}

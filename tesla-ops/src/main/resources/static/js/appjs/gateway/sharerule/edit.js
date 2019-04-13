var prefix = "/filter/sharerule";
var share_rule_url = "/api/filter/sharerule";
$(document).ready(function() {
    initSelect();
    initEditor('rule');
    formValidator();
});

var initSelect = function(){
    $("#name").selectpicker({
        liveSearch: true,
        noneSelectedText: '请选择规则名称',
        noneResultsText: '没有找到规则！'
    });
};

var formValidator = function() {
    var e = "<i class='fa fa-times-circle'></i> ";
    $.extend($.validator.defaults,{ignore:""});
    $("#shareRuleForm").validate({
        rules: {
            rule: "required",
            name: "required",
            describe: "required",
            filterType: "required"
        },
        messages: {
            rule: e + "请输入详细规则内容",
            name: e + "请选择规则名称",
            describe: e + "请输入规则描述",
            filterType: e + "请选择规则名称"
        },
        submitHandler: function(form) {
            swal({
                title: "修改记录？",
                text: "是否确定修改通用组件规则？",
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
                    url: share_rule_url,
                    data: $(form).serialize()+"&_method=PUT",
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal({
                                title: "修改通用组件规则成功！",
                                text: "修改通用组件规则成功！",
                                type: "success"
                            }, function (e) {
                                removeIframe(true);
                            });
                        } else {
                            swal("修改通用组件规则失败！", "修改通用组件规则失败。", "error");
                        }
                    }
                });
            });
        },
        errorPlacement:function(error,element) {
            if (element.attr("name") == "rule") {
                error.insertAfter(".CodeMirror");
            } else {
                error.insertAfter(element);
            }
        },
    });
};
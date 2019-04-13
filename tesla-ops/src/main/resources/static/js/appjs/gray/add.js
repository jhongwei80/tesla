var apiPrefix = "/api/gray";
var apiPolicyPrefix = "/api/gray/policy";
var apiEurekaPrefix = "/api/gray/eureka";

var tabSize=0;

var appList;

$(document).ready(function() {

    //日期时间选择器
    laydate.render({
        elem: '#effectTime'
        ,type: 'datetime'
    });
    laydate.render({
        elem: '#expireTime'
        ,type: 'datetime'
    });

    $('#addGrayRule').click(function() {
        initGrayRuleEdit();
    });

    jQuery.validator.addMethod("compareDate",
        function (value, element, param) {
            var effectTime = $("#effectTime").val();
            var expireTime = $("#expireTime").val();
            var reg = new RegExp('-','g');

            effectTime = new Date(parseInt(Date.parse(effectTime),10));
            expireTime = new Date(parseInt(Date.parse(expireTime),10));
            if(effectTime>expireTime){
                return false;
            }else{
                return true;
            }
        }, $.validator.format("结束日期必须大于开始日期"));

    jQuery.validator.addMethod("notEqualTo",
        function (value, element, param) {
            var targetValue = $(param).val();
            return value !== targetValue;
        });

    initBaseForm();
    getEurekaApps();
});


function getEurekaApps() {
    $.ajax({
        cache: true,
        type: "get",
        url: apiEurekaPrefix,
        async: false,
        success: function (data) {
            appList = data;
        }
    });
}

function initBaseForm() {
    var e = "<i class='fa fa-times-circle'></i> ";
    $("#garyForm").validate({
        rules: {
            planName: "required",
            planOwner: "required",
            effectTime: "required",
            expireTime: {"required":true,"compareDate":true}
        },
        messages: {
            planName: e + "请输入计划名称",
            planOwner: e + "请输入计划负责人名称",
            effectTime: e + "请输入计划生效时间",
            expireTime: {"required":e + "请输入计划结束时间","compareDate":e + "失效日期必须大于生效日期"}
        },
        submitHandler: function (form) {
            swal({
                title: "新增灰度计划？",
                text: "是否确定保存灰度计划基础信息？",
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
                    url: apiPrefix,
                    data: $('#garyForm').serialize(),
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            $("#id").val(data.id);
                            $("#enable").val(data.enable);
                            swal(
                                {
                                    title: "保存灰度计划基础信息成功！",
                                    text: "保存灰度计划基础信息成功！",
                                    type: "success"
                                }, function (e) {

                                });

                        } else {
                            swal("保存灰度计划基础信息失败！", "保存灰度计划基础信息失败。", "error");
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
}

function initGrayRuleEdit() {


    var data = {
        nextTab: tabSize
    };
    $('#dynamicsTabli').tmpl(data).appendTo('.nav-tabs');
    $('#dynamicsTabContent').tmpl(data).appendTo('.tab-content');
    $('.nav-tabs').find('a:last').tab('show');
    $('#grayRuleSection').tmpl(data).appendTo('#grayDiv_' + tabSize);
    var policyMax = 0;
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.grayPolicyParamConditionVOS\\["+policyMax+"\\]\\.paramKind").selectpicker('refresh');
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.grayPolicyParamConditionVOS\\["+policyMax+"\\]\\.paramKind").show();

    var e = "<i class='fa fa-times-circle'></i> ";
    $("#grayRuleForm_"+tabSize).validate({});
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.consumerService").rules("add",{required:true,messages:{required:e+'消费方不能为空'}});
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.providerService").rules("add",{required:true,messages:{required:e+'提供方不能为空'}});
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.providerService").rules("add",{notEqualTo:"#grayPolicyVOS\\\\["+tabSize+"\\\\]\\\\.consumerService",messages:{notEqualTo:e+'消费方和提供方不能相同'}});

    for ( var i in appList) {
        $("#grayPolicyVOS\\["+tabSize+"\\]\\.consumerService").append("<option value='"+appList[i]+"'>"+appList[i]+"</option>");
        $("#grayPolicyVOS\\["+tabSize+"\\]\\.providerService").append("<option value='"+appList[i]+"'>"+appList[i]+"</option>");
    }
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.consumerService").selectpicker('render');
    $("#grayPolicyVOS\\["+tabSize+"\\]\\.providerService").selectpicker('render');
    tabSize++;

}

function fnClickAddRow(tabIndex){
    var policyMax = $("#grayPolicyVOS\\["+tabIndex+"\\]\\.maxIndex").val();
    policyMax ++ ;
    $("#grayPolicyVOS\\["+tabIndex+"\\]\\.maxIndex").val(policyMax);

    var data = {
        policyIndex: tabIndex,
        policyRuleIndex:policyMax
    };
    $('#policySection').tmpl(data).appendTo("[name='policy\\["+tabIndex+"\\]']");
    $("#grayPolicyVOS\\["+tabIndex+"\\]\\.grayPolicyParamConditionVOS\\["+policyMax+"\\]\\.paramKind").selectpicker('refresh');
    $("#grayPolicyVOS\\["+tabIndex+"\\]\\.grayPolicyParamConditionVOS\\["+policyMax+"\\]\\.paramKind").show();
}

function delrow(obj) {
    $(obj).parent().parent().remove();
}

function closeTab(index) {

    $(".nav-tabs").find("li.active").remove();
    $('#grayDiv_' + index).remove();
    $('.nav-tabs').find('a:last').tab('show');

}

function delSingleGrayRule(index) {
    swal({
        title: "删除规则？",
        text: "是否确定删除规则？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        if (isNaN(parseInt($("#grayPolicyVOS\\["+index+"\\]\\.id").val()))) {
            closeTab(index);
            setTimeout(function () {
                swal("删除成功", "规则未入库，删除成功", "success");
            },200);
        }else{
            $.ajax({
                cache: true,
                type: "delete",
                url: apiPolicyPrefix+"/"+parseInt($("#grayPolicyVOS\\["+index+"\\]\\.id").val()),
                async: true,
                success: function (data) {
                    if (ajaxIsSuccess(data)) {
                        closeTab(index);
                        swal("删除成功","规则已入库，删除成功，库中数据已删除","success");
                    } else {
                        swal("删除失败！", "规则已入库，删除失败。", "error");
                    }
                }
            });
        }
    });
}

function addSingleGrayRule(index) {
    if(!$("#garyForm").valid()){
        return;
    }
    if(!$("#grayRuleForm_"+index).valid()){
        return;
    }
    var formdata = new FormData(document.getElementById("grayRuleForm_"+index));
    if(!isNaN(parseInt($("#id").val()))){
        formdata.append("id",parseInt($("#id").val()));
    }
    formdata.append("planName",$("#planName").val());
    formdata.append("planDesc",$("#planDesc").val());
    formdata.append("planOwner",$("#planOwner").val());
    formdata.append("effectTime",$("#effectTime").val());
    formdata.append("expireTime",$("#expireTime").val());
    formdata.append("enable",$("#enable").val());

    $.ajax({
        type: "post",
        url: apiPrefix,
        data: formdata,
        processData:false,
        contentType:false,
        async: true,
        success: function (data) {
            if (ajaxIsSuccess(data)) {
                $("#id").val(data.id);
                $("#enable").val(data.enable);
                $("#grayPolicyVOS\\["+index+"\\]\\.id").val(data["grayPolicyId"]);
                swal("保存规则信息成功！","保存规则信息成功！","success");
            } else {
                swal("保存规则信息失败！", "保存规则信息失败。", "error");
            }

        },
        error:function (XMLHttpRequest, textStatus, errorThrown)  {
            swal("保存规则基础信息失败！", textStatus, "error");
        }
    });
}

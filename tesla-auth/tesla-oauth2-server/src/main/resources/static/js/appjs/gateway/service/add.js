var apiPrefix = "/api/gateway/service";
var pagePrefix = "/gateway/service";


$(function () {
    routeConfig.init($('#service_router_panel'));
    authConfig.init($('#service_auth_panel'));
    rateLimitConfig.init($('#service_rate_limit_panel'))
    serviceHeaderConfig.init($('#service_modify_header_panel'));

    $("input[name=servicePrefix]").blur(function () {
        $("input[name=serviceAddress]").val(osgGatewayUrl + path($("input[name=servicePrefix]").val()))
    });
    $("input[name=servicePrefix]").blur();
    initForm();
    $("#addEndpoint").click(function () {
        closeAllEndpoint()
        endpointConfig.init();
    })

});

function closeAllEndpoint() {
    $("#endpointListPanelBody").find("a[name='endpoint-close']").each(function () {
        if ($(this).parent().parent().find("div[name='endpoint']").attr("class") == "panel-collapse collapse in") {
            $(this).click();
        }
    });
}

function initForm() {
    var e = "<i class='fa fa-times-circle'></i> ";
    $("#serviceForm").validate({
        rules: {
            serviceName: "required",
            servicePrefix: "required",
            serviceDesc: "required"
        },
        messages: {
            serviceName: e + "请输入服务名称",
            servicePrefix: e + "请输入服务前缀",
            serviceDesc: e + "请输入服务描述"
        },
        submitHandler: function (form) {
            swal({
                title: "新增记录？",
                text: "是否确定保存接口分组信息？",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "保存",
                closeOnConfirm: false,
                showLoaderOnConfirm: true
            }, function () {
                var serviceDefinition = {};

                var formData = new FormData();

                if ($("input[name=serviceId]").val() != undefined) {
                    serviceDefinition.serviceId = $("input[name=serviceId]").val();
                }
                serviceDefinition.serviceName = $("input[name=serviceName]").val();
                serviceDefinition.serviceDesc = $("textarea[name=serviceDesc]").val();
                serviceDefinition.serviceEnabled = $("#serviceEnabled").val();
                serviceDefinition.servicePrefix = $("input[name=servicePrefix]").val();

                serviceDefinition.routerDTO = routeConfig.getFormatResult($('#routeConent'));

                var pluginList = [];
                pluginList = authConfig.getFormatResult($('#authConent'), pluginList);
                pluginList = rateLimitConfig.getFormatResult($('#service_rate_limit_panel'), pluginList);
                pluginList = serviceHeaderConfig.getFormatResult($('#service_modify_header_panel'), "request", pluginList);
                pluginList = serviceHeaderConfig.getFormatResult($('#service_modify_header_panel'), "response", pluginList);

                serviceDefinition.pluginDTOList = pluginList;
                serviceDefinition.endpointDTOList = getEndpointList();

                var fileMap = {};
                $.each($("input[type='file']"), function (i, file) {
                    var fileObj = file.files[0];
                    if (fileObj != undefined) {
                        formData.append($(file).parent().parent().parent().find("input[type='hidden']").val(), fileObj);
                    }
                })
                formData.append("serviceDTO", JSON.stringify(serviceDefinition));

                $.ajax({
                    cache: true,
                    type: "post",
                    url: apiPrefix,
                    processData: false,
                    contentType: false,
                    data: formData,
                    async: true,
                    success: function (data) {
                        if (ajaxIsSuccess(data)) {
                            swal(
                                {
                                    title: "保存服务配置成功！",
                                    text: "保存服务配置成功！",
                                    type: "success"
                                }, function (e) {
                                    removeIframe(true);
                                });
                        } else {
                            swal("保存服务配置失败！", data.msg, "error");
                        }

                    }
                });
                return;
            });
        }

    });
}


function getEndpointList() {
    var endpointList = [];
    $.each($("#endpointListPanelBody").find("div[name='endpoint']"), function (i, endpointDiv) {
        endpointList.push(getEndpoint($(endpointDiv)));
    })
    return endpointList;
}

function getEndpoint(endpointDiv) {
    var endpointDefinition = {};
    endpointDefinition.endpointMethod = endpointDiv.find("select[name='endpointMethod']").val();
    endpointDefinition.endpointUrl = endpointDiv.find("input[name='endpointUrl']").val();
    endpointDefinition.pluginDTOList = getEndpointPluginList(endpointDiv);
    return endpointDefinition;
}

function getEndpointPluginList(endpointDiv) {
    var endpointPlugins = [];
    var index = endpointDiv.find("input[name='index']").val();
    $.each($("#endpoint-" + index).find("div[name='selectedPluginDiv']").find("button"), function (i, pluginButton) {
        var pluginType = $(pluginButton).attr("name");
        endpointPlugins = getConfig(pluginType).getFormatResult(endpointDiv, endpointPlugins);
    })
    return endpointPlugins;

}


function addEndpointPluginConfigDiv(endpointDiv, authType) {
    var config = getConfig(authType);
    if (config != undefined) {
        config.init(endpointDiv, authType);
    }
}

function getConfig(pluginType) {
    switch (pluginType) {
        case 'IgnoreAuth':
            return ignoreAuthConfig;
            break;
        case 'CreateToken':
            return createTokenConfig;
            break;
        case 'SilentRegistration':
            return silentRegistrationConfig;
            break;
        case 'PathTransform':
            return pathTransformConfig;
            break;
        case 'BodyTransform':
            return bodyTransformConfig;
            break;
        case 'SignatureVerify':
            return signtureVerifyConfig;
            break;
        case 'JarExecute':
            return jarExecuteConfig;
            break;
        case 'GroovyExecute':
            return groovyExecuteConfig;
            break;
        case 'RpcRouting':
            return rpcRoutingConfig;
            break;
        case 'ModifyHeader':
            return endpointHeaderConfig;
            break;
        case 'AccessToken':
            return accessTokenConfig;
            break;
        case 'CacheResult':
            return cacheResultConfig;
            break;
        case 'Mock':
            return mockConfig;
            break;
        default:
            return undefined;
    }
}

function removeEndpoint(endpointDivId) {
    $("#" + endpointDivId).parent().remove();
}


function addEventOnPluginSelect(endpointDiv) {
    endpointDiv.find("select[name='endpointPluginsSelect']").change(function () {
        var selectedPlugins = endpointDiv.find("select[name='endpointPluginsSelect']").val();
        if (selectedPlugins == undefined) {
            selectedPlugins = [];
        }
        $.each(selectedPlugins, function (i, val) {
            if (endpointDiv.find("div[name='selectedPluginDiv']").find("button[name='" + val + "']")[0] == undefined) {
                var data = {
                    index: endpointDiv.find("input[name='index']").val(),
                    pluginType: val,
                    pluginName: endpointDiv.find("select[name='endpointPluginsSelect']").find("option[value='" + val + "']").text()
                };
                var tpl = $("#selectedPluginDiv_button").html();
                var template = Handlebars.compile(tpl);
                var html = template(data);
                endpointDiv.find("div[name='selectedPluginDiv']").append(html);
                addEndpointPluginConfigDiv(endpointDiv, val);
            }
        })
        $.each(endpointDiv.find("div[name='selectedPluginDiv']").find("button"), function (i, val) {
            if (selectedPlugins.indexOf($(val).attr("name")) == -1) {
                $(val).parent().remove();
                removeEndpointPlugin("endpoint-" + endpointDiv.find("input[name='index']").val(), $(val).attr("name"));
            }

        });
    })
}

function removeEndpointPlugin(endpointDivId, authType) {
    var endpointDiv = $("#" + endpointDivId);
    endpointDiv.find("button[name='" + authType + "']").parent().remove();
    var selectedPlugins = endpointDiv.find("select[name='endpointPluginsSelect']").val();
    if (selectedPlugins == undefined) {
        selectedPlugins = [];
    }
    var changedSelectPlugins = [];
    $.each(selectedPlugins, function (i, val) {
        if (endpointDiv.find("div[name='selectedPluginDiv']").find("button[name='" + val + "']")[0] != undefined) {
            changedSelectPlugins.push(val);
        }
    });
    endpointDiv.find("select[name='endpointPluginsSelect']").selectpicker('val', changedSelectPlugins);
    var config = getConfig(authType);
    if (config != undefined) {
        config.remove(endpointDiv, authType);
    }
}

function initTextArea(endpointDiv, templateName, mode) {
    if (templateName == "") {
        initTextAreaByContent(endpointDiv, "", mode)
    } else {
        $.ajax({
            url: pagePrefix + "/template/" + templateName,
            async: false,
            success: function (result) {
                initTextAreaByContent(endpointDiv, result, mode)
            }
        });
    }
}

function initTextAreaByContent(endpointDiv, content, mode) {
    var textareas = endpointDiv.find("textarea");
    textareas.each(function () {
        var textarea = $(this);
        textarea.val(content);
        var myCodeMirror = CodeMirror.fromTextArea(textarea[0], {
            lineWrapping: true,
            lineNumbers: true,
            autoRefresh: true,
            mode: mode
        });
        myCodeMirror.on("blur", function () {
            textarea.val(myCodeMirror.getValue());
        })
        textarea.change(function () {
            myCodeMirror.setValue(textarea.val());
        })
    });

}

function clearTextarea(divObj) {
    var textAreas = divObj.find("textarea");
    textAreas.each(function () {
        $(this).val("");
        $(this).change();
    });
}

function path(url) {
    if (url == "") {
        return url;
    }
    if (url.indexOf("/") != 0) {
        return "/" + url;
    }
    return url;
}
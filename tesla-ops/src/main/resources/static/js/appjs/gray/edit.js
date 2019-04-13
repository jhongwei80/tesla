function addGrayPolicyEdit(grayPolicy) {
    var tmptabSize = tabSize;
    initGrayRuleEdit();
    $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.consumerService").val(grayPolicy.consumerService);
    $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.providerService").val(grayPolicy.providerService);
    $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.consumerService").selectpicker("refresh");
    $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.providerService").selectpicker("refresh");
    $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.id").val(grayPolicy.id);
    $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.planId").val(grayPolicy.planId);
    var conditionIndex = 0;
    grayPolicy.grayPolicyNodeConditionVOS.forEach(function (nodeCondition) {
        conditionIndex = 0;
        while($("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyNodeConditionVOS\\["+conditionIndex+"\\]\\.paramKey").val()!=undefined){
            if($("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyNodeConditionVOS\\["+conditionIndex+"\\]\\.paramKey").val() == nodeCondition.paramKey){
                $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyNodeConditionVOS\\["+conditionIndex+"\\]\\.paramValue").val(nodeCondition.paramValue);
            }
            conditionIndex++;
        }
    });
    conditionIndex = 0;
    grayPolicy.grayPolicyParamConditionVOS.forEach(function (paramCondition) {
        if(conditionIndex != 0){
            fnClickAddRow(tmptabSize);
        }
        $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyParamConditionVOS\\["+conditionIndex+"\\]\\.paramValue").val(paramCondition.paramValue);
        $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyParamConditionVOS\\["+conditionIndex+"\\]\\.paramKey").val(paramCondition.paramKey);
        $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyParamConditionVOS\\["+conditionIndex+"\\]\\.paramKind").val(paramCondition.paramKind);
        $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyParamConditionVOS\\["+conditionIndex+"\\]\\.paramKind").selectpicker("refresh");

        if("Y" == paramCondition.transmit ){
            $("#grayPolicyVOS\\["+tmptabSize+"\\]\\.grayPolicyParamConditionVOS\\["+conditionIndex+"\\]\\.transmit").prop("checked","checked");
        }
        conditionIndex++;
    });

}
package io.github.tesla.ops.gray.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

import com.google.common.collect.Lists;

import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.domain.GrayPolicyDO;
import io.github.tesla.ops.gray.helper.BeanMapper;
import io.github.tesla.ops.gray.helper.GrayParamKind;
import io.github.tesla.ops.gray.helper.YesNoKind;

/**
 * 灰度计划
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/11/5 17:16
 * @version: V1.0.0
 * @since JDK 11
 */
public class GrayPlanVO implements Serializable {

    private Long id;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 计划描述
     */
    private String planDesc;

    /**
     * 计划负责人
     */
    private String planOwner;

    /**
     * 是否启用
     */
    private String enable;

    /**
     * 生效时间
     */
    private String effectTime;

    /**
     * 失效时间
     */
    private String expireTime;

    private List<GrayPolicyVO> grayPolicyVOS;

    /**
     * 创建者
     */
    private String createUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }

    public String getPlanOwner() {
        return planOwner;
    }

    public void setPlanOwner(String planOwner) {
        this.planOwner = planOwner;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(String effectTime) {
        this.effectTime = effectTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public List<GrayPolicyVO> getGrayPolicyVOS() {
        return grayPolicyVOS;
    }

    public void setGrayPolicyVOS(List<GrayPolicyVO> grayPolicyVOS) {
        this.grayPolicyVOS = grayPolicyVOS;
    }

    public GrayPlanDO createGrayPlanDO() {
        GrayPlanDO model = BeanMapper.map(this, GrayPlanDO.class);
        if (StringUtils.isNotBlank(this.getEffectTime())) {
            model.setEffectTime(Timestamp.valueOf(this.getEffectTime()));
        }
        if (StringUtils.isNotBlank(this.getExpireTime())) {
            model.setExpireTime(Timestamp.valueOf(this.getExpireTime()));
        }
        model.setCreateUser(SecurityUtils.getSubject().getPrincipals().getRealmNames().iterator().next());
        return model;
    }

    public GrayPolicyDO createGrayPolicyDO() {
        GrayPolicyVO grayPolicyVO = getEffectiveGrayPolicyVO();
        if (Objects.isNull(grayPolicyVO)) {
            return null;
        }
        return BeanMapper.map(grayPolicyVO, GrayPolicyDO.class);
    }

    private GrayPolicyVO getEffectiveGrayPolicyVO() {
        if (CollectionUtils.isEmpty(this.getGrayPolicyVOS())) {
            return null;
        }
        for (GrayPolicyVO grayPolicyVO : this.getGrayPolicyVOS()) {
            if (grayPolicyVO != null && StringUtils.isNotBlank(grayPolicyVO.getConsumerService())) {
                grayPolicyVO.setPlanId(this.getId());
                return grayPolicyVO;
            }
        }
        return null;
    }

    public List<GrayPolicyConditionDO> createGrayPolicyConditionDOList(Long policyId) {
        List<GrayPolicyConditionDO> grayPolicyConditionDOList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(this.getGrayPolicyVOS())) {
            return grayPolicyConditionDOList;
        }
        GrayPolicyVO grayPolicyVO = getEffectiveGrayPolicyVO();
        if (Objects.isNull(grayPolicyVO)) {
            return grayPolicyConditionDOList;
        }
        for (GrayPolicyConditionVO grayPolicyConditionVO : grayPolicyVO.getGrayPolicyNodeConditionVOS()) {
            GrayPolicyConditionDO grayPolicyConditionDO = getGrayPolicyConditionDO(policyId, grayPolicyConditionVO);
            if (Objects.isNull(grayPolicyConditionDO)) {
                continue;
            }
            grayPolicyConditionDOList.add(grayPolicyConditionDO);
        }
        for (GrayPolicyConditionVO grayPolicyConditionVO : grayPolicyVO.getGrayPolicyParamConditionVOS()) {
            GrayPolicyConditionDO grayPolicyConditionDO = getGrayPolicyConditionDO(policyId, grayPolicyConditionVO);
            if (Objects.isNull(grayPolicyConditionDO)) {
                continue;
            }
            grayPolicyConditionDOList.add(grayPolicyConditionDO);
        }
        return grayPolicyConditionDOList;
    }

    private GrayPolicyConditionDO getGrayPolicyConditionDO(Long policyId, GrayPolicyConditionVO grayPolicyConditionVO) {
        if (StringUtils.isBlank(grayPolicyConditionVO.getParamKey())
            || StringUtils.isBlank(grayPolicyConditionVO.getParamValue())) {
            return null;
        }
        GrayPolicyConditionDO grayPolicyConditionDO =
            BeanMapper.map(grayPolicyConditionVO, GrayPolicyConditionDO.class);
        grayPolicyConditionDO.setPlanId(this.getId());
        grayPolicyConditionDO.setPolicyId(policyId);
        grayPolicyConditionDO
            .setServiceTarget(GrayParamKind.get(grayPolicyConditionDO.getParamKind()).getTarget().getCode());
        if (StringUtils.isEmpty(grayPolicyConditionDO.getTransmit())) {
            grayPolicyConditionDO.setTransmit(YesNoKind.NO.getCode());
        }
        return grayPolicyConditionDO;
    }
}

package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.google.auto.service.AutoService;

import javax.validation.constraints.AssertFalse;

/**
 * javax.validation.constraints.AssertFalse
 */
@AutoService(IPropertyPostProcessor.class)
public class AssertFalsePostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        AssertFalse assertFalseAnno = propertyModel.getPropertyItem().getAnnotation(AssertFalse.class);
        if (assertFalseAnno == null) return propertyModel;

        propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值只能为false)"));

        return propertyModel;
    }
}

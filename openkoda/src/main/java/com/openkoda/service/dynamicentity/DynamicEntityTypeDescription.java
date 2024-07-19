package com.openkoda.service.dynamicentity;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;

import java.util.List;

class DynamicEntityTypeDescription extends TypeDescription.Latent {
    DynamicEntityTypeDescription(final String name, final int modifiers, final Generic superClass, final List<? extends Generic> interfaces) {
        super(name, modifiers, superClass, interfaces);
    }

    @Override
    public TypeDescription getDeclaringType() {
        return null;
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return new AnnotationList.Empty();
    }
}

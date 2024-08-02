package com.yeonieum.productservice.global.converter;

import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ServiceStatusCodeConverter implements AttributeConverter<ServiceStatusCode, String> {

    @Override
    public String convertToDatabaseColumn(ServiceStatusCode status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public ServiceStatusCode convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return ServiceStatusCode.fromCode(dbData);
    }
}
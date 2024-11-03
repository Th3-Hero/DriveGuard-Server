package com.group11.driveguard.api.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ValidationProcessor {

    /**
     * Add errors for the given {@link ConstraintViolation}s to the provided {@link ValidationDetail}.
     *
     * @param violations the {@link ConstraintViolation}s to process
     * @param detail the {@link ValidationDetail} to add the errors to
     */
    public static void addConstraintViolationsToValidationDetail(Collection<ConstraintViolation<?>> violations, ValidationDetail detail) {
        for (ConstraintViolation<?> violation : violations) {
            detail.addError(violation.getMessage(), getViolationPath(violation.getPropertyPath()));
        }
    }

    private static String getViolationPath(Path propertyPath) {
        StringBuilder fieldPath = new StringBuilder();
        for (Path.Node node : propertyPath) {
            // Some elements of the path are unnecessary clutter, so they can be skipped in the final field path
            if (node.getKind() != ElementKind.PARAMETER && node.getKind() != ElementKind.PROPERTY) {
                continue;
            }

            if (!fieldPath.isEmpty()) {
                fieldPath.append(".");
            }
            fieldPath.append(node);
        }
        return fieldPath.toString();
    }
}
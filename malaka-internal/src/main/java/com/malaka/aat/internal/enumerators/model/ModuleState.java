package com.malaka.aat.internal.enumerators.model;

import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.internal.model.Module;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum ModuleState {
    NEW("001", "Module yangi yaratildi"),
    SENT("002", "Module to'ldirildi va tasdiqlash uchun jo'natildi"),
    APPROVED("003", "Module tasdiqlangan"),
    REJECTED("004", "Module rad etildi");

    private final String value;
    private final String description;


    public static void setState(Module module, ModuleState state) {
        validateAndSet(module, state);
    }


    public static void setState(Module module, String state) {
        Optional<ModuleState> first = Arrays.stream(ModuleState.values())
                .filter(s -> s.value.equals(state))
                .findFirst();

        if (first.isPresent()) {
            validateAndSet(module, first.get());
        } else {
            throw new IllegalArgumentException("Module state not found with value: " + state);
        }
    }


    private static void validateAndSet(Module module, ModuleState state) {
        String currentState = module.getModuleState();

        switch (state) {
            case NEW -> {
                // NEW can only be set on module creation (when state is null)
                if (currentState != null) {
                    throw new BadRequestException("Cannot set module " + module.getId() + " to NEW state. NEW state can only be set on creation.");
                }
            }
            case SENT -> {
                // SENT can be set from NEW state (teacher confirms module is filled)
                if (currentState == null || (!currentState.equals("001") && !currentState.equals("004")) ) {
                    throw new BadRequestException("Cannot set module " + module.getId() + " to SENT state. Current state must be NEW (001) or REJECTED (004).");
                }
            }
            case APPROVED -> {
                // APPROVED can be set from SENT state (methodist/faculty head approves)
                // This typically happens when the course is approved
                if (currentState == null || !currentState.equals("002")) {
                    throw new BadRequestException("Cannot set module " + module.getId() + " to APPROVED state. Current state must be SENT (002).");
                }
            }
            case REJECTED -> {
                // REJECTED can be set from SENT state (methodist rejects)
                if (currentState == null || !currentState.equals("002")) {
                    throw new BadRequestException("Cannot set module " + module.getId() + " to REJECTED state. Current state must be SENT (002).");
                }
            }
        }

        module.setModuleState(state.getValue());
    }

    public static Optional<ModuleState> fromValue(String value) {
        return Arrays.stream(ModuleState.values())
                .filter(s -> s.value.equals(value))
                .findFirst();
    }
}

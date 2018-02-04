package com.spb.kbv.sampleskbv.rxbus.events;

import android.support.annotation.NonNull;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

public final class PermissionsEvents {
    @Data
    @EqualsAndHashCode(callSuper = false)
    public final static class GrantPermissionEvent implements RxBusEvent {
        private final int requestId;
        private final Map<String, Integer> resultsMap;
    }
}

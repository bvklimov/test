package com.spb.kbv.sampleskbv.rxbus;

import com.spb.kbv.sampleskbv.rxbus.events.RxBusEvent;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {
    private final PublishSubject<RxBusEvent> bus;

    public RxBus() {
        bus = PublishSubject.create();
    }

    public void send(RxBusEvent event) {
        bus.onNext(event);
    }

    public Observable<RxBusEvent> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
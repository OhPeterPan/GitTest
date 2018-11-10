package com.dalimao.mytaxi.rx;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxBus {
    private static RxBus sRxBus;
    private CopyOnWriteArraySet<Object> set;

    private RxBus() {
        set = new CopyOnWriteArraySet<>();

    }

    public void register(Object o) {
        set.add(o);
    }

    public void unRegister(Object event) {
        set.remove(event);
    }

    public static RxBus getInstance() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus();
                }
            }
        }
        return sRxBus;
    }

    @SuppressLint("checkResult")
    public void chainProcess(Function function) {
        Observable.just("")
                .map(function)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        for (Object o : set) {
                            System.out.println("走不走");
                            chainAnnotation(o, obj);
                        }
                    }
                });
    }

    private void chainAnnotation(Object o, Object data) {
        Method[] methods = o.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(RegisterBus.class)) {
                Class<?> type = method.getParameterTypes()[0];
                if (type.getSimpleName().equals(data.getClass().getSimpleName())) {
                    try {
                        System.out.println("走不走");
                        method.invoke(o, new Object[]{data});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}

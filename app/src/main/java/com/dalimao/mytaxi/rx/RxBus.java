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
                        sendData(obj);
                    }
                });
    }

    private void chainAnnotation(Object o, Object data) {
        if (data == null) return;
        Method[] methodArray = o.getClass().getDeclaredMethods();
        Method method;
        for (int i = 0; i < methodArray.length; i++) {
            method = methodArray[i];
            try {
                if (methodArray[i].isAnnotationPresent(RegisterBus.class)) {
                    // 被 @RegisterBus 修饰的方法
                    Class paramType = method.getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())) {
                        // 参数类型和 data 一样，调用此方法
                        methodArray[i].invoke(o, new Object[]{data});
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendData(Object obj) {
        if (set == null) return;
        for (Object o : set) {
            //System.out.println("走不走");
            chainAnnotation(o, obj);
        }
    }
}

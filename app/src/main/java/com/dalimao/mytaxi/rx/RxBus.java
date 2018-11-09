package com.dalimao.mytaxi.rx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArraySet;

public class RxBus {
    private static RxBus sRxBus;
    private CopyOnWriteArraySet<BaseDataEvent> set;

    private RxBus() {
        set = new CopyOnWriteArraySet<>();

    }

    public void register(BaseDataEvent event) {
        set.add(event);
    }

    public void unRegister(BaseDataEvent event) {
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

    public void chainProcess(Object obj) throws InvocationTargetException, IllegalAccessException {
        for (Object o : set) {
            Method[] methods = o.getClass().getDeclaredMethods();
            for (Method m : methods) {
                m.setAccessible(true);
                if (m.isAnnotationPresent(RegisterBus.class)) {
                    Class<?> mClass = m.getParameterTypes()[0];//获取方法参数类型
                    if (mClass.getSimpleName().equals(obj.getClass().getSimpleName())) {
                        m.invoke(o, new Object[]{obj});//调用这个方法
                    }
                }
            }
        }
    }
}

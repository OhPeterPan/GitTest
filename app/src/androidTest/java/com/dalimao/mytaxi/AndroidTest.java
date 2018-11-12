package com.dalimao.mytaxi;

import android.util.Log;

import com.dalimao.mytaxi.rx.RegisterBus;
import com.dalimao.mytaxi.rx.RxBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.functions.Function;

public class AndroidTest {
    Presenter presenter;

    @Before
    public void before() {
        presenter = new Presenter(new Manager());
        RxBus.getInstance().register(presenter);
    }

    @After
    public void after() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        RxBus.getInstance().unRegister(presenter);
    }

    @Test
    public void getUser() {
        presenter.getUser();
    }

    class Presenter {
        Manager manager;

        public Presenter(Manager manager) {
            this.manager = manager;
        }

        public void getUser() {
            manager.getUser();
        }

        @RegisterBus
        public void onUser(User user) {

            Log.d("wak", "user:" + user.userName + "," + user.userId);
        }

    }

    class Manager {
        void getUser() {
            RxBus.getInstance().chainProcess(new Function() {
                @Override
                public Object apply(Object o) throws Exception {
                    System.out.println("你好");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    User user = new User();
                    user.userName = "你好吗？";
                    user.userId = "1";
                    return user;
                }
            });
        }
    }

    class User {
        String userName;
        String userId;
    }
}

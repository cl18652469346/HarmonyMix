package com.hoperun.mycard;

import com.hoperun.mycard.slice.CardAbilitySlice;
import com.hoperun.mycard.widget.controller.FormController;
import com.hoperun.mycard.widget.controller.FormControllerManager;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.rpc.IRemoteObject;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CardService extends Ability {
    private Timer timer;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        startTimer();
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestKid();
            }
        }, 0, 6 * 1000);
    }

    private void requestKid() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OpenApiService.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(OpenApiService.class)
                .getKid(Contants.API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Kid>() {
                    @Override
                    public void onCompleted() {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                new ToastDialog(CardService.this).setText("onCompleted").show();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                new ToastDialog(CardService.this).setText(throwable.getMessage()).show();
                                LogUtils.info("chenle", "kid throwable = " + throwable.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onNext(Kid kid) {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                new ToastDialog(CardService.this).setText(kid.getNewslist().get(0).getQuest()).show();
                                LogUtils.info("chenle", "kid QA = " + kid.getNewslist().get(0).getQuest());
                                LogUtils.info("chenle", "kid Result = " + kid.getNewslist().get(0).getResult());
                                updateForms(kid);
                            }
                        });
                    }
                });
    }

    /**
     * 每间隔6s,请求一遍接口,并更新卡片
     */
    private void updateForms(Kid kid) {
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        List<Long> formIds = formControllerManager.getAllFormIdFromSharePreference();
        if (formIds == null || formIds.size() == 0) {
            LogUtils.error("chenle","updateForms no formIds -> return");
            return;
        }
        LogUtils.error("chenle","updateForms >>> ");
        for (Long formId : formIds) {
            FormController formController = formControllerManager.getController(formId);
            formController.updateFormData(formId, this, kid);
        }
    }


    @Override
    public void onBackground() {
        super.onBackground();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return null;
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}
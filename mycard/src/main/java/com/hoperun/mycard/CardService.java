package com.hoperun.mycard;

import com.hoperun.mycard.slice.CardAbilitySlice;
import com.hoperun.mycard.widget.controller.FormController;
import com.hoperun.mycard.widget.controller.FormControllerManager;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.event.commonevent.*;
import ohos.event.notification.NotificationRequest;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CardService extends Ability {
    private static final String TAG = "CardService";
    private Timer timer;

    private MyCommonEventSubscriber subscriber;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        LogUtils.error(TAG, "onStart >>> ");

        // 创建通知，其中1005为notificationId
        NotificationRequest request = new NotificationRequest(1005);
        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setTitle("CardService").setText("Keep Running");
        NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        request.setContent(notificationContent);
        // 绑定通知，1005为创建通知时传入的notificationId
        keepBackgroundRunning(1005, request);

        subscribe();

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
                LogUtils.error(TAG, "timer 6s >>> ");
                if (hasFrom()) {
                    requestKid();
//                    updateForms(null);
                } else {
                    LogUtils.error(TAG, "timer noFrom, Stop");
                    timer.cancel();
                    terminateAbility();
                }
            }
        }, 0, 3 * 60 * 1000);
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
//                                new ToastDialog(CardService.this).setText("onCompleted").show();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
//                                new ToastDialog(CardService.this).setText(throwable.getMessage()).show();
                                LogUtils.info(TAG, "kid throwable = " + throwable.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onNext(Kid kid) {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
//                                new ToastDialog(CardService.this).setText(kid.getNewslist().get(0).getQuest()).show();
                                LogUtils.info(TAG, "kid QA = " + kid.getNewslist().get(0).getQuest());
                                LogUtils.info(TAG, "kid Result = " + kid.getNewslist().get(0).getResult());
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
            LogUtils.error(TAG, "updateForms no formIds -> return");
            return;
        }
        LogUtils.error(TAG, "updateForms >>> ");
        for (Long formId : formIds) {
            FormController formController = formControllerManager.getController(formId);
            formController.updateFormData(formId, kid);
        }
    }

    private boolean hasFrom() {
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        List<Long> formIds = formControllerManager.getAllFormIdFromSharePreference();
        if (formIds == null || formIds.size() == 0) {
            LogUtils.error(TAG, "updateForms no formIds -> return");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackground() {
        super.onBackground();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.error(TAG, "onStop >>> ");
        unSubcribe();
        cancelBackgroundRunning();
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
        LogUtils.error(TAG, "onCommand >>> ");
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return null;
    }

    @Override
    public void onDisconnect(Intent intent) {
    }


    class MyCommonEventSubscriber extends CommonEventSubscriber {

        public MyCommonEventSubscriber(CommonEventSubscribeInfo subscribeInfo) {
            super(subscribeInfo);
        }

        @Override
        public void onReceiveEvent(CommonEventData commonEventData) {
            LogUtils.info(TAG, "onReceiveEvent ");
            requestKid();
//            updateForms(null);
        }
    }

    private void subscribe() {
        try {
            MatchingSkills skills = new MatchingSkills();
            skills.addEvent(Contants.REFRESH_CARD);
            skills.addEvent(CommonEventSupport.COMMON_EVENT_SCREEN_ON); // 亮屏事件

            CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(skills);

            subscriber = new MyCommonEventSubscriber(subscribeInfo);

            CommonEventManager.subscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtils.info(TAG, "subscribe RemoteException");
        }
    }

    private void unSubcribe() {
        try {
            CommonEventManager.unsubscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtils.info(TAG, "Exception occurred during unsubscribeCommonEvent invocation.");
        }
    }
}
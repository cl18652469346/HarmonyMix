package com.hoperun.mycard.slice;

import com.hoperun.mycard.*;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ToastDialog;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.schedulers.Schedulers;

public class CardAbilitySlice extends AbilitySlice {
    private Text text;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_card);

        text = (Text) findComponentById(ResourceTable.Id_text_helloworld);

        text.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                requestKid();
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
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
                                new ToastDialog(CardAbilitySlice.this).setText("onCompleted").show();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(throwable.getMessage());
//                                new ToastDialog(CardAbilitySlice.this).setText(throwable.getMessage()).show();
//                                LogUtils.info("chenle", "kid throwable = " + throwable.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onNext(Kid kid) {
                        getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(kid.getNewslist().get(0).getQuest() + "  " + kid.getNewslist().get(0).getResult());
//                                new ToastDialog(CardAbilitySlice.this).setText(kid.getNewslist().get(0).getQuest()).show();
//                                LogUtils.info("chenle", "kid QA = " + kid.getNewslist().get(0).getQuest());
//                                LogUtils.info("chenle", "kid Result = " + kid.getNewslist().get(0).getResult());
                            }
                        });
                    }
                });
    }
}

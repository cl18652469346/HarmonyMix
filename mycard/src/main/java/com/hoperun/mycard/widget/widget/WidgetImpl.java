package com.hoperun.mycard.widget.widget;

import com.hoperun.mycard.Contants;
import com.hoperun.mycard.Kid;
import com.hoperun.mycard.LogUtils;
import com.hoperun.mycard.ResourceTable;
import com.hoperun.mycard.widget.controller.FormController;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.ComponentProvider;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.event.intentagent.IntentAgent;
import ohos.event.intentagent.IntentAgentConstant;
import ohos.event.intentagent.IntentAgentHelper;
import ohos.event.intentagent.IntentAgentInfo;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.text.SimpleDateFormat;
import java.util.*;

public class WidgetImpl extends FormController {
    private static final String TAG = "CardService";
    private static final int DEFAULT_DIMENSION_2X2 = 2;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();
    private ProviderFormInfo formInfo;

    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X2, ResourceTable.Layout_form_image_with_information_widget_2_2);
    }

    public WidgetImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        LogUtils.info(TAG, "bind form data when create form");

        formInfo = new ProviderFormInfo(RESOURCE_ID_MAP.get(dimension), context);

        ComponentProvider componentProvider = new ComponentProvider(RESOURCE_ID_MAP.get(dimension), context);
//        componentProvider.setText(ResourceTable.Id_question, "bindFormData  bindFormData  bindFormData");
        componentProvider.setIntentAgent(ResourceTable.Id_refresh, pushRefreshIntentAgent());

        formInfo.mergeActions(componentProvider);
        return formInfo;
    }

    @Override
    public void updateFormData(long formId, Kid kid) {
        LogUtils.info(TAG, "update form data timing, default 30 minutes");
        ComponentProvider componentProvider = new ComponentProvider(RESOURCE_ID_MAP.get(dimension), context);
        String time = format.format(new Date());
        if (kid != null) {
            componentProvider.setText(ResourceTable.Id_question, kid.getNewslist().get(0).getQuest());
            componentProvider.setText(ResourceTable.Id_result, kid.getNewslist().get(0).getResult());
        } else {
            componentProvider.setText(ResourceTable.Id_question, time);
            componentProvider.setText(ResourceTable.Id_result, time + "?");
        }
        try {
            boolean isSuccess = ((Ability) context).updateForm(formId, componentProvider);
        } catch (Exception e) {
            LogUtils.error(TAG, "updateForms Exception e = " + e.getMessage());
        }
    }

    @Override
    public void onTriggerFormEvent(long formId, String message) {
        LogUtils.info(TAG, "handle card click event.");
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        LogUtils.info(TAG, "get the default page to route when you click card.");
        return null;
    }


    private IntentAgent pushRefreshIntentAgent() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withAction(Contants.REFRESH_CARD)
                .build();
        intent.setOperation(operation);
        List<Intent> intentList = new ArrayList<>();
        intentList.add(intent);
        List<IntentAgentConstant.Flags> flags = new ArrayList<>();
        flags.add(IntentAgentConstant.Flags.UPDATE_PRESENT_FLAG);
        IntentAgentInfo paramsInfo = new IntentAgentInfo(200, IntentAgentConstant.OperationType.SEND_COMMON_EVENT, flags, intentList, null);
        IntentAgent intentAgent = IntentAgentHelper.getIntentAgent(context, paramsInfo);
        LogUtils.info(TAG, "pushRefreshIntentAgent");
        return intentAgent;
    }
}
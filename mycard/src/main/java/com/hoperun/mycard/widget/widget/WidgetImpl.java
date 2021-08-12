package com.hoperun.mycard.widget.widget;

import com.hoperun.mycard.Kid;
import com.hoperun.mycard.ResourceTable;
import com.hoperun.mycard.widget.controller.FormController;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentProvider;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.HashMap;
import java.util.Map;

public class WidgetImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, WidgetImpl.class.getName());
    private static final int DEFAULT_DIMENSION_2X2 = 2;


    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();

    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X2, ResourceTable.Layout_form_image_with_information_widget_2_2);
    }

    public WidgetImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.info(TAG, "bind form data when create form");
        ComponentProvider componentProvider = new ComponentProvider(RESOURCE_ID_MAP.get(dimension), context);
        ProviderFormInfo formInfo = new ProviderFormInfo();
        formInfo.mergeActions(componentProvider);
        return formInfo;
    }

    @Override
    public void updateFormData(long formId, Context context1, Kid kid) {
        HiLog.info(TAG, "update form data timing, default 30 minutes");
        ComponentProvider componentProvider = new ComponentProvider(RESOURCE_ID_MAP.get(dimension), context);
        componentProvider.setText(ResourceTable.Id_question, kid.getNewslist().get(0).getQuest());
        componentProvider.setText(ResourceTable.Id_result, kid.getNewslist().get(0).getResult());
        try {
            boolean isSuccess = ((Ability) context).updateForm(formId, componentProvider);
            new ToastDialog(context).setText("isSuccess = " + isSuccess).show();
        } catch (Exception e) {
            new ToastDialog(context).setText(e.getMessage()).show();
        }
    }

    @Override
    public void onTriggerFormEvent(long formId, String message) {
        HiLog.info(TAG, "handle card click event.");
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        HiLog.info(TAG, "get the default page to route when you click card.");
        return null;
    }
}
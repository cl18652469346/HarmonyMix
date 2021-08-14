package com.hoperun.mycard;

import com.hoperun.mycard.slice.CardAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import com.hoperun.mycard.widget.controller.FormController;
import com.hoperun.mycard.widget.controller.FormControllerManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.bundle.ElementName;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class CardAbility extends Ability {
    public static final int DEFAULT_DIMENSION_2X2 = 2;
    private static final int INVALID_FORM_ID = -1;
    private static final String TAG = "CardService";
    private String topWidgetSlice;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(CardAbilitySlice.class.getName());
        if (intentFromWidget(intent)) {
            topWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice != null) {
                setMainRoute(topWidgetSlice);
            }
        }
        stopAbility(intent);
    }

    @Override
    protected ProviderFormInfo onCreateForm(Intent intent) {
        startService();
        LogUtils.error(TAG, "onCreateForm");
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        String formName = intent.getStringParam(AbilitySlice.PARAM_FORM_NAME_KEY);
        int dimension = intent.getIntParam(AbilitySlice.PARAM_FORM_DIMENSION_KEY, DEFAULT_DIMENSION_2X2);
        LogUtils.error(TAG, "onCreateForm: formId=" + formId + ",formName=" + formName);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        formController = (formController == null) ? formControllerManager.createFormController(formId,
                formName, dimension) : formController;
        if (formController == null) {
            LogUtils.error(TAG, "Get null controller. formId: " + formId + ", formName: " + formName);
            return null;
        }
        return formController.bindFormData();
    }

    @Override
    protected void onUpdateForm(long formId) {
        LogUtils.error(TAG, "onUpdateForm");
        super.onUpdateForm(formId);
//        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
//        FormController formController = formControllerManager.getController(formId);
//        formController.updateFormData(formId, this, new Kid());
    }

    @Override
    protected void onDeleteForm(long formId) {
        LogUtils.error(TAG, "onDeleteForm: formId=" + formId);
        super.onDeleteForm(formId);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        formControllerManager.deleteFormController(formId);
    }

    @Override
    protected void onTriggerFormEvent(long formId, String message) {
        LogUtils.error(TAG, "onTriggerFormEvent: " + message);
        super.onTriggerFormEvent(formId, message);
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        formController.onTriggerFormEvent(formId, message);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intentFromWidget(intent)) { // Only response to it when starting from a service widget.
            String newWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice == null || !topWidgetSlice.equals(newWidgetSlice)) {
                topWidgetSlice = newWidgetSlice;
                restart();
            }
        }
    }

    private boolean intentFromWidget(Intent intent) {
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        return formId != INVALID_FORM_ID;
    }

    private String getRoutePageSlice(Intent intent) {
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        if (formId == INVALID_FORM_ID) {
            return null;
        }
        FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        if (formController == null) {
            return null;
        }
        Class<? extends AbilitySlice> clazz = formController.getRoutePageSlice(intent);
        if (clazz == null) {
            return null;
        }
        return clazz.getName();
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setElement(new ElementName(
                "",
                getBundleName(),
                "com.hoperun.mycard.CardService"
        ));
        startAbility(intent);
    }
}

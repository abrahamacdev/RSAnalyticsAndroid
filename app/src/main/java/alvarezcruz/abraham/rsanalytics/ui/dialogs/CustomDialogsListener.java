package alvarezcruz.abraham.rsanalytics.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;

import io.reactivex.rxjava3.functions.Consumer;

public class CustomDialogsListener implements Dialog.OnCancelListener, Dialog.OnDismissListener {

    private Consumer<DialogInterface> onCancel;
    private Consumer<DialogInterface> onDismiss;

    private boolean soloPrimero = false;


    private boolean _yaAvisado = false;

    public CustomDialogsListener(){}

    public CustomDialogsListener(Consumer<DialogInterface> onCancel, Consumer<DialogInterface> onDismiss) {
        this.onCancel = onCancel;
        this.onDismiss = onDismiss;
    }

    public void avisarSoloPrimero(){
        soloPrimero = true;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (onCancel != null){
            try {

                if (soloPrimero){
                    if (!_yaAvisado){
                        onCancel.accept(dialog);
                        _yaAvisado = true;
                    }
                }
                else {
                    onCancel(dialog);
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onDismiss != null){
            try {

                if (soloPrimero){
                    if (!_yaAvisado){
                        onDismiss.accept(dialog);
                        _yaAvisado = true;
                    }
                }
                else {
                    onDismiss(dialog);
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public void setOnCancelConsumer(Consumer<DialogInterface> onCancel) {
        this.onCancel = onCancel;
    }

    public void setOnDismissConsumer(Consumer<DialogInterface> onDismiss) {
        this.onDismiss = onDismiss;
    }
}

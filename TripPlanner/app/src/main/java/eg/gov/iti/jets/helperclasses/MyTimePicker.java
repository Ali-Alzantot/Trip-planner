package eg.gov.iti.jets.helperclasses;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import eg.gov.iti.jets.helperinterfaces.MyDialogInterface;

import java.util.Calendar;

/**
 * Created by esraa on 3/8/2018.
 */

public class MyTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener  {

    private MyDialogInterface myDialogInterface;

    public MyTimePicker() {
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

       Calendar calendar=Calendar.getInstance();
        int hourOfDay=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getContext(),this,hourOfDay,min, DateFormat.is24HourFormat(getContext()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myDialogInterface=(MyDialogInterface)getArguments().getSerializable("myDialogInterface");
        }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
       myDialogInterface.setTimeString(hourOfDay,minute);
    }


}

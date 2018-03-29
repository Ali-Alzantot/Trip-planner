package eg.gov.iti.jets.helperclasses;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import eg.gov.iti.jets.helperinterfaces.MyDialogInterface;

import java.util.Calendar;

/**
 * Created by esraa on 3/8/2018.
 */

public class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private MyDialogInterface myDialogInterface;

    public MyDatePicker() {
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

       Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog= new DatePickerDialog(getContext(),this,year,month,dayOfMonth);
        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        return dialog;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myDialogInterface=(MyDialogInterface)getArguments().getSerializable("myDialogInterface");
       }




    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
         month=month+1;
         myDialogInterface.setDateString(dayOfMonth,month,year);//int: the selected month (0-11 for compatibility with MONTH)
    }
}

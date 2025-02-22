package apcoders.in.krushitech.utils;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class BasicUtils {
    public interface DatePickupCallback {
        void onDateSelected(Date date);
    }

    public BasicUtils() {

    }

    public static boolean isProductAvailable(Date availableFromDate, Date availableToDate) {
        Date today = new Date();  // Get today's date

        // Check if today's date is within the available range
        return !today.before(availableFromDate) && !today.after(availableToDate);
    }

    public static void showDateSelectionDialog(Context context, DatePickupCallback datePickupCallback) {
        final Date[] fromDate = {null};
        final Date[] toDate = {null};

        // Show "From" Date Picker
        DatePickerDialog fromDatePicker = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    fromDate[0] = calendar.getTime();
                    if(fromDate[0].before(new Date())){
                       datePickupCallback.onDateSelected(null);
                       return;
                    }
                    datePickupCallback.onDateSelected(fromDate[0]);
                    // After selecting the "From" date, show "To" Date Picker
//                    showToDatePicker(fromDate[0], context);
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        fromDatePicker.setTitle("Select From Date");
        fromDatePicker.show();
    }

    public static void showToDatePicker(Context context, DatePickupCallback datePickupCallback) {
        DatePickerDialog toDatePicker = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    Date toDate = calendar.getTime();
                    if(toDate.before(new Date())){
                        datePickupCallback.onDateSelected(null);
                        return;
                    }
                    datePickupCallback.onDateSelected(toDate);
                    // Validate and process the selected dates
//                    processSelectedDates(fromDate, toDate, context);
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        toDatePicker.setTitle("Select To Date");
        toDatePicker.show();
    }
}

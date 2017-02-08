package com.example.razvan.socialeventshelper.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Razvan on 2/8/2017.
 */

public class GeneralUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getMonthNameFromNumber(String month){
        String monthString;
        switch (month) {
            case "01":  monthString = "JAN";        break;
            case "02":  monthString = "FEB";        break;
            case "03":  monthString = "MAR";        break;
            case "04":  monthString = "APR";        break;
            case "05":  monthString = "MAY";        break;
            case "06":  monthString = "JUNE";       break;
            case "07":  monthString = "JULY";       break;
            case "08":  monthString = "AUG";        break;
            case "09":  monthString = "SEPT";       break;
            case "10": monthString = "OCT";         break;
            case "11": monthString = "NOV";         break;
            case "12": monthString = "DEC";         break;
            default: monthString = "Invalid month"; break;
        }
        return monthString;
    }
}

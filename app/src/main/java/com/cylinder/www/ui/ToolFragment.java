package com.cylinder.www.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cylinder.www.env.Signal;

/**
 * Created by xiutao on 2014/9/19.
 */
public class ToolFragment extends Fragment implements View.OnClickListener {

    private OnToolBarOnClickListener onToolBarOnClickListener;


    @Override
    public void onClick(View v) {

    }

    public interface OnToolBarOnClickListener {
        public void onButtonSelected(Signal s);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool_bar, container, false);
        InitialButton(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    void InitialButton(View view) {

    }
    //士大夫

    private void showMakeSureDialog(final Thread thread, Activity activity, String str) {
        //AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
        AlertDialog.Builder normalDia = new AlertDialog.Builder(activity);
        normalDia.setIcon(R.drawable.ic_launcher);
        normalDia.setTitle(str);
        normalDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                thread.start();
            }
        });
        normalDia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        normalDia.create().show();
    }
}

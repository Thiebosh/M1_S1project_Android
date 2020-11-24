package fr.yncrea.m1_s1project_android.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;

public class MainBoardFragment extends Fragment implements BluetoothChildren {

    /*
     * Section BluetoothChildren
     */

    @Override
    public void retrieveData(final Object data) {
        Toast.makeText(getContext(), data+" from mainboard", Toast.LENGTH_SHORT).show();
        ((BluetoothParent) getActivity()).getGenerator();
    }


    /*
     * Section Menu
     */

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //définit éléments visibles du menu pour ce fragment
        menu.findItem(R.id.menu_disconnect).setVisible(true);
        menu.findItem(R.id.menu_toMainBoard).setVisible(false);
        menu.findItem(R.id.menu_toBackup).setVisible(true);
    }


    /*
     * Section Cycle de vie
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_board, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu
        Button input0 = view.findViewById(R.id.input0);
        input0.setOnClickListener(v -> {
            Drawable background = input0.getBackground();
            int color = Color.TRANSPARENT;
            if(background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            Toast.makeText(getContext(), "Bouton cliqué : input0" + color, Toast.LENGTH_SHORT).show();
            input0.setBackgroundColor(Color.GREEN);
        });
        RadioGroup radioInputs;
        radioInputs = (RadioGroup) view.findViewById(R.id.selectInput);
        radioInputs.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.selectInput0 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input0);
                    break;
                case R.id.selectInput1 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input1);
                    break;
                case R.id.selectInput2 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input2);
                    break;
                case R.id.selectInput3 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input3);
                    break;
                case R.id.selectInput4 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input4);
                    break;
                case R.id.selectInput5 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input5);
                    break;
                case R.id.selectInput6 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input6);
                    break;
                case R.id.selectInput7 : ((TextView) view.findViewById(R.id.selectedInput)).setText(R.string.input7);
                    break;
            }
        });

        view.findViewById(R.id.switch1).setOnClickListener(v -> {
            ((BluetoothParent) getActivity()).sendData("get");
        });

        return view;
    }



    /*public void onInputSelected(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()){
            case R.id.input0 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 0");
                break;
            case R.id.input1 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 1");
                break;
            case R.id.input2 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 2");
                break;
            case R.id.input3 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 3");
                break;
            case R.id.input4 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 4");
                break;
            case R.id.input5 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 5");
                break;
            case R.id.input6 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 6");
                break;
            case R.id.input7 :
                if(checked)
                    ((TextView) view.findViewById(R.id.selectedInput)).setText("Input 7");
                break;
        }
    }*/
}

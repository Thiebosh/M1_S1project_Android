package fr.yncrea.m1_s1project_android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.Objects;

import fr.yncrea.m1_s1project_android.R;
import fr.yncrea.m1_s1project_android.RecyclerView.MainBoardAdapterView;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothChildren;
import fr.yncrea.m1_s1project_android.interfaces.BluetoothParent;
import fr.yncrea.m1_s1project_android.models.Channel;
import fr.yncrea.m1_s1project_android.models.Generator;
import fr.yncrea.m1_s1project_android.models.PowerSupply;

import static fr.yncrea.m1_s1project_android.models.PowerSupply.I;
import static fr.yncrea.m1_s1project_android.models.PowerSupply.V;

public class MainBoardFragment extends Fragment implements BluetoothChildren {

    private MainBoardAdapterView mAdapter;

    /*
     * Section BluetoothChildren
     */

    @Override
    public void applyChanges(Generator generator, int index) {
        mAdapter.updateChannelList(generator.getChannelList(), index);
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

        Activity activity = Objects.requireNonNull(getActivity());

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.mainboard_recycler);
        mAdapter = new MainBoardAdapterView(getContext(),
                ((BluetoothParent) activity).getGenerator().getChannelList());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



        /*Button input0 = view.findViewById(R.id.activation);
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
        */
        view.findViewById(R.id.switch1).setOnClickListener(v -> {

            ((BluetoothParent) activity).getGenerator().getChannelList().get(7).setCurrentValue(2.4);

            Channel tmp = new Channel();
            tmp.setId(7);
            tmp.setCurrentValue(2.4);
            ((BluetoothParent) activity).sendData(tmp);
        });

/*        ((EditText) view.findViewById(R.id.value)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double edittext = Double.parseDouble(s.toString());
                Log.d("MainBoardClick", "edit input0 value" + edittext);
                ((BluetoothParent) Objects.requireNonNull(getActivity())).getGenerator()
                        .getChannelList().get(0).setCurrentValue(edittext);

                Channel tmp = new Channel();
                tmp.setId(0);
                tmp.setCurrentValue(edittext);
                ((BluetoothParent) Objects.requireNonNull(getActivity())).sendData(tmp);

            }
        });

*/

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

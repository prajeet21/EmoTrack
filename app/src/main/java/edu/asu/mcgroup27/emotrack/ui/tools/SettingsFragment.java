package edu.asu.mcgroup27.emotrack.ui.tools;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.asu.mcgroup27.emotrack.R;
import edu.asu.mcgroup27.emotrack.Util;
import edu.asu.mcgroup27.emotrack.database.FirebaseDBHelper;
import edu.asu.mcgroup27.emotrack.database.UserMetaData;
import edu.asu.mcgroup27.emotrack.database.UserMetaDataListener;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SettingsFragment extends Fragment {

    private SettingsViewModel toolsViewModel;
    Dialog addTwitterDialog;
    private DatabaseReference dbtwitteridref;
    String twitter_username;
    String usernameEditText;
    EditText editTextTwitter;
    protected TextView twitterTextView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        twitterTextView = root.findViewById(R.id.usernameTextView);

        //biometric feature setting
        final Switch bioSetting = root.findViewById(R.id.biometric_setting);
        bioSetting.setChecked(Util.getBiometric(getContext()) == 1 ? true:false);
        bioSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Util.setBiometric(getContext(), 1);
                } else {
                    Util.setBiometric(getContext(), 0);
                }
            }
        });


        //get twitter username

        dbtwitteridref = FirebaseDBHelper.getUserMetaDataRef().child("twitterID");

        dbtwitteridref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                twitter_username = dataSnapshot.getValue().toString();
                if(twitter_username != null) {
                    twitterTextView.setText(twitter_username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // click on add/edit button


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Button add_btn = getActivity().findViewById(R.id.add_edit_button);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTwitterDialog = onCreateDialog();
                addTwitterDialog.show();
            }
        });

    }

    public Dialog onCreateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_twitter_username, null))
                // Add action buttons
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

//                        Map<String,Object> taskMap = new HashMap<String,Object>();
//                        taskMap.put("twitterid", "test");
//                        dbtwitteridref.updateChildren(taskMap);
                        editTextTwitter = addTwitterDialog.findViewById(R.id.usernameEditText);
                        usernameEditText = editTextTwitter.getText().toString();
                        dbtwitteridref.setValue(usernameEditText);
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
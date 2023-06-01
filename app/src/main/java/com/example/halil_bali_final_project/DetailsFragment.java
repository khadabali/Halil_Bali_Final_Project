package com.example.halil_bali_final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TextView date;
        TextView url;
        TextView title;
        Button goBack;

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_details_fragment,
                container, false);


        goBack = view.findViewById(R.id.detailsButton);
        goBack.setOnClickListener(click -> {
            Intent dateIntent = new Intent(getActivity(), DatePicker.class);
            startActivity(dateIntent);
        });

        // initialize the textview by id
        date = view.findViewById(R.id.fragmentDate);
        url = view.findViewById(R.id.fragmentUrl);
        title = view.findViewById(R.id.fragmentTitle);

        // receives the information passed through the bundle
        Bundle b = getArguments();
        String imageDate = b.getString("DATE");
        String imageUrl = b.getString("URL");
        String imageTitle = b.getString("TITLE");

        // sets the information to the text areas
        date.setText(imageDate);
        title.setText(imageTitle);

        // identifying the url
        SpannableString spannableString = new SpannableString(imageUrl);
        // method to open the browser and travel to the image url
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        // makes the url clickable
        spannableString.setSpan(clickableSpan, 0, imageUrl.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        url.setText(spannableString);
        url.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }


}
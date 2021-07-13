package com.example.facerecogapp.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facerecogapp.API.TeacherAPI;
import com.example.facerecogapp.Activity.MainActivity;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.Model.Teacher;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    private Teacher teacher;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView userName;
    private TextView email;
    private TextView phone;
    private TextView department;
    private TextView password;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        initializeUI(root);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString(getString(R.string.email), "0");
        getTeacherInfo(new ResultCallBack<Teacher>() {
            @Override
            public void onSuccess(ArrayList<Teacher> teacherArrayList) {
                Teacher teacher = teacherArrayList.get(0);
                if(teacher != null){
                    userName.setText(teacher.getName());
                    email.setText(teacher.getEmail());
                    phone.setText(teacher.getPhoneNumber());
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, userEmail);
        return root;
    }

    private void initializeUI(View root) {
        userName = root.findViewById(R.id.account_fragment_name_text_view);
        email = root.findViewById(R.id.account_fragment_email_text_view);
        password = root.findViewById(R.id.account_fragment_password_text_view);
        phone = root.findViewById(R.id.account_fragment_phone_text_view);
        department = root.findViewById(R.id.account_fragment_department_text_view);
    }

    private void getTeacherInfo(ResultCallBack callBack, String email) {
        try{
            TeacherAPI teacherAPI = ServiceGenerator.createService(TeacherAPI.class);
            Call<Teacher> call = teacherAPI.getTeacher(email);
            call.enqueue(new Callback<Teacher>() {
                @Override
                public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                    if(response.code() == 401){
                        Toast.makeText(getActivity(), "Hết phiên đăng nhập", Toast.LENGTH_SHORT).show();
                    }else{
                        teacher = response.body();
                        ArrayList<Teacher> teacherArrayList = new ArrayList<Teacher>();
                        teacherArrayList.add(teacher);
                        callBack.onSuccess(teacherArrayList);
                    }
                }

                @Override
                public void onFailure(Call<Teacher> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
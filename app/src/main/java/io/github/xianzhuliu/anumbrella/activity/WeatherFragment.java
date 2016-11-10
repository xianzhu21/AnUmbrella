package io.github.xianzhuliu.anumbrella.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.City;
import io.github.xianzhuliu.anumbrella.model.Weather;
import io.github.xianzhuliu.anumbrella.util.WeatherCode;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    public static final String ARG_MYCITYID = "mycity_id";
    private static final String TAG = "WeatherFragment";
    private LinearLayout mWeatherInfoLayout;
    private TextView mPublishText;
    private TextView mWeatherDespText;
    private TextView mTempText;
    private TextView mCurrentDateText;
    private ImageView mImgWeather;
    private Weather mWeather;
    private int mMyCityId;
    private String mCityCode;
    private String mCityName;
    private AnUmbrellaDB mAnUmbrellaDB;

    private OnFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance(int myCityId) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MYCITYID, myCityId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMyCityId = getArguments().getInt(ARG_MYCITYID);

            mAnUmbrellaDB = AnUmbrellaDB.getInstance(getActivity().getApplicationContext());
            int cityId = mAnUmbrellaDB.findMyCityById(mMyCityId).getCityId();
            City city = mAnUmbrellaDB.findCityById(cityId);
            mCityCode = city.getCityCode();
            mCityName = city.getCountyName();


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_weather, container, false);
        initView(view);
        Gson gson = new Gson();
        mWeather = gson.fromJson(mAnUmbrellaDB.findMyCityById(mMyCityId).getWeather(), Weather.class);
        if (mWeather != null) {
            showWeather();
        } else {
            mCurrentDateText.setText("请检查网络后刷新一下吧 +__+");
        }
        return view;
    }

    private void initView(View view) {
        mWeatherInfoLayout = (LinearLayout) view.findViewById(R.id.weather_info_layout);
        mPublishText = (TextView) view.findViewById(R.id.publish_text);
        mWeatherDespText = (TextView) view.findViewById(R.id.weather_desp);
        mTempText = (TextView) view.findViewById(R.id.temp);
        mCurrentDateText = (TextView) view.findViewById(R.id.current_date);
        mImgWeather = (ImageView) view.findViewById(R.id.img_weather);
    }

    private void showWeather() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日");
        mTempText.setText(mWeather.daily_forecast.get(0).tmp.min + "°C ~ " + mWeather.daily_forecast.get(0).tmp.max
                + "°C");
        mWeatherDespText.setText(mWeather.now.cond.txt + " " + mWeather.now.tmp + "°C");
        String publishTime = mWeather.basic.update.loc;
        mPublishText.setText(publishTime.substring(publishTime.length() - 11) + " 发布");
        mImgWeather.setImageResource(WeatherCode.getWeatherCode(Integer.parseInt(mWeather.now.cond.code)));
        mCurrentDateText.setText(simpleDateFormat.format(new Date()));
        mWeatherInfoLayout.setVisibility(View.VISIBLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void updateWeather(int myCityId, String cityCode);
    }
}

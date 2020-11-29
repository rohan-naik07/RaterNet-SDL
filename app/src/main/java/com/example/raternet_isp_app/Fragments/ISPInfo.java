package com.example.raternet_isp_app.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raternet_isp_app.CreateDiscussionActivity;
import com.example.raternet_isp_app.models.Constants;
import com.example.raternet_isp_app.R;
import com.example.raternet_isp_app.SearchNetworkActivity;
import com.example.raternet_isp_app.endpoints.GetDataService;
import com.example.raternet_isp_app.motionlisteners.OnSwipeTouchListener;
import com.example.raternet_isp_app.network.RetrofitClientInstance;
import com.example.raternet_isp_app.network.RetrofitClientInstance2;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ISPInfo extends Fragment implements View.OnClickListener{

    private TextView ISPView;
    private ProgressDialog progressDialog;
    private String ip = null;
    private ViewGroup ispDetails;
    private TextView ispSpeedUp,organization,addressView;
    private ConnectivityManager cm;
    private Geocoder geocoder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.isp_view, container, false);
        ISPView = view.findViewById(R.id.ISP);
        return view;
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ispDetails = getView().findViewById(R.id.details);
        ispSpeedUp = ispDetails.findViewById(R.id.isp_speed_up);
        organization = ispDetails.findViewById(R.id.org);
        addressView = getView().findViewById(R.id.address);


        cm = (ConnectivityManager)  getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        try {
            ispSpeedUp.setText(getWifiLevel().toString() + " Mbps");
        } catch (Exception e){
            if(nc!=null){
                Integer upSpeed = nc.getLinkUpstreamBandwidthKbps();
                Double speed = (double) upSpeed * 0.0010;
                ispSpeedUp.setText(speed.toString() + " Mbps");
            }
        }


        fetchIPInfo();
        getView().setOnTouchListener(new OnSwipeTouchListener(getContext()){
            public void onSwipeTop() {
                ispDetails.setVisibility(View.VISIBLE);
            }
            public void onSwipeBottom() {
                ispDetails.setVisibility(View.GONE);
            }
        });
        getView().findViewById(R.id.btnSearchNetwork).setOnClickListener(this);
        getView().findViewById(R.id.Refresh).setOnClickListener(this);
        getView().findViewById(R.id.discuss).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //fetchIPInfo();
    }

    public void fetchIPInfo(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting your Network Info..."); // show progess dialog till server responds
        progressDialog.show();
        GetDataService serviceIP= RetrofitClientInstance.getRetrofitInstance()
                .create(GetDataService.class);
        Call<String> callIP = serviceIP.getIP();
        callIP.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    ip = response.body();
                    GetDataService serviceISP= RetrofitClientInstance2.getRetrofitInstance()
                            .create(GetDataService.class);
                    Call<JsonObject> callISP = serviceISP.getIPInfo(ip);
                    callISP.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            progressDialog.dismiss();
                            try{
                                JsonObject jsonObject = response.body();

                                //Setting ISP in Constants
                                Constants.ISP_Name=jsonObject.get("org").getAsString();
                                if(jsonObject.get("org").getAsString().equals("")){
                                    Constants.ISP_Name = "Hathway Cable and Datacom Pvt Ltd";
                                }
                                ISPView.setText(jsonObject.get("isp").getAsString());
                                organization.setText(jsonObject.get("as").getAsString());

                                if(Constants.address!=null){
                                    addressView.setText(Constants.address);
                                } else {
                                    addressView.setText("Getting address...");
                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                ISPView.setText("Json Error");
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("ISP Error",t.getMessage());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    ISPView.setText("IP Error");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("IP Error",t.getMessage());
            }
        });
    }

    public Integer getWifiLevel()
    {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(linkSpeed, 5);
        return level;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSearchNetwork:
                startActivity(new Intent(getContext(), SearchNetworkActivity.class));
                break;
            case R.id.Refresh :
                ispDetails.setVisibility(View.VISIBLE);
                break;
            case R.id.discuss:
                startActivity(new Intent(getContext(), CreateDiscussionActivity.class));
            default: break;
        }
    }
}
//Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
/*
RESPONSES:
* D/OkHttp: <-- 200 OK https://api.ipify.org/ (1156ms)
D/OkHttp: Server: Cowboy
    Connection: keep-alive
    Content-Type: text/plain
    Vary: Origin
    Date: Fri, 30 Oct 2020 07:49:25 GMT
    Content-Length: 13
    Via: 1.1 vegur
D/OkHttp: 116.74.187.92
    <-- END HTTP (13-byte body)
    *
    *  I/ISP Error: CLEARTEXT communication to ip-api.com not permitted by network security policy
    *
    * D/OkHttp: <-- 200 OK http://ip-api.com/json/116.74.187.92 (538ms)
D/OkHttp: Date: Fri, 30 Oct 2020 10:56:57 GMT
    Content-Type: application/json; charset=utf-8
D/OkHttp: Content-Length: 339
    Access-Control-Allow-Origin: *
    X-Ttl: 60
    X-Rl: 44
D/OkHttp: {"status":"success","country":"India","countryCode":"IN","region":"MH",
"regionName":"Maharashtra","city":"Pune","zip":"411038","lat":18.5196,"lon":73.8554,
"timezone":"Asia/Kolkata","isp":"Hathway IP over Cable Internet Access",
"org":"Hathway Cable and Datacom Pvt Ltd","as":"AS17488 Hathway IP Over Cable Internet",
"query":"116.74.187.92"}
D/OkHttp: <-- END HTTP (339-byte body)
* */
